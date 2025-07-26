package com.cisbox.quarkus.rest;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.dto.*;
import com.cisbox.quarkus.entity.*;
import com.cisbox.quarkus.service.ScoreboardService;

import io.quarkus.logging.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.*;

@Consumes(MediaType.WILDCARD)
@Path("/scoreboard")
public class ScoreboardRest {

    @Inject
    ScoreboardService scoreboardService;

    @Inject
    CsvEntityPersister entityPersister;

    /**
     * Create new Season
     * 
     * @param name Name of the Season
     * @param startDate Start-Date of the Season (format YYYY-MM-DD)
     * @param endDate End-Date of the Season (format YYYY-MM-DD)
     * @return An HTTP Response
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season")
    public Response createSeason(@QueryParam("name") String name, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("icon") String icon, @QueryParam("team_size") int teamSize) {
        List<Season> seasonList = entityPersister.readSeasons();
        if(seasonList.stream().anyMatch(currSeason -> currSeason.getName().equals(name))){
            return Response.status(409).build();
        } else {
            Season season = new Season(name, LocalDate.parse(startDate), LocalDate.parse(endDate), icon, teamSize);
            seasonList.add(season);
            if(entityPersister.writeSeasons(seasonList) == 0) {
                return Response.ok(season).build();
            } else {
                return Response.serverError().build();
            }
        }
    }

    /**
     * Get Season List
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season")
    public Response getSeasonList() {
        return Response.ok(entityPersister.readSeasons()).build();
    }
    
    /**
     * Get Season Info
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}")
    public Response getSeason(@PathParam("season") String season) {
        if(season == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        Optional<Season> seasonObj = scoreboardService.getSeason(season);
        
        if(seasonObj.isEmpty()){
            return Response.status(404).build();
        } else {
            return Response.ok(seasonObj).build();
        }
    }

    /**
     * Get Table for Season
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}/table")
    public Response getSeasonTable(@PathParam("season") String season) {
        if(season == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(scoreboardService.getSeasonTable(season)).build();
    }

    /**
     * Get Game List for a Season
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}/game")
    public Response getSeasonGamelist(@PathParam("season") String season) {
        if(season == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        List<Game> gameList = entityPersister.readGames();
        return Response.ok(
                gameList.stream()
                        .filter(currGame -> currGame.getSeasonName().equals(season))
                        .collect(
                                Collectors.toList()
                        )
        ).build();
    }

    /**
     * Get Game List for a Season
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/game")
    public Response getGamelist() {
        List<Game> gameList = entityPersister.readGames();

        return Response.ok(gameList).build();
    }

    /**
     * Get UserList
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    public Response getUserlist() {
        return Response.ok(
                entityPersister.readUsers().stream()
                        .sorted(Comparator.comparing(User::getName))
                        .collect(Collectors.toList())
        ).build();
    }

    /**
     * create user
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user/{username}")
    public Response createUser(@PathParam("username") String username) {
        List<User> userList = entityPersister.readUsers();        
        var user = new User(username);

        if(userList.stream().anyMatch(currUser -> currUser.getName().equals(username))){
            return Response.status(409).build();
        } else {
            userList.add(user);
            if(entityPersister.writeUsers(userList) == 0) {
                return Response.ok(user).build();
            } else {
                return Response.serverError().build();
            }
        }
    }

    /**
     * Adds a 1v1 Game to an existing Season
     *
     * @param season The Season to add the Game to
     * @param team1User1 User playing as Team 1
     * @param team2User1 User playing as Team 2
     * @param score1 Score of Team 1
     * @param score2 Score of Team 2
     * @return A HTTP Response
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}/game")
    public Response addGameToSeason(
            @PathParam("season") String season, 
            @QueryParam("team1user1") String team1User1,
            @QueryParam("team2user1") String team2User1,
            @QueryParam("score1") int score1,
            @QueryParam("score2") int score2
        ) {
        
        List<Game> gameList = entityPersister.readGames();
        Optional<Season> seasonOpt = scoreboardService.getSeason(season);

        if(seasonOpt.isEmpty()){
            return Response.status(Status.NOT_FOUND).build();
        }
        
        var game = new Game(seasonOpt.get().getName(), team1User1, team2User1, score1, score2);
        
        gameList.add(game);
        if(entityPersister.writeGames(gameList) == 0) {
            return Response.ok(game).build();
        } else {
            return Response.serverError().build();
        }
    }

    /**
     * Adds a 2v2 Game to an existing Season
     *
     * @param season The Season to add the Game to
     * @param team1User1 First User playing in Team 1
     * @param team1User2 Second User playing in Team 2
     * @param team2User1 First User playing in Team 2
     * @param team2User2 Second User playing in Team 2
     * @param score1 Score of Team 1
     * @param score2 Score of Team 2
     * @return A HTTP Response
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}/teamgame")
    public Response addTeamGameToSeason(
            @PathParam("season") String season, 
            @QueryParam("team1user1") String team1User1,
            @QueryParam("team1user2") String team1User2,
            @QueryParam("team2user1") String team2User1,
            @QueryParam("team2user2") String team2User2,
            @QueryParam("score1") int score1,
            @QueryParam("score2") int score2
        ) {
        
        List<Game> gameList = entityPersister.readGames();
        Optional<Season> seasonOpt = scoreboardService.getSeason(season);

        if(seasonOpt.isEmpty()){
            return Response.status(Status.NOT_FOUND).build();
        }
        
        var game = new Game(seasonOpt.get().getName(), LocalDate.now(), team1User1, team1User2, team2User1, team2User2, score1, score2);
        
        gameList.add(game);
        if(entityPersister.writeGames(gameList) == 0) {
            return Response.ok(game).build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/boardgame")
    public Response createBoardgame(
            @QueryParam("name") String name
    ) {
        if (StringUtils.isBlank(name)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        List<Boardgame> boardgames = entityPersister.readBoardgames();
        if (boardgames.stream().anyMatch(bg ->  bg.getName().equals(name))) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Boardgame newGame = new Boardgame(name);
        boardgames.add(newGame);
        if (entityPersister.writeBoardgames(boardgames)) {
            return Response.ok(newGame).build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/boardgames")
    public Response getBoardgames() {
        return Response.ok(entityPersister.readBoardgames()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/boardgames/sessions")
    public Response getBoardgameSessions(
            @QueryParam("boardgameId") String boardgameId,
            @QueryParam("maxResults") int maxResults
    ) {
        Map<UUID, Boardgame> boardgames = entityPersister.readBoardgames().stream()
                .collect(Collectors.toMap(Boardgame::getId, b -> b));
        List<BoardgameSession> sessions = entityPersister.readBoardgameSessions().stream()
                .sorted((s1, s2) -> -1 * s1.getDate().compareTo(s2.getDate()))
                .toList();
        Map<UUID, List<BoardgameSessionParticipant>>  participants = entityPersister.readBoardgameSessionParticipants()
                .stream().collect(Collectors.groupingBy(BoardgameSessionParticipant::getSessionId));

        UUID boardgameIdFilter = null;
        if (StringUtils.isNotBlank(boardgameId)) {
            try {
                boardgameIdFilter = UUID.fromString(boardgameId);
            } catch (IllegalArgumentException e) {
                return Response.status(Status.BAD_REQUEST).entity("Bad Request: Invalid UUID Format").build();
            }
        }

        int count = 0;
        List<BoardgameTableEntry> results = new LinkedList<>();
        for (BoardgameSession session : sessions) {
            if (boardgameIdFilter != null && !session.getBoardgameId().equals(boardgameIdFilter)) {
                continue;
            }

            results.add(new BoardgameTableEntry(
                    boardgames.get(session.getBoardgameId()),
                    session.getDate(),
                    participants.get(session.getId())
                            .stream()
                            .map(BoardgameParticipantDTO::fromEntity)
                            .toList()
            ));

            count++;
            if (maxResults > 0 && count >= maxResults) {
                break;
            }
        }
        return Response.ok(results).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/boardgame/{gameId}/session")
    public Response createNewSession(@PathParam("gameId") String gameId, BoardgameSessionDTO dto) {
        if (StringUtils.isBlank(gameId)) {
            return Response.status(Status.BAD_REQUEST).entity("No GameID provided").build();
        }

        UUID gameUuid;
        try {
            gameUuid = UUID.fromString(gameId);
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity("Invalid GameID format").build();
        }

        if (entityPersister.readBoardgames().stream().noneMatch(bg -> bg.getId().equals(gameUuid))) {
            return Response.status(Status.BAD_REQUEST).entity("GameID does not exist").build();
        }

        var givenNames = dto.participants().stream()
                .map(BoardgameParticipantDTO::name)
                .collect(Collectors.toCollection(HashSet::new));
        var existingNames = entityPersister.readUsers().stream()
                .map(User::getName)
                .collect(Collectors.toCollection(HashSet::new));
        if (!existingNames.containsAll(givenNames)) {
            return Response.status(Status.BAD_REQUEST).entity("Bad Request: User does not exist").build();
        }

        int winnerOrLoserCount = 0;

        BoardgameSession session = new BoardgameSession(gameUuid, dto.date());
        List<BoardgameSessionParticipant> participants = new LinkedList<>();

        for (var part : dto.participants()) {
            BoardgameSessionParticipant entity = new BoardgameSessionParticipant();
            entity.setSessionId(session.getId());
            entity.setPlayerName(part.name());
            entity.setHasWon(part.hasWon());
            entity.setHasLost(part.hasLost());
            if (part.hasWon() || part.hasLost()) {
                winnerOrLoserCount++;
            }
            participants.add(entity);
        }

        if (winnerOrLoserCount == 0) {
            return Response.status(Status.BAD_REQUEST).entity("Bad Request: No Winners or Losers set").build();
        }
        Log.infof("Saving Boardgame Session: %s", dto);

        var fileSessions = entityPersister.readBoardgameSessions();
        fileSessions.add(session);

        boolean success = entityPersister.writeBoardgameSessions(fileSessions);
        if (!success) {
            return Response.serverError().build();
        }

        var fileParticipants = entityPersister.readBoardgameSessionParticipants();
        fileParticipants.addAll(participants);

        success = entityPersister.writeBoardgameSessionParticipants(fileParticipants);
        if (success) {
            return Response.ok().build();
        }
        return Response.serverError().build();
    }
}