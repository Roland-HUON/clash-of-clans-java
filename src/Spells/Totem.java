package Spells;

public class Totem {
    private static int maxLevel = 7;
    private static int ID_CPT = 1;
    private static int levelIncrease = 1;
    int levelDuration;

    int id;
    int level = 1;
    int pv;
    double stunDuration = 0.5;
    String name;

    public Totem(int levelDuration, int level, int pv, double stunDuration, String name) {
        this.levelDuration = levelDuration;
        this.id = ID_CPT++;
        this.level = level;
        this.pv = pv;
        this.stunDuration = stunDuration;
        this.name = name;
    }

    public int getLevelDuration() {
        return levelDuration;
    }

    public void setLevelDuration(int levelDuration) {
        this.levelDuration = levelDuration;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public double getStunDuration() {
        return stunDuration;
    }

    public void setStunDuration(double stunDuration) {
        this.stunDuration = stunDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
