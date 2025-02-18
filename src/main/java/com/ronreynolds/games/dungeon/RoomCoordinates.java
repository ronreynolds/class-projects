package com.ronreynolds.games.dungeon;

public class RoomCoordinates {
    private final int row;
    private final int column;
    public RoomCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
}
