package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.troop.StandardTroop;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefensiveBuildingTest {

    @ParameterizedTest
    @EnumSource(value = BuildingType.class, names = {"CANNON", "ARCHER_TOWER", "MONOLITH", "RICOCHET_CANNON"})
    @DisplayName("A defensive building fires with the damage of its own level.")
    void damageFollowsTheLevel(BuildingType type) {
        DefensiveBuilding building = new DefensiveBuilding(type, 1);

        assertThat(building.getDps()).isEqualTo(type.damageAt(1));

        building.upgrade();

        assertThat(building.getDps()).isEqualTo(type.damageAt(2));
    }

    @Test
    @DisplayName("Attacking removes exactly the damage of the building from the target.")
    void attackDealsItsDamage() {
        DefensiveBuilding cannon = new DefensiveBuilding(BuildingType.CANNON, 1);
        Laboratory target = new Laboratory(1);
        int expected = target.getMaxHp() - cannon.getDps();

        cannon.attack(target);

        assertThat(target.getHp()).isEqualTo(expected);
    }

    @Test
    @DisplayName("A destroyed building cannot fire anymore.")
    void aDestroyedBuildingCannotAttack() {
        DefensiveBuilding cannon = new DefensiveBuilding(BuildingType.CANNON, 1);
        Laboratory target = new Laboratory(1);
        cannon.takeDamage(cannon.getMaxHp());

        assertThatThrownBy(() -> cannon.attack(target))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Hitting an already destroyed target is a no-op.")
    void attackingADestroyedTargetDoesNothing() {
        DefensiveBuilding cannon = new DefensiveBuilding(BuildingType.CANNON, 1);
        Laboratory target = new Laboratory(1);
        target.takeDamage(target.getMaxHp());

        cannon.attack(target);

        assertThat(target.getHp()).isZero();
    }

    @Test
    @DisplayName("A building without a damage table cannot be a defence.")
    void rejectsANonDefensiveType() {
        assertThatThrownBy(() -> new DefensiveBuilding(BuildingType.GOLD_MINE, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(BuildingType.GOLD_MINE.label());
    }

    @Test
    @DisplayName("A cannon cannot reach an airborne troop.")
    void aCannonCannotReachTheAir() {
        DefensiveBuilding cannon = new DefensiveBuilding(BuildingType.CANNON, 1);
        Troop dragon = new StandardTroop(TroopType.DRAGON, 1);

        assertThat(cannon.canTarget(dragon)).isFalse();

        cannon.attack(dragon);

        assertThat(dragon.getHp()).isEqualTo(dragon.getMaxHp());
    }

    @Test
    @DisplayName("An air defence cannot reach a troop on the ground.")
    void anAirDefenceCannotReachTheGround() {
        DefensiveBuilding airDefense = new DefensiveBuilding(BuildingType.AIR_DEFENSE, 1);
        Troop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);

        assertThat(airDefense.canTarget(barbarian)).isFalse();

        airDefense.attack(barbarian);

        assertThat(barbarian.getHp()).isEqualTo(barbarian.getMaxHp());
    }

    @Test
    @DisplayName("An archer tower hits the ground and the air alike.")
    void anArcherTowerHitsBoth() {
        DefensiveBuilding tower = new DefensiveBuilding(BuildingType.ARCHER_TOWER, 1);
        Troop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);
        Troop dragon = new StandardTroop(TroopType.DRAGON, 1);

        tower.attack(barbarian);
        tower.attack(dragon);

        assertThat(barbarian.getHp()).isEqualTo(barbarian.getMaxHp() - tower.getDps());
        assertThat(dragon.getHp()).isEqualTo(dragon.getMaxHp() - tower.getDps());
    }

    @Test
    @DisplayName("A building is never mistaken for a flying troop.")
    void aBuildingIsAlwaysReachable() {
        DefensiveBuilding airDefense = new DefensiveBuilding(BuildingType.AIR_DEFENSE, 1);

        assertThat(airDefense.canTarget(new Laboratory(1))).isTrue();
    }
}
