package com.ronreynolds.games.dungeon;

import com.ronreynolds.games.util.Console;
import com.ronreynolds.games.util.RandomUtil;

public class Monster {
    private static final int MIN_DAMAGE = 1;

    public enum MonsterType {
        goblin(6, 10),
        zombie(12, 15),
        orc(18, 20),
        ogre(55, 5);

        final int maxDamage;
        final int maxHealth;

        MonsterType(int maxHealth, int maxDamage) {
            this.maxDamage = maxDamage;
            this.maxHealth = maxHealth;
        }
    }

    public static Monster generateRandomMonster() {
        switch (RandomUtil.randomPositiveIntLessThan(4)) {
            case 0:
                return new Monster(MonsterType.goblin);
            case 1:
                return new Monster(MonsterType.zombie);
            case 2:
                return new Monster(MonsterType.orc);
            case 3:
                return new Monster(MonsterType.ogre);
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

    public MonsterType getType() {
        return monsterType;
    }

    public void onHit(int damage) {
        this.health -= damage;
    }

    /**
     * monster attacks player
     *
     * @return the damage done
     */
    public int attack(Player target) {
        int damage = RandomUtil.randomIntBetween(MIN_DAMAGE, maxDamage);
        target.onHit(damage);
        return damage;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void print() {
        if (isDead()) {
            Console.print("a dead %s", monsterType);
        } else {
            Console.print("%s with %d HP (does %d max-damage)", monsterType, health, maxDamage);
        }
    }
}
