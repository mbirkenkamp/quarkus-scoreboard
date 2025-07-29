package com.cisbox.quarkus.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;

import com.cisbox.quarkus.entity.*;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Getter;

@ApplicationScoped
@RegisterForReflection
@Named("EntityPersister")
public class CsvEntityPersister implements EntityPersister{ 
    private static final String DATA_DIRECTORY_VAR = "scoreboard.data.directory";

    @Getter
    private final String userFilePath;
    @Getter
    private final String seasonFilePath;
    @Getter
    private final String gameFilePath;
    @Getter
    private final String departmentFilePath;
    @Getter
    private final String boardgameFilePath;
    @Getter
    private final String boardgameSessionFilePath;
    @Getter
    private final String boardgameSessionParticipantFilePath;

    private CsvEntityPersister(){
        Log.infof("Using Storage Path: %s", new File(".").getAbsolutePath());
        userFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/user.csv";
        seasonFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/season.csv";
        gameFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/game.csv";
        departmentFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/department.csv";
        boardgameFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/boardgame.csv";
        boardgameSessionFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/boardgame-session.csv";
        boardgameSessionParticipantFilePath = ConfigProvider.getConfig().getValue(DATA_DIRECTORY_VAR, String.class) + "/boardgame-session-participant.csv";
    }

    @CacheResult(cacheName = "user-cache")
    public List<User> readUsers(){
        try {
            return new CsvToBeanBuilder<User>(new FileReader(userFilePath)).withType(User.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "user-cache")
    public int writeUsers(Collection<User> userList){
        try {
            Writer writer = new FileWriter(userFilePath);
            StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(writer).build();
            beanToCsv.write(userList.iterator());        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "season-cache")
    public List<Season> readSeasons(){
        try {
                return new CsvToBeanBuilder<Season>(new FileReader(seasonFilePath)).withType(Season.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "season-cache")
    public int writeSeasons(List<Season> seasonList){
        try {
            
            Writer writer = new FileWriter(seasonFilePath);
            StatefulBeanToCsv<Season> beanToCsv = new StatefulBeanToCsvBuilder<Season>(writer).build();
            beanToCsv.write(seasonList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "game-cache")
    public List<Game> readGames(){
        try {
            return new CsvToBeanBuilder<Game>(new FileReader(gameFilePath)).withType(Game.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "game-cache")
    public int writeGames(List<Game> gameList){
        try {
            Writer writer = new FileWriter(gameFilePath);
            StatefulBeanToCsv<Game> beanToCsv = new StatefulBeanToCsvBuilder<Game>(writer).build();
            beanToCsv.write(gameList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "department-cache")
    @Override
    public List<Department> readDepartments() {
        try {
            return new CsvToBeanBuilder<Department>(new FileReader(gameFilePath)).withType(Department.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "department-cache")
    @Override
    public int writeDepartments(List<Department> departmentList) {
        try {
            Writer writer = new FileWriter(departmentFilePath);
            StatefulBeanToCsv<Department> beanToCsv = new StatefulBeanToCsvBuilder<Department>(writer).build();
            beanToCsv.write(departmentList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "boardgame-cache")
    @Override
    public List<Boardgame> readBoardgames() {
        try {
            return new CsvToBeanBuilder<Boardgame>(new FileReader(boardgameFilePath))
                    .withType(Boardgame.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "boardgame-cache")
    @Override
    public boolean writeBoardgames(List<Boardgame> boardgameList) {
        try {
            Writer writer = new FileWriter(boardgameFilePath);
            StatefulBeanToCsv<Boardgame> beanToCsv = new StatefulBeanToCsvBuilder<Boardgame>(writer).build();
            beanToCsv.write(boardgameList);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return false;
        }
        return true;
    }

    @CacheResult(cacheName = "boardgame-session-cache")
    @Override
    public List<BoardgameSession> readBoardgameSessions() {
        try {
            return new CsvToBeanBuilder<BoardgameSession>(new FileReader(boardgameSessionFilePath))
                    .withType(BoardgameSession.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "boardgame-session-cache")
    @Override
    public boolean writeBoardgameSessions(List<BoardgameSession> boardgameSessionList) {
        try {
            Writer writer = new FileWriter(boardgameSessionFilePath);
            StatefulBeanToCsv<BoardgameSession> beanToCsv = new StatefulBeanToCsvBuilder<BoardgameSession>(writer).build();
            beanToCsv.write(boardgameSessionList);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return false;
        }
        return true;
    }

    @CacheResult(cacheName = "boardgame-session-participant-cache")
    @Override
    public List<BoardgameSessionParticipant> readBoardgameSessionParticipants() {
        try {
            return new CsvToBeanBuilder<BoardgameSessionParticipant>(new FileReader(boardgameSessionParticipantFilePath))
                    .withType(BoardgameSessionParticipant.class).build().parse();
        } catch (FileNotFoundException e) {
            Log.error(e);
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "boardgame-session-participant-cache")
    @Override
    public boolean writeBoardgameSessionParticipants(List<BoardgameSessionParticipant> boardgameSessionParticipantList) {
        try {
            Writer writer = new FileWriter(boardgameSessionParticipantFilePath);
            StatefulBeanToCsv<BoardgameSessionParticipant> beanToCsv = new StatefulBeanToCsvBuilder<BoardgameSessionParticipant>(writer).build();
            beanToCsv.write(boardgameSessionParticipantList);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return false;
        }
        return true;
    }
}
