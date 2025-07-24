package com.cisbox.quarkus.dto;

import java.time.*;
import java.util.*;

public record BoardgameSessionDTO(
        LocalDate date,
        List<ParticipantDTO> participants
) {
    public record ParticipantDTO(
            String name,
            boolean hasWon,
            boolean hasLost
    ) {}
}
