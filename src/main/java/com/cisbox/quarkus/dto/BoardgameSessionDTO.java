package com.cisbox.quarkus.dto;

import java.time.*;
import java.util.*;

public record BoardgameSessionDTO(
        LocalDate date,
        List<BoardgameParticipantDTO> participants
) {}
