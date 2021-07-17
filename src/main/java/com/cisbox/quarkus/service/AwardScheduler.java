package com.cisbox.quarkus.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.cisbox.quarkus.dao.EntityPersister;
import com.cisbox.quarkus.entity.User;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped 
public class AwardScheduler {

    @Inject 
    private ScoreboardService scoreboardService;

    @Scheduled(cron="0 0 1 * * ?")     
    void awardChampions() {
        Map<String, User> userMap = EntityPersister.readUsers().stream().collect(Collectors.toMap(User::getName, Function.identity()));
        Map<String, Integer> awardMap = new HashMap<>();

        EntityPersister.readSeasons().stream()
            .filter(currSeason -> LocalDate.now().isAfter(currSeason.getEndDate()))
            .forEach(currSeason -> {
                String seasonWinnerName = scoreboardService.getSeasonTable(currSeason.getName()).get(0).getName();
                if(!awardMap.containsKey(seasonWinnerName)){
                    awardMap.put(seasonWinnerName, 0);
                }
                awardMap.put(seasonWinnerName, awardMap.get(seasonWinnerName) + 1);
            });
        
        for(Entry<String, Integer> currAwardMapEntry : awardMap.entrySet()){
            userMap.get(currAwardMapEntry.getKey()).setAwards(currAwardMapEntry.getValue());
        }

        EntityPersister.writeUsers(userMap.values());
    }
}
