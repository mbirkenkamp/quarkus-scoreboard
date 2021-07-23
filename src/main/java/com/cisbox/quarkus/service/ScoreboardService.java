package com.cisbox.quarkus.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.dto.TableEntry;
import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;

import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
@RegisterForReflection
@Named("ScoreboardService")
public class ScoreboardService {
    @Inject 
    private CsvEntityPersister entityPersister;

    public List<TableEntry> getSeasonTable(String season){
        List<Game> gameList = entityPersister.readGames();
        Map<String, TableEntry> sortedMap = new HashMap<>();
        
        entityPersister.readUsers().stream().forEach(currUser -> sortedMap.put(currUser.getName(), new TableEntry(currUser.getName())));

        for(Game currGame : gameList) {
            if(currGame.getSeasonName().equals(season)){
                sortedMap.get(currGame.getUser1()).logGame(currGame.getUser1Score(), currGame.getUser2Score());
                sortedMap.get(currGame.getUser2()).logGame(currGame.getUser2Score(), currGame.getUser1Score());
            }
        }

        return sortedMap.values().stream()
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());
    }

    public String handleSeason(String season) {
        if(season.equals("current")){
            Optional<Season> output = entityPersister.readSeasons().stream()
            .filter(currSeason -> LocalDate.now().isAfter(currSeason.getStartDate()) 
                	&& LocalDate.now().isBefore(currSeason.getEndDate()))
            .findAny();
            return output.isPresent() ? output.get().getName() : null;
        } else {
            return season;
        }
    }

    public Optional<Season> getSeason(String seasonName) {
        return entityPersister.readSeasons().stream()
            .filter(currSeason -> currSeason.getName().equals(seasonName))
            .findFirst();
    }
}
