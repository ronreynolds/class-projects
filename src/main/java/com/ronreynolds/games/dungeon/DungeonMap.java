package com.ronreynolds.games.dungeon;

import lombok.extern.slf4j.Slf4j;

/**
 * represents the collection of rooms that constitute the dungeon
 */
@Slf4j
public class DungeonMap {
    // characters used to indicate the current player position and the rooms they have visited already
    private static final char PLAYER_CHAR = 'X';
    private static final char VISITED_CHAR = '*';

    private final Room[][] rooms;
    private final String topAndBottomLine;
    private final int rows;
    private final int columns;

    public enum Move {
        Up, Down, Left, Right;

        public static Move getMoveForChar(char c) {
            switch (c) {
                case 'U':
                case 'u':
                    return DungeonMap.Move.Up;
                case 'D':
                case 'd':
                    return DungeonMap.Move.Down;
                case 'L':
                case 'l':
                    return DungeonMap.Move.Left;
                case 'R':
                case 'r':
                    return DungeonMap.Move.Right;
            }
            return null;
        }
    }

    /**
     * generate a random dungeon of the specified size
     */
    public static DungeonMap generateRandomDungeonMap(int rows, int columns, int startRow, int startCol) {
        Room[][] rooms = new Room[rows][columns];
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                rooms[row][col] = Room.generateRandomRoom();
            }
        }

        // the starting room is ALWAYS empty (because of how game flow works in DungeonGame)
        rooms[startRow][startCol] = Room.emptyRoom();
        rooms[startRow][startCol].wasVisited();

        return new DungeonMap(rooms, rows, columns);
    }

    /**
     * wrap a DungeonMap around a double-array of rooms; width and height are provided so we don't have to extract them
     * from the rooms array every time.
     */
    private DungeonMap(Room[][] rooms, int rows, int columns) {
        this.rooms = rooms;
        this.rows = rows;
        this.columns = columns;
        // when printing the dungeon used to demarcate the very top and bottom of the dungeon; we cache it for perf
        this.topAndBottomLine = String.format("+%s+", "-".repeat(this.columns));
    }

    /**
     * print the state of the dungeon rooms (visited, not visited) and where the player is in the dungeon
     */
    public void print(int currentRoomRow, int currentRoomCol) {
        // print map (X for player location, * for visited rooms, ' ' for unvisited rooms
        System.out.println(topAndBottomLine);
        for (int row = 0; row < rows; ++row) {
            System.out.print('|');
            for (int col = 0; col < columns; ++col) {
                Room room = getRoom(row, col);
                if (row == currentRoomRow && col == currentRoomCol) {
                    System.out.print(PLAYER_CHAR);
                } else {
                    if (room.isVisited()) {
                        System.out.print(VISITED_CHAR);
                    } else {
                        System.out.print(room.getCharForContents());
                    }
                }
            }
            System.out.println('|');   // move down one line for next row
        }
        System.out.println(topAndBottomLine);
    }

    public boolean isValidMove(Move move, int currentRow, int currentCol) {
        switch (move) {
            case Up:
                return currentRow > 0; // you can go up if you're not on the 0th row
            case Down:
                return currentRow < rows - 1; // you can go down if you're not on the last row
            case Left:
                return currentCol > 0;   // you can go left if you're not in the 0th col
            case Right:
                return currentCol < columns - 1;  // you can go right if you're not in the last col
        }
        throw new IllegalArgumentException("invalid move " + move);
    }

    public Room getRoom(int row, int col) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("invalid row - " + row);
        }
        if (col < 0 || col >= columns) {
            throw new IllegalArgumentException("invalid col - " + col);
        }
        return rooms[row][col];
    }
}
