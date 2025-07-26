package com.cisbox.quarkus.dto;

import com.cisbox.quarkus.entity.*;

public record BoardgameParticipantDTO(
        String name,
        boolean hasWon,
        boolean hasLost
) {
    public static BoardgameParticipantDTO fromEntity(BoardgameSessionParticipant entity) {
        return new BoardgameParticipantDTO(entity.getPlayerName(), entity.isHasWon(), entity.isHasLost());
    }
}
