package com.cisbox.quarkus.dao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;
import com.cisbox.quarkus.entity.User;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
@RegisterForReflection
@Named("EntityPersister")
public class EntityPersister {
    private static final String USERFILE = "../data/user.csv";
    private static final String SEASONFILE = "../data/season.csv";
    private static final String GAMEFILE = "../data/game.csv";

    private EntityPersister(){}

    @CacheResult(cacheName = "user-cache")
    public static List<User> readUsers(){
        try {
        return new CsvToBeanBuilder<User>(new FileReader(USERFILE)).withType(User.class).build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "user-cache")
    public static int writeUsers(Collection<User> userList){
        try {
            Writer writer = new FileWriter(USERFILE);
            StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(writer).build();
            beanToCsv.write(userList.iterator());        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "season-cache")
    public static List<Season> readSeasons(){
        try {
            return new CsvToBeanBuilder<Season>(new FileReader(SEASONFILE)).withType(Season.class).build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "season-cache")
    public static int writeSeasons(List<Season> seasonList){
        try {
            
            Writer writer = new FileWriter(SEASONFILE);
            StatefulBeanToCsv<Season> beanToCsv = new StatefulBeanToCsvBuilder<Season>(writer).build();
            beanToCsv.write(seasonList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "game-cache")
    public static List<Game> readGames(){
        try {
        return new CsvToBeanBuilder<Game>(new FileReader(GAMEFILE)).withType(Game.class).build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "game-cache")
    public static int writeGames(List<Game> gameList){
        try {
            Writer writer = new FileWriter(GAMEFILE);
            StatefulBeanToCsv<Game> beanToCsv = new StatefulBeanToCsvBuilder<Game>(writer).build();
            beanToCsv.write(gameList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }
}
