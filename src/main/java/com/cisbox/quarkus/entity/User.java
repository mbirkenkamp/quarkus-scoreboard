package com.cisbox.quarkus.entity;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {    
    @CsvBindByName private String name;
    @CsvBindByName private boolean mostWins = false;
    @CsvBindByName private boolean mostGoals = false;

    public User(String name) {
        this.name = name;
    }
}