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

import com.cisbox.quarkus.dao.EntityPersister;
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
    private EntityPersister entityPersister;

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
    public Response createSeason(@QueryParam("name") String name, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate) {
        List<Season> seasonList = entityPersister.readSeasons();
        if(seasonList.stream().anyMatch(currSeason -> currSeason.getName().equals(name))){
            return Response.status(409).build();
        } else {
            Season season = new Season(name, LocalDate.parse(startDate), LocalDate.parse(endDate));
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
        String concreteSeason = scoreboardService.handleSeason(season);

        if(concreteSeason == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        Optional<Season> seasonObj = scoreboardService.getSeason(concreteSeason);
        
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
        String concreteSeason = scoreboardService.handleSeason(season);

        if(concreteSeason == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(
            gson.toJson(
                scoreboardService.getSeasonTable(concreteSeason)
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

        String concreteSeason = scoreboardService.handleSeason(season);

        if(concreteSeason == null){
            return Response.status(Status.NOT_FOUND).build();
        }

        List<Game> gameList = entityPersister.readGames();
        return Response.ok(
            gson.toJson(
                gameList.stream()
                .filter(currGame -> currGame.getSeasonName().equals(concreteSeason))
                .collect(
                    Collectors.toList()
                )
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
        return Response.ok(gson.toJson(entityPersister.readUsers())).build();
    }

    /**
     * create user
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user/{username}")
    public Response createUsert(@PathParam("username") String username) {
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
            @QueryParam("user1") String user1,
            @QueryParam("user2") String user2, 
            @QueryParam("score1") int score1,
            @QueryParam("score2") int score2
        ) {
        
        List<Game> gameList = entityPersister.readGames();
        String concreteSeason = scoreboardService.handleSeason(season);

        if(concreteSeason == null){
            return Response.status(Status.NOT_FOUND).build();
        }
        
        var game = new Game(concreteSeason, LocalDate.now(), user1, user2, score1, score2);
        
        gameList.add(game);
        if(entityPersister.writeGames(gameList) == 0) {
            return Response.ok(gson.toJson(game)).build();
        } else {
            return Response.serverError().build();
        }
    }
}