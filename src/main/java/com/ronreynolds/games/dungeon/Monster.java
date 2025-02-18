package com.ronreynolds.games.dungeon;

import java.util.concurrent.ThreadLocalRandom;

public class Monster {
    private static final int MIN_DAMAGE = 0;    // not sure if this should be 0 or 1 (vague requirements) :-/

    enum MonsterType {
        goblin (6, 10),
        zombie (12, 15),
        orc (18, 20),
        sizemogre (55, 5);

        final int maxDamage;
        final int maxHealth;
        MonsterType(int maxHealth, int maxDamage) {
            this.maxDamage = maxDamage;
            this.maxHealth = maxHealth;
        }

    }

    public static Monster generateRandomMonster() {
        switch(ThreadLocalRandom.current().nextInt(0, 4)) {
            case 0:
                return new Monster(MonsterType.goblin);
            case 1:
                return new Monster(MonsterType.zombie);
            case 2:
                return new Monster(MonsterType.orc);
            case 3:
                return new Monster(MonsterType.sizemogre);
            default:
                throw new IllegalStateException("impossible random number outsize 0...3");
        }
    }

    private int health;
    private final int maxDamage;
    private final MonsterType monsterType;

    public Monster(MonsterType type) {
        monsterType = type;
        maxDamage = type.maxDamage;
        health = type.maxHealth;
    }

    public void onHit(int damage) {
        this.health -= damage;
    }

    public void attack(Player target) {
        int damage = ThreadLocalRandom.current().nextInt(MIN_DAMAGE, maxDamage);
        target.onHit(damage);
    }
    public boolean isDead() {
        return health <= 0;
    }
}
