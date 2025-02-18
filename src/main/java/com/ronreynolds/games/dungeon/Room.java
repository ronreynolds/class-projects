package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.RandomUtil;

public class Room {
    private static final int MIN_GOLD = 1;
    private static final int MIN_HEALTH = 10;
    // reasonable values?
    private static final int MAX_GOLD = 100;
    private static final int MAX_HEALTH = 50;

    private boolean visited;
    private final Monster monster;
    private final int gold;
    private final int health;

    public static Room generateRandomRoom() {
        switch(RandomUtil.randomIntBetween(0,3)) {
            case 0:
                return new Room(Monster.generateRandomMonster(), 0, 0);
            case 1:
                return new Room(null, RandomUtil.randomIntBetween(MIN_GOLD, MAX_GOLD), 0);
            case 2:
                return new Room(null, 0, RandomUtil.randomIntBetween(MIN_HEALTH, MAX_HEALTH));
            default:
                throw new IllegalStateException("impossible random number not between 0...2");
        }
    }

    private Room(Monster monster, int gold, int health) {
        this.monster = monster;
        this.gold = gold;
        this.health = health;
    }

    public Monster getMonster() {
        return monster;
    }
    public int getGold() {
        return gold;
    }
    public int getHealth() {
        return health;
    }
    public boolean isVisited() {
        return visited;
    }
    public void wasVisited() {
        visited = true;
    }
}
