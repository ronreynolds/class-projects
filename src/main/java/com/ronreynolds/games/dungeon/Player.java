package com.ronreynolds.games.dungeon;

public class Player {
    private static final int WINNING_GOLD = 100;

    enum PlayerClass {
        Warrior(100, 1, 15),
        Thief(70, 1.2F, 10);

        PlayerClass(int maxHealth, float lootMultiplier, int maxDamage) {
            this.maxHealth = maxHealth;
            this.lootMultiplier = lootMultiplier;
            this.maxDamage = maxDamage;
        }

        public static PlayerClass select() {
            // TODO - ask player to pick class
            // return selected class
            return Warrior;
        }

        final int maxHealth;
        final float lootMultiplier;
        final int maxDamage;
    }

    private int health;
    private int gold;
    private int maxDamage;
    private PlayerClass playerClass;
    private float lootMultiplier;
    private int currentRow;
    private int currentCol;

    public Player(PlayerClass playerClass) {
        this.playerClass = playerClass;
        health = playerClass.maxHealth;
        lootMultiplier = playerClass.lootMultiplier;
        maxDamage = playerClass.maxDamage;
    }
    public void onHit(int damage) {
        this.health -= damage;
    }
    public void onHeal(int health) {
        health = Math.max(playerClass.maxHealth, this.health + health);
    }
    public void onLoot(int gold) {
        this.gold += gold * lootMultiplier;
    }
    public void attack(Monster target) {
        target.onHit(this.maxDamage);
    }
    public boolean isDead() {
        return this.health <= 0;
    }
    public boolean isRich() {
        return this.gold >= WINNING_GOLD;
    }
    public RoomCoordinates getRoomCoordinates() {
        return new RoomCoordinates(currentRow, currentCol);
    }
}
