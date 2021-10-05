package com.cisbox.quarkus.dao;

import java.util.Collection;
import java.util.List;

import com.cisbox.quarkus.entity.Department;
import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;
import com.cisbox.quarkus.entity.User;

public interface EntityPersister {
    List<User> readUsers();
    int writeUsers(Collection<User> userList);
    List<Season> readSeasons();
    int writeSeasons(List<Season> seasonList);
    List<Game> readGames();
    int writeGames(List<Game> gameList);
    List<Department> readDepartments();
    int writeDepartments(List<Department> departmentList);
}
