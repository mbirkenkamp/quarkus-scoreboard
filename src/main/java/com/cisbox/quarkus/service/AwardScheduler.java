package com.cisbox.quarkus.service;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.entity.User;
import io.quarkus.scheduler.Scheduled;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@ApplicationScoped 
public class AwardScheduler {

    @Inject
    CsvEntityPersister entityPersister;

    @Scheduled(cron="0 0 1 * * ?")     
    void awardChampions() {
        List<User> userList = entityPersister.readUsers();
        Map<String, Integer> winMap = new HashMap<>();
        Map<String, Integer> goalMap = new HashMap<>();

        entityPersister.readGames()
        .forEach(currGame -> 
            currGame.getWinners().forEach(currWinner -> {
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
        
        String winner = winMap.entrySet().stream().max(Entry.comparingByValue()).get().getKey();
        String topScorer = goalMap.entrySet().stream().max(Entry.comparingByValue()).get().getKey();

        for(User user : userList){
            user.setMostWins(user.getName().equals(winner));
            user.setMostGoals(user.getName().equals(topScorer));
        }

        entityPersister.writeUsers(userList);
    }
}
