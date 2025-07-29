package com.cisbox.quarkus.entity;

import java.util.*;

import org.apache.commons.lang3.*;

import com.opencsv.bean.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boardgame implements Comparable<Boardgame> {

    @CsvBindByName
    private UUID id;

    @CsvBindByName
    private String name;

    public Boardgame(String name) {
        this(UUID.randomUUID(), name);
    }

    @Override
    public int compareTo(Boardgame boardgame) {
        return StringUtils.compare(this.name, boardgame.name);
    }
}
