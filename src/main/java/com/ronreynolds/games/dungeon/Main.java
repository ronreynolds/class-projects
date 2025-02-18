package com.ronreynolds.games.dungeon;

public class Main {
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;

    public static void main(String[] args) {
        Player player = new Player(Player.PlayerClass.select());
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        // TODO - get width and height from args (if available)

        DungeonMap map = DungeonMap.generateRandomDungeonMap(width, height);
        DungeonGame game = new DungeonGame(map, player);
        game.play();
    }
}
