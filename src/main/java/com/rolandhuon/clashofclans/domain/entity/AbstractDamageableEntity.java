package com.rolandhuon.clashofclans.domain.entity;

import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.common.EntityType;

import java.util.Objects;

public abstract class AbstractDamageableEntity<T extends EntityType> implements Damageable {
    private final int id;
    private final T type;
    private static int ID_CPT = 1;
    private int level, maxHp, hp;

    protected AbstractDamageableEntity(T type, int level) {
        Objects.requireNonNull(type);
        this.id = ID_CPT++;
        this.type = type;
        applyLevel(level);
    }

    public int getId() {
        return id;
    }

    public T getType(){
        return type;
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

    public String getName(){
        return type.label() + " lv." + level;
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

    public void upgrade(){
        if(level >= type.maxLevel()) throw new IllegalStateException("Already max level.");
        applyLevel(++level);
    }

    protected void applyLevel(int newLevel){
        this.level = newLevel;
        this.maxHp = type.hpAt(newLevel);
        this.hp = this.maxHp;
    }

    public void onDeath(){
        this.hp = 0;
    }
}
