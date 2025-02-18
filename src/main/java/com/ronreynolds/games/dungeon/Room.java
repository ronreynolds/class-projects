package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.Console;
import com.ronreynolds.games.util.RandomUtil;

public class Room {
    private static final int MIN_GOLD = 1;
    private static final int MIN_HEALTH = 10;
    // reasonable values?
    private static final int MAX_GOLD = 100;
    private static final int MAX_HEALTH = 50;

    private boolean visited;
    private final Monster monster;
    private int gold;
    private int health;

    public static Room generateRandomRoom() {
        switch (RandomUtil.randomPositiveIntLessThan(3)) {
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

    public static final Room EMPTY_ROOM = new Room(null, 0, 0);

    static {
        EMPTY_ROOM.wasVisited();
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

    public void clearGold() {
        this.gold = 0;
    }

    public int getHealth() {
        return health;
    }

    public void clearHealth() {
        this.health = 0;
    }

    public boolean hasLoot() {
        return gold > 0 || health > 0;
    }


    public boolean isVisited() {
        return visited;
    }

    public void wasVisited() {
        visited = true;
    }

    public void print() {
        String contents;
        if (monster != null) {
            contents = "a monster";
        } else if (gold > 0) {
            contents = gold + " gold pieces";
        } else if (health > 0) {
            contents = "a healing potion";
        } else {
            contents = "nothing";
        }
        Console.print("the room contains %s!", contents);
        if (visited) {
            Console.print("it looks like you've been here before");
        }
    }

    public char getCharForContents() {
        char c = ' ';   // for non-test mode or just empty
        if (DungeonGame.testMode) {
            if (getHealth() > 0) {
                c = 'H';
            } else if (getGold() > 0) {
                c = '$';
            } else if (getMonster() != null) {
                switch (getMonster().getType()) {
                    case goblin:
                        c = 'G';
                        break;
                    case zombie:
                        c = 'Z';
                        break;
                    case orc:
                        c = 'o';
                        break;
                    case ogre:
                        c = 'O';
                        break;
                }
            }
        }
        return c;
    }
}
