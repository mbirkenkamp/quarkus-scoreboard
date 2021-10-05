package com.cisbox.quarkus.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.entity.User;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped 
public class AwardScheduler {

    @Inject 
    private ScoreboardService scoreboardService;

    @Inject 
    private CsvEntityPersister entityPersister;

    @Scheduled(cron="0 0 1 * * ?")     
    void awardChampions() {
        List<User> userList = entityPersister.readUsers();
        Map<String, Integer> winMap = new HashMap<>();
        Map<String, Integer> goalMap = new HashMap<>();

        entityPersister.readGames().stream()
        .forEach(currGame -> 
            currGame.getWinners().stream().forEach(currWinner -> {
                if(currWinner == null || currWinner.isBlank()){
                    return;
                }

                if(winMap.get(currWinner) == null){
                    winMap.put(currWinner, 0);
                    goalMap.put(currWinner, 0);
                }

                winMap.put(currWinner, winMap.get(currWinner) + 1);
                goalMap.put(currWinner, goalMap.get(currWinner) + currGame.getPositiveGoalDiff());
            }
            )
        );
        
        String winner = winMap.entrySet().stream().max(Comparator.comparing(Entry::getValue)).get().getKey();
        String topScorer = goalMap.entrySet().stream().max(Comparator.comparing(Entry::getValue)).get().getKey();

        for(User user : userList){
            user.setMostWins(user.getName().equals(winner));
            user.setMostGoals(user.getName().equals(topScorer));
        }

        entityPersister.writeUsers(userList);
    }
}
