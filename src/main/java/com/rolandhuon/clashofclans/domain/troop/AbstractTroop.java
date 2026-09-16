package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.common.Damageable;

import java.util.Objects;

public abstract class AbstractTroop implements Troop {
    private final int id;
    private final TroopType type;
    private static int ID_CPT = 1;
    private int level, maxHp, hp, dps;

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getDps() {
        return dps;
    }

    protected AbstractTroop(TroopType type, int level){
        this.type = Objects.requireNonNull(type);
        this.id = ID_CPT++;
        applyLevel(level);
    }

    private void applyLevel(int newLevel){
        TroopStats stats = type.statsAt(newLevel);
        this.level = newLevel;
        this.maxHp = stats.hp();
        this.hp = this.maxHp;
        this.dps = stats.dps();
    }

    public String getName(){
        return type.label() + " lv." + level;
    }

    public void upgrade(){
        if(level >= type.maxLevel()) throw new IllegalStateException("Already max level.");
        applyLevel(++level);
    }

    public boolean isAlive(){
        return hp > 0;
    }

    public void takeDamage(int amount){
        if(amount < 0) throw new IllegalArgumentException("Damage > 0");
        if(!isAlive()) return;
        this.hp = Math.max(0, this.hp - amount);
        if(this.hp == 0) onDeath();
    }

    public void attack(Damageable target){
        Objects.requireNonNull(target, "Need target");
        if(!isAlive()) throw new IllegalStateException("This troop is dead");
        if(!target.isAlive()) return;
        target.takeDamage(getDps());
    }

    public void onDeath(){
        this.hp = 0;
    }

    public TroopType getType() {
        return type;
    }

    public int getHousingSpace(){
        return type.housingSpace();
    }
}
