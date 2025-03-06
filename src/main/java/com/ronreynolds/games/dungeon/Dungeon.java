package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.Console;

public class Dungeon {
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;

    /**
     * main entry-point into Dungeon game
     */
    public static void main(String[] args) {
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        for (String arg : args) {
            if (arg.startsWith("--width=")) {
                width = Integer.parseInt(arg.substring("--width=".length()));
            } else if (arg.startsWith("--height")) {
                height = Integer.parseInt(arg.substring("--height=".length()));
            } else if (arg.equals("--debug")) {
                DungeonGame.testMode = true;
            } else {
                Console.print("skipping unrecognized arg '%s'", arg);
            }
        }
        Player.PlayerClass playerClass = Player.PlayerClass.select();
        Player player = new Player(playerClass);
        Console.print("good luck %s; it takes %d gold to escape the dungeon", playerClass, DungeonGame.WINNING_GOLD);

        DungeonMap map = DungeonMap.generateRandomDungeonMap(height, width, 0, 0);
        DungeonGame game = new DungeonGame(map, player);
        game.play();
        Console.print("the end.  you gathered %d gold, killed %s, and %s", player.getGold(), player.getKillMap(),
                player.isDead() ? "died. :(" : "didn't die! :)");
    }
}
