package com.cisbox.quarkus.entity;

import java.util.*;

import org.apache.commons.lang3.*;

import com.opencsv.bean.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardgameSessionParticipant implements Comparable<BoardgameSessionParticipant> {

    @CsvBindByName
    private UUID sessionId;

    @CsvBindByName
    private String playerName;

    @CsvBindByName
    private boolean hasWon;

    @CsvBindByName
    private boolean hasLost;

    @Override
    public int compareTo(BoardgameSessionParticipant boardgameSessionParticipant) {
        if (this.hasWon && !boardgameSessionParticipant.hasWon) {
            return -1;
        } else if (!this.hasWon && boardgameSessionParticipant.hasWon) {
            return 1;
        }
        return StringUtils.compare(this.playerName, boardgameSessionParticipant.playerName);
    }
}
