package com.cisbox.quarkus.service;

import java.io.IOException;
import java.nio.file.Files;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.cisbox.quarkus.dao.CsvEntityPersister;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;

import java.nio.file.Path;

@ApplicationScoped 
public class BackupScheduler {

    @Inject
    CsvEntityPersister entityPersister;

    @Inject
    Mailer mailer;  

    @Scheduled(cron="0 0 2 * * ?")
    public void sendFilesAsBackup(){
        String email = ConfigProvider.getConfig().getValue("scoreboard.data.backup.email", String.class);
        try{
            mailer.send(
                Mail.withText(email, "scoreboard backup", "nobody")
                    .addAttachment("user.csv",Files.readAllBytes(Path.of(entityPersister.getUserFilePath())), "text/plain")
                    .addAttachment("game.csv",Files.readAllBytes(Path.of(entityPersister.getGameFilePath())), "text/plain")
                    .addAttachment("season.csv",Files.readAllBytes(Path.of(entityPersister.getSeasonFilePath())), "text/plain")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
