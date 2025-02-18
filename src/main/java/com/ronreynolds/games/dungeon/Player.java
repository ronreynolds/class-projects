package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.Console;

public class Player {
    public enum PlayerClass {
        Warrior(100, 1.0, 15),
        Thief(70, 1.2, 10);

        final int maxHealth;
        final double lootMultiplier;
        final int maxDamage;

        PlayerClass(int maxHealth, double lootMultiplier, int maxDamage) {
            this.maxHealth = maxHealth;
            this.lootMultiplier = lootMultiplier;
            this.maxDamage = maxDamage;
        }

        public static PlayerClass select() {
            Console.print("select your class (W)arrior or (T)hief:");
            // keep prompting the user for input until they provide a word that starts with W or T
            char input = Console.readLineUntil(s -> !s.isBlank() && s.matches("[WwTt].*"), Console.stringToChar);
            switch (input) {
                case 'W':
                case 'w':
                    return Warrior;
                case 'T':
                case 't':
                    return Thief;
            }
            throw new IllegalStateException("somehow user entered a word that didn't start with W or T");
        }
    }

    private int health;
    private int gold;
    private int maxDamage;
    private PlayerClass playerClass;
    private double lootMultiplier;

    /**
     * create Player of the specified class
     */
    public Player(PlayerClass playerClass) {
        this.playerClass = playerClass;
        health = playerClass.maxHealth;
        lootMultiplier = playerClass.lootMultiplier;
        maxDamage = playerClass.maxDamage;
    }

    /**
     * player was hit for the specified damage
     */
    public void onHit(int damage) {
        this.health -= damage;
    }

    /**
     * player drank a healing potion; return true if they needed it
     */
    public boolean onHeal(int health) {
        boolean needsHealth = this.health < playerClass.maxHealth;
        if (needsHealth) {
            // Math.min ensures health will never be greater than max-health
            this.health = Math.min(playerClass.maxHealth, this.health + health);
        }
        return needsHealth;
    }

    /**
     * player received some loot to add to their bag
     *
     * @return how much they receive (adjusting for class)
     */
    public int onLoot(int gold) {
        // unfortunately multiplier is a double so we need to do some casting so the math works
        int adjustedGold = (int) Math.round((double) gold * lootMultiplier);
        this.gold += adjustedGold;
        return adjustedGold;
    }

    /**
     * player is attacking the specified monster
     */
    public int attack(Monster target) {
        int damage = this.maxDamage; // because the player is oh so awesome
        target.onHit(damage);
        return damage;
    }

    /**
     * return true if the player has no health left
     */
    public boolean isDead() {
        return this.health <= 0;
    }

    public int getGold() {
        return gold;
    }

    public int getHealth() {
        return health;
    }

    public void print() {
        Console.print("GP = %d (%d to go)%nHP = %d", this.gold, DungeonGame.WINNING_GOLD - this.gold, this.health);
    }
}
