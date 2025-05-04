package com.cisbox.quarkus.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.dto.TableEntry;
import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@RegisterForReflection
@Named("ScoreboardService")
public class ScoreboardService {

    @Inject
    CsvEntityPersister entityPersister;

    public List<TableEntry> getSeasonTable(String season) {
        List<Game> gameList = entityPersister.readGames();
        Map<String, TableEntry> sortedMap = new HashMap<>();
        
        entityPersister.readUsers().forEach(currUser -> sortedMap.put(currUser.getName(), new TableEntry(currUser.getName())));

        for(Game currGame : gameList) {
            if(currGame.getSeasonName().equals(season)){
                sortedMap.get(currGame.getTeam1User1()).logGame(currGame.getTeam1Score(), currGame.getTeam2Score());
                sortedMap.get(currGame.getTeam2User1()).logGame(currGame.getTeam2Score(), currGame.getTeam1Score());

                if(!StringUtils.isEmpty(currGame.getTeam1User2()) && !StringUtils.isEmpty(currGame.getTeam2User2())) {
                    sortedMap.get(currGame.getTeam1User2()).logGame(currGame.getTeam1Score(), currGame.getTeam2Score());
                    sortedMap.get(currGame.getTeam2User2()).logGame(currGame.getTeam2Score(), currGame.getTeam1Score());
                }
            }
        }

        return sortedMap.values().stream()
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());
    }

    public Optional<Season> getSeason(String seasonName) {
        return entityPersister.readSeasons().stream()
            .filter(currSeason -> currSeason.getName().equals(seasonName))
            .findFirst();
    }
}
