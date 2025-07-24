package com.cisbox.quarkus.entity;

import java.time.*;
import java.util.*;

import com.opencsv.bean.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardgameSession implements Comparable<BoardgameSession> {

    @CsvBindByName
    private UUID id;

    @CsvBindByName
    private UUID boardgameId;

    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate date;

    public BoardgameSession(UUID boardgameId, LocalDate date) {
        this(UUID.randomUUID(), boardgameId, date);
    }

    @Override
    public int compareTo(BoardgameSession boardgameSession) {
        return date.compareTo(boardgameSession.date);
    }
}
