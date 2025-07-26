package com.cisbox.quarkus.dto;

import java.time.*;
import java.util.*;

import com.cisbox.quarkus.entity.*;

public record BoardgameTableEntry(
        Boardgame boardgame,
        LocalDate date,
        List<BoardgameParticipantDTO> participants
) {}
