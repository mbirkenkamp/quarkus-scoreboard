package com.cisbox.quarkus.rest;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;
import com.cisbox.quarkus.entity.User;
import com.cisbox.quarkus.service.ScoreboardService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
}