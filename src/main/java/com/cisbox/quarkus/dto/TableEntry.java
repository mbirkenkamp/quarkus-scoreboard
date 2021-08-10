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
    private Float weightedScore = 0.0f;

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
        this.weightedScore = calcWeightedScore();
    }

    public Long getGoalTotal(){
        return goalsScored - goalsReceived;
    }

    public Float calcWeightedScore() {
        if(gameCount < 3){
            return 0.0f;
        } else {
            return points.floatValue()/gameCount.floatValue()*100;
        }
    }

    @Override
    public int compareTo(TableEntry o) {
        if(this.getWeightedScore().equals(o.getWeightedScore())){
            return this.getGoalTotal().compareTo(o.getGoalTotal());
        }
        return this.getWeightedScore().compareTo(o.getWeightedScore());
    }

    /*@Override
    public int compareTo(TableEntry o) {
        if(this.points.equals(o.points)){
            return this.getGoalTotal().compareTo(o.getGoalTotal());
        }
        return this.points.compareTo(o.points);
    }*/
}
