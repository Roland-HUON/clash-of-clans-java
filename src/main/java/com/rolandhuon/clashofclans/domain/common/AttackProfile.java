package com.rolandhuon.clashofclans.domain.common;

public record AttackProfile(double range, AttackType type, double splashRadius) {
    public AttackProfile{
        if(range < 0) throw new IllegalArgumentException("Range >= 0 ");
        if(type == AttackType.SPLASH && splashRadius <= 0) throw new IllegalArgumentException("A splash attacker needs a radius above zero");
        if(type == AttackType.SINGLE && splashRadius != 0) throw new IllegalArgumentException("Splash radius for single has to be 0");
    }

    public static AttackProfile single(double range){
        return new AttackProfile(range, AttackType.SINGLE, 0);
    }

    public static AttackProfile splash(double range, double radius){
        return new AttackProfile(range, AttackType.SPLASH, radius);
    }

    public boolean isSplash(){
        return type == AttackType.SPLASH;
    }

    public int splashTargets(){
        return isSplash() ? 1 + (int) Math.round(splashRadius) : 0;
    }
}
