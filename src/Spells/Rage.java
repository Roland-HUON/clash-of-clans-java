package Spells;

import Troops.Troop;

public class Rage implements Spell {
    private static int maxLevel = 7;
    private static int ID_CPT = 1;
    private static int levelIncrease = 1;
    int levelDuration;

    int id;
    int level = 1;
    int speedBoost;
    int attackSpeedBoost;
    int attackBoost;
    String name;

    public Rage(int level, int speedBoost, int attackSpeedBoost, int attackBoost) {
        this.id = ID_CPT++;
        this.level = level;
        this.speedBoost = speedBoost;
        this.attackSpeedBoost = attackSpeedBoost;
        this.attackBoost = attackBoost;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSpeedBoost() {
        return speedBoost;
    }

    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
    }

    public int getAttackSpeedBoost() {
        return attackSpeedBoost;
    }

    public void setAttackSpeedBoost(int attackSpeedBoost) {
        this.attackSpeedBoost = attackSpeedBoost;
    }

    public int getAttackBoost() {
        return attackBoost;
    }

    public void setAttackBoost(int attackBoost) {
        this.attackBoost = attackBoost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevelDuration() {
        return levelDuration;
    }

    public void setLevelDuration(int levelDuration) {
        this.levelDuration = levelDuration;
    }

    @Override
    public void ability() {

    }
}
