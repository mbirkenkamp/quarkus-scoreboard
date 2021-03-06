package com.cisbox.quarkus.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.cisbox.quarkus.dao.CsvEntityPersister;
import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;
import com.cisbox.quarkus.entity.User;
import com.cisbox.quarkus.service.ScoreboardService;
import com.google.gson.Gson;

@Path("/scoreboard")
public class ScoreboardRest {

    @Inject 
    private ScoreboardService scoreboardService;

    @Inject 
    private CsvEntityPersister entityPersister;

    Gson gson = new Gson();

    /**
     * neue Saison anlegen
     * 
     * @param name name of season
     * @param startDate start date of the season (format YYYY-MM-DD)
     * @param endDate end sate of the season (format YYYY-MM-DD)
     * @return HTTP 200 for success
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
                return Response.ok(gson.toJson(season)).build();
            } else {
                return Response.serverError().build();
            }
        }
    }

    /**
     * get season list
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season")
    public Response getSeasonList() {
        return Response.ok(gson.toJson(entityPersister.readSeasons())).build();
    }
    
    /**
     * get season info
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}")
    public Response getSeason(@PathParam("season") String season) {
        if(season == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        Optional<Season> seasonObj = scoreboardService.getSeason(season);
        
        if(!seasonObj.isPresent()){
            return Response.status(404).build();
        } else {
            return Response.ok(gson.toJson(seasonObj)).build();
        }
    }

    /**
     * get table for season
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/season/{season}/table")
    public Response getSeasonTable(@PathParam("season") String season) {
        if(season == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(
            gson.toJson(
                scoreboardService.getSeasonTable(season)
            )
        ).build();
    }

    /**
     * get gamelist for season
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
            gson.toJson(
                gameList.stream()
                .filter(currGame -> currGame.getSeasonName().equals(season))
                .collect(
                    Collectors.toList()
                )
            )
        ).build();
    }

    /**
     * get gamelist for season
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/game")
    public Response getGamelist() {
        List<Game> gameList = entityPersister.readGames();

        return Response.ok(
                gson.toJson(
                    gameList
                )
            ).build();
    }

    /**
     * get userlist
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    public Response getUserlist() {
        return Response.ok(
            gson.toJson(
                entityPersister.readUsers().stream()
                .sorted((user1, user2) -> user1.getName().compareTo(user2.getName()))
                .collect(Collectors.toList())
            )
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
                return Response.ok(gson.toJson(user)).build();
            } else {
                return Response.serverError().build();
            }
        }
    }

    /**
     * create game
     * @param season
     * @param user1
     * @param user2
     * @param score1
     * @param score2
     * @return
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
            return Response.ok(gson.toJson(game)).build();
        } else {
            return Response.serverError().build();
        }
    }

    /**
     * create game
     * @param season
     * @param user1
     * @param user2
     * @param score1
     * @param score2
     * @return
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
            return Response.ok(gson.toJson(game)).build();
        } else {
            return Response.serverError().build();
        }
    }
}