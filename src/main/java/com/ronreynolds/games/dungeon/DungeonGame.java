package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.Console;

public class DungeonGame {
    public static final int MIN_GOLD = 1;
    public static final int MIN_HEALTH = 10;
    // reasonable values?
    public static final int MAX_GOLD = 20;
    public static final int MAX_HEALTH = 25;

    public static final int WINNING_GOLD = 100;
    public static boolean testMode = false;

    private final DungeonMap map;
    private final Player player;

    public DungeonGame(DungeonMap map, Player player) {
        this.map = map;
        this.player = player;
    }

    public void play() {
        int currentRoomRow = 0;
        int currentRoomCol = 0;
        // keep going as long as the player is still alive and doesn't have enough gold
        // technically this could be an infinite loop if the dungeon doesn't contain enough gold.
        while (!player.isDead() && player.getGold() < WINNING_GOLD) {
            // display the map and the player's location
            map.print(currentRoomRow, currentRoomCol);
            // display player's gold and health
            player.print();

            // get move
            Console.print("select a direction (Up, Down, Left, Right):");
            char c = Console.readLineUntil(DungeonGame::isValidMove, Console.stringToChar);
            DungeonMap.Move move = DungeonMap.Move.getMoveForChar(c);
            if (move == null) {
                throw new IllegalStateException(c + "did not matched one of the move letters; isValidMove() is broken");
            }

            // validate move (make sure there's a room in that direction)
            if (map.isValidMove(move, currentRoomRow, currentRoomCol)) {
                // update player's position in dungeon
                switch (move) {
                    case Up:
                        --currentRoomRow;
                        break;
                    case Down:
                        ++currentRoomRow;
                        break;
                    case Left:
                        --currentRoomCol;
                        break;
                    case Right:
                        ++currentRoomCol;
                        break;
                }

                // check contents of new room (visited, monster, etc)
                Room room = map.getRoom(currentRoomRow, currentRoomCol);

                // inform player of what they found
                room.print();

                // if there's a monster describe it
                Monster monster = room.getMonster();
                if (monster != null) {
                    playerMeetsMonster(player, monster);
                } else if (room.hasLoot()) {
                    playerFindsLoot(player, room);
                } else {
                    Console.print("nothing to see here");
                }
                room.wasVisited();
            } else {
                Console.print("you can't go %s from here", move);
            }
        }

    }

    /* return true if the input starts with U, D, L, or R (case-insensitive); otherwise false */
    private static boolean isValidMove(String input) {
        return input.matches("[UuDdLlRr].*");
    }

    /* return true if the input starts with A or R (case-insensitive); otherwise false */
    private static boolean isValidAction(String input) {
        return input.matches("[AaRr].*");
    }

    private static void playerMeetsMonster(Player player, Monster monster) {
        monster.print();
        if (!monster.isDead()) {
            boolean playerRunsAway = false;
            // begin melee loop; only ways out are someone dies or player runs
            while (!monster.isDead() && !player.isDead() && !playerRunsAway) {
                // monster ALWAYS attacks first (doesn't seem fair but whatever)
                int damage = monster.attack(player);
                Console.print("the %s hits you for %d damage", monster.getType(), damage);
                if (player.isDead()) {
                    Console.print("you see a bright light...");
                    break;
                }
                Console.print("you have %d HP left", player.getHealth());

                // get player's action and determine the outcome
                Console.print("what do you do?  Attack or Run?");
                char action = Console.readLineUntil(DungeonGame::isValidAction, Console.stringToChar);
                switch (action) {
                    case 'A':
                    case 'a':
                        damage = player.attack(monster);
                        Console.print("you hit the %s for %d damage", monster.getType(), damage);
                        if (monster.isDead()) {
                            Console.print("it's dead, Jim!!");
                            player.killedA(monster.getType());
                        } else {
                            Console.print("the %s looks really mad now", monster.getType());
                        }
                        break;
                    case 'R':
                    case 'r':
                        playerRunsAway = true;
                        Console.print("you go and hide in a corner of the room until the %s falls asleep; " +
                                "you may now creep into another room", monster.getType());
                        break;
                    default:
                        throw new IllegalArgumentException(action + " is an invalid choice; isValidMove() failed");
                }
            }
        } else {
            Console.print("it's dead, Jim"); // star trek (original series) reference
        }
    }

    private static void playerFindsLoot(Player player, Room room) {
        int gold = room.getGold();
        if (gold > 0) {
            int adjustedGold = player.onLoot(gold); // thieves have a gold-multiplier (shrug)
            room.clearGold();   // so the player can only take the gold once :)
            Console.print("you loot %d gold; money, money, money!", adjustedGold);
        } else {
            boolean useHealth = player.onHeal(room.getHealth());
            if (useHealth) {
                Console.print("you feel refweshed!");
                room.clearHealth();
            } else {
                Console.print("you don't need it; leaving healing potion there for later");
            }
        }
    }
}
