package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.Console;
import com.ronreynolds.games.util.RandomUtil;

/**
 * a room in a dungeon; tracks if it's been visited by player and what it contains (monster, gold, or health).
 * if player took gold then room's gold will be 0; if player drank health room's health will be 0; if player killed
 * room's monster then monster's health will be 0 (but it will still be in the room).
 */
public class Room {
    private boolean visited;
    private final Monster monster;
    private int gold;
    private int health;

    // this might need some weighting towards more monsters...
    public static Room generateRandomRoom() {
        switch (RandomUtil.randomPositiveIntLessThan(3)) {
            case 0:
                return new Room(Monster.generateRandomMonster(), 0, 0);
            case 1:
                return new Room(null, RandomUtil.randomIntBetween(DungeonGame.MIN_GOLD, DungeonGame.MAX_GOLD), 0);
            case 2:
                return new Room(null, 0, RandomUtil.randomIntBetween(DungeonGame.MIN_HEALTH, DungeonGame.MAX_HEALTH));
            default:
                throw new IllegalStateException("impossible random number not between 0...2");
        }
    }

    // starting-room is always empty (because of game flow in DungeonGame)
    public static Room emptyRoom() {
        return new Room(null, 0, 0);
    }

    // force others to use factory methods to create either random or empty rooms
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

    // print the contents of the room
    public void print() {
        if (visited) {
            Console.print("it looks like you've been here before");
        }

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
    }

    // this method allows us to display contents for testing if enabled; otherwise it'll be a space
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
