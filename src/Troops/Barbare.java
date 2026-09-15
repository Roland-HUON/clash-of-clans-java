package Troops;

public class Barbare implements Troop {
    private static final int maxLevel = 13;
    private static int ID_CPT = 1;

    int id;
    int pv;
    int attack;
    int housingSpace;
    int level;
    int levelDuration;
    String name;

    public Barbare(int pv, int attack, int housingSpace, int level, String name) {
        this.id = ID_CPT++;
        this.pv = pv;
        this.attack = attack;
        this.housingSpace = housingSpace;
        this.level = level;
        this.name = name;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int onDamage(int pv, int damage){
        this.pv = pv - damage;
        if(pv<0){
            throw new IllegalArgumentException();
            //onDeath();
        }
        return pv;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHousingSpace() {
        return housingSpace;
    }

    public void setHousingSpace(int housingSpace) {
        this.housingSpace = housingSpace;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void upgrade() {
        if(level >= .maxLevel()){
            throw new IllegalStateException(name + " is already at max level !");
        }
        this.level++;
        this.
    }

    @Override
    public void onAttack() {

    }

    @Override
    public void onDeath() {
        this.pv = 0;
    }
}
