import Heros.Hero;
import Spells.Spell;
import Troops.Troop;

import java.util.List;

public class Player {
    private static int ID_CPT = 1;

    int id;
    int level;
    List<Troop> troops;
    List<Spell> spells;
    List<Hero> heros;

    public Player(int level, List<Troop> troops, List<Spell> spells, List<Hero> heros) {
        this.id = ID_CPT++;
        this.level = level;
        this.troops = troops;
        this.spells = spells;
        this.heros = heros;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public List<Spell> getSpells() {
        return spells;
    }

    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }

    public List<Hero> getHeros() {
        return heros;
    }

    public void setHeros(List<Hero> heros) {
        this.heros = heros;
    }
}
