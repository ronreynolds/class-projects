package com.ronreynolds.games.dungeon;

public class DungeonMap {
    private final Room[][] rooms;

    public static DungeonMap generateRandomDungeonMap(int width, int height) {
        Room[][] rooms = new Room[width][height];
        for (int row  = 0; row < width; ++row) {
            for (int col = 0; col < height; ++col) {
                rooms[row][col] = Room.generateRandomRoom();
            }
        }
        return new DungeonMap(rooms);
    }

    public DungeonMap(Room[][] rooms) {
        this.rooms = rooms;
    }

    public void print() {
        // print map (X for player location, * for visited rooms, ' ' for unvisited rooms
        // print contents of new room
    }
}
