package com.ronreynolds.games.dungeon;

public class DungeonGame {
    private final DungeonMap map;
    private final Player player;

    public DungeonGame(DungeonMap map, Player player) {
        this.map = map;
        this.player = player;
    }
    public void play() {
        while (!(player.isDead() || player.isRich())) {
            // get move
            // validate move (make sure there's a room in that direction)
            // update player's position in dungeon
            // check contents of new room (visited, monster, etc)
            // inform player of what they found
            // if monster get players fight/flight response
            // if loot add to player's wallet
            // if health apply if player needs it
        }

    }
}
