package com.cisbox.quarkus.entity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @CsvBindByName
    private String seasonName;

    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate date;

    @CsvBindByName
    private String team1User1;
    @CsvBindByName
    private String team1User2;
    @CsvBindByName
    private String team2User1;
    @CsvBindByName
    private String team2User2;
    @CsvBindByName
    private int team1Score;
    @CsvBindByName
    private int team2Score;

    public Game(String seasonName, String team1User1, String team2User1, Integer team1Score, Integer team2Score) {
        this.seasonName = seasonName;
        this.team1User1 = team1User1;
        this.team2User1 = team2User1;
        this.date = LocalDate.now();
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    public Game(String seasonName, String team1User1, String team1User2, String team2User1, String team2User2, Integer team1Score, Integer team2Score) {
        this.seasonName = seasonName;
        this.team1User1 = team1User1;
        this.team1User2 = team1User2;
        this.team2User1 = team2User1;
        this.team2User2 = team2User2;
        this.date = LocalDate.now();
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    public List<String> getWinners() {        
        if(team1Score > team2Score){
            return Arrays.asList(team1User1, team1User2);
        } else {
            return Arrays.asList(team2User1, team2User2);
        }
    }

    public Integer getPositiveGoalDiff() {        
        if(team1Score > team2Score){
            return team1Score - team2Score;
        } else {
            return team2Score - team1Score;
        }
    }
}
