package com.rolandhuon.clashofclans.domain.village;

import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VillageTest {
    @Test
    @DisplayName("New Village is not destroyed and has not targets alives.")
    void newVillageNotDestroyedOrHaveTargets(){
        Village village = new Village();
        assertThat(village.isDestroyed())
                .isFalse();
        assertThat(village.destructionPercentage())
                .isZero();
        assertThat(village.aliveTargets())
                .isEmpty();
    }

    @Test
    @DisplayName("Add a 2nd laboratory is illegal.")
    void add2LaboratoryIsIllegal(){
        Village village = new Village();
        var laboratory1 = new Laboratory(10);
        var laboratory2 = new Laboratory(10);

        village.addBuilding(laboratory1);

        assertThatThrownBy(() -> village.addBuilding(laboratory2))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Cannot modifiable aliveTargets function from outside.")
    void aliveTargetsIsUnmodifiable(){
        Village village = new Village();
        var laboratory1 = new Laboratory(10);
        village.addBuilding(laboratory1);

        List<Damageable> targets = village.aliveTargets();

        assertThatThrownBy(() -> targets.add(new HeroHall(1)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Add a laboratory and a hero hall is ok and targets alives is 2.")
    void addLaboratoryAndHeroHallOk(){
        Village village = new Village();
        var laboratory = new Laboratory(10);
        var herohall = new HeroHall(10);
        village.addBuilding(laboratory);
        village.addBuilding(herohall);

        assertThat(village.aliveTargets()).hasSize(2);
    }

    @Test
    @DisplayName("Destroy a building in a village with only 2 alives targets give 50% destruction, not destroyed and target alive remaining is equal to 1.")
    void destroyVillageWith2BuildingAndOnly1RemainingGive50(){
        Village village = new Village();
        var laboratory = new Laboratory(10);
        var herohall = new HeroHall(10);
        village.addBuilding(laboratory);
        village.addBuilding(herohall);

        laboratory.takeDamage(laboratory.getHp());

        assertThat(village.destructionPercentage()).isEqualTo(50);
        assertThat(village.isDestroyed()).isFalse();
        assertThat(village.aliveTargets()).hasSize(1);
    }

    @Test
    @DisplayName("Destroy 2 buildings in a village with only 2 alives targets give 100% destruction, the village is destroyed and target alive remaining is equal to 0.")
    void destroyVillageWith2Building(){
        Village village = new Village();
        var laboratory = new Laboratory(10);
        var herohall = new HeroHall(10);
        village.addBuilding(laboratory);
        village.addBuilding(herohall);

        laboratory.takeDamage(laboratory.getHp());
        herohall.takeDamage(herohall.getHp());

        assertThat(village.destructionPercentage()).isEqualTo(100);
        assertThat(village.isDestroyed()).isTrue();
        assertThat(village.aliveTargets()).hasSize(0);
    }
}
