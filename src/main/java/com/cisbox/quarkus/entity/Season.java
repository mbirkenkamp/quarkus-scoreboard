package com.cisbox.quarkus.entity;

import java.time.LocalDate;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Season {

    @CsvBindByName
    private String name;
    
    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate startDate;

    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate endDate; 
    
    @CsvBindByName
    private String icon; 

    @CsvBindByName
    private int teamSize; 
}
