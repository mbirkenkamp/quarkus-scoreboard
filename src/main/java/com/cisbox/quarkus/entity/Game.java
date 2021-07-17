package com.cisbox.quarkus.entity;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @CsvBindByName private String seasonName;

    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate date;

    @CsvBindByName private String user1;
    @CsvBindByName private String user2;
    @CsvBindByName private int user1Score;
    @CsvBindByName private int user2Score;

    public Game(String seasonName, String user1, String user2, String score) {
        this.seasonName = seasonName;
        this.user1 = user1;
        this.user2 = user2;
        this.date = LocalDate.now();
        if(Pattern.matches("[0-9]:[0-9]", score)){
            this.user1Score = Integer.parseInt(score.split(":")[0]);
            this.user2Score = Integer.parseInt(score.split(":")[1]);
        }
    }

    public String getWinner(){
        if (user1Score > user2Score) {
            return user1;
        } else {
            return user2;
        }
    }
}
