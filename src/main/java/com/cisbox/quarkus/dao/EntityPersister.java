package com.cisbox.quarkus.dao;

import java.util.Collection;
import java.util.List;

import com.cisbox.quarkus.entity.*;

import io.quarkus.cache.*;

public interface EntityPersister {
    List<User> readUsers();
    int writeUsers(Collection<User> userList);
    List<Season> readSeasons();
    int writeSeasons(List<Season> seasonList);
    List<Game> readGames();
    int writeGames(List<Game> gameList);
    List<Department> readDepartments();
    int writeDepartments(List<Department> departmentList);

    @CacheResult(cacheName = "boardgame-cache")
    List<BoardgameSession> readBoardgameSessions();

    @CacheInvalidateAll(cacheName = "boardgame-cache")
    boolean writeBoardgameSessions(List<BoardgameSession> boardgameList);

    @CacheResult(cacheName = "boardgame-cache")
    List<Boardgame> readBoardgames();

    @CacheInvalidateAll(cacheName = "boardgame-cache")
    boolean writeBoardgames(List<Boardgame> departmentList);

    @CacheResult(cacheName = "boardgame-session-participant-cache")
    List<BoardgameSessionParticipant> readBoardgameSessionParticipants();

    @CacheInvalidateAll(cacheName = "boardgame-session-participant-cache")
    boolean writeBoardgameSessionParticipants(List<BoardgameSessionParticipant> boardgameSessionParticipantList);
}
