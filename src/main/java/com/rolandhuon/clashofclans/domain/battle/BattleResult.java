package com.rolandhuon.clashofclans.domain.battle;

public record BattleResult(int destructionPercentage, int turns, int survivingTroops) {

    public BattleResult{
        if(destructionPercentage < 0 || destructionPercentage > 100) throw new IllegalArgumentException("Invalid percentage");
        if(turns < 0 ) throw new IllegalArgumentException("Invalid number of turns");
        if(survivingTroops < 0 ) throw new IllegalArgumentException("Invalid number of remaining troops");
    }
    public int stars(){
        int percentage = destructionPercentage;
        if(percentage == 100) {
            return 3;
        }
        if(percentage >= 75) return 2;
        if(percentage >= 50) return 1;
        return 0;
    }

    public boolean villageDestroyed(){
        return destructionPercentage == 100;
    }
}
