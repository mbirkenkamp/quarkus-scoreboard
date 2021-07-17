package com.cisbox.quarkus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableEntry implements Comparable<TableEntry> {
    private String name = "";
    private Long gameCount = 0l;
    private Long points = 0l;
    private Long goalsScored = 0l;
    private Long goalsReceived = 0l;

    public TableEntry(String name){
        this.name = name;
    }

    public void logGame(int goalsScored, int goalsReceived) {
        this.gameCount += 1;
        this.goalsScored += goalsScored;
        this.goalsReceived += goalsReceived;
        if(goalsScored > goalsReceived) {
            this.points += 1;
        }
    }

    public Long getGoalTotal(){
        return goalsScored - goalsReceived;
    }

    @Override
    public int compareTo(TableEntry o) {
        if(this.points.equals(o.points)){
            return this.getGoalTotal().compareTo(o.getGoalTotal());
        }
        return this.points.compareTo(o.points);
    }
}
