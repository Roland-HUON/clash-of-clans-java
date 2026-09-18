package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.config.BattleProperties;
import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.battle.UnitDiedEvent;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.DefensiveBuilding;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.troop.StandardTroop;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.domain.village.Village;
import com.rolandhuon.clashofclans.service.targeting.DefenseFirstTargeting;
import com.rolandhuon.clashofclans.service.targeting.FirstAliveTargeting;
import com.rolandhuon.clashofclans.service.targeting.ResourceFirstTargeting;
import com.rolandhuon.clashofclans.service.targeting.WeakestFirstTargeting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BattleServiceTest {

    private final List<Object> publishedEvents = new ArrayList<>();
    private final CombatService combatService = new CombatService(publishedEvents::add);

    private BattleService battleService(int maxTurns, TargetingStrategy... strategies) {
        return new BattleService(combatService, List.of(strategies), new BattleProperties(maxTurns));
    }

    private BattleService fullyEquipped(int maxTurns) {
        return battleService(maxTurns,
                new FirstAliveTargeting(),
                new WeakestFirstTargeting(),
                new DefenseFirstTargeting(),
                new ResourceFirstTargeting());
    }

    private Village hallThenLaboratory() {
        Village village = new Village();
        village.addBuilding(new HeroHall(1));
        village.addBuilding(new Laboratory(1));
        return village;
    }

    private Troop troop(TroopType type, int level) {
        return new StandardTroop(type, level);
    }

    private List<Troop> barbarians(int count) {
        List<Troop> army = new ArrayList<>();
        for (int i = 0; i < count; i++) army.add(troop(TroopType.BARBARIAN, 1));
        return army;
    }

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("Every targeting mode used by a troop type must have a strategy.")
        void acceptsACompleteSetOfStrategies() {
            assertThatCode(() -> fullyEquipped(50)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("A missing strategy is rejected at construction, not during a battle.")
        void rejectsAnIncompleteSetOfStrategies() {
            assertThatThrownBy(() -> battleService(50, new FirstAliveTargeting()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("WEAKEST_FIRST")
                    .hasMessageContaining("DEFENSE_FIRST");
        }

        @Test
        @DisplayName("No strategy at all is rejected too.")
        void rejectsAnEmptySetOfStrategies() {
            assertThatThrownBy(() -> battleService(50))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("fight")
    class Fight {

        @Test
        @DisplayName("An empty army ends the battle immediately.")
        void emptyArmyEndsImmediately() {
            BattleResult result = fullyEquipped(50).fight(List.of(), hallThenLaboratory());

            assertThat(result.turns()).isZero();
            assertThat(result.destructionPercentage()).isZero();
            assertThat(result.villageDestroyed()).isFalse();
        }

        @Test
        @DisplayName("A battle that cannot be won stops at the configured turn limit.")
        void weakArmyStopsAtTurnLimit() {
            BattleResult result = fullyEquipped(10).fight(barbarians(1), hallThenLaboratory());

            assertThat(result.turns()).isEqualTo(10);
            assertThat(result.villageDestroyed()).isFalse();
            assertThat(result.survivingTroops()).isEqualTo(1);
        }

        @Test
        @DisplayName("A strong enough army razes the village before the limit.")
        void strongArmyRazesTheVillage() {
            BattleResult result = fullyEquipped(200).fight(barbarians(20), hallThenLaboratory());

            assertThat(result.destructionPercentage()).isEqualTo(100);
            assertThat(result.stars()).isEqualTo(3);
            assertThat(result.villageDestroyed()).isTrue();
            assertThat(result.turns()).isLessThan(200);
        }

        @Test
        @DisplayName("Each troop picks its target according to its own targeting mode.")
        void targetingDependsOnTheTroopType() {
            int laboratoryHp = BuildingType.LABORATORY.hpAt(1);
            int heroHallHp = BuildingType.HEROHALL.hpAt(1);
            int archerDps = TroopType.ARCHER.statsAt(1).dps();
            int barbarianDps = TroopType.BARBARIAN.statsAt(1).dps();

            int maxTurns = (laboratoryHp + archerDps - 1) / archerDps + 5;

            assertThat((long) archerDps * maxTurns)
                    .as("the archer must be able to finish the laboratory")
                    .isGreaterThanOrEqualTo(laboratoryHp);
            assertThat((long) barbarianDps * maxTurns)
                    .as("neither troop may bring the hero hall down")
                    .isLessThan(heroHallHp);

            BattleResult withBarbarian = fullyEquipped(maxTurns)
                    .fight(List.of(troop(TroopType.BARBARIAN, 1)), hallThenLaboratory());
            BattleResult withArcher = fullyEquipped(maxTurns)
                    .fight(List.of(troop(TroopType.ARCHER, 1)), hallThenLaboratory());

            assertThat(withBarbarian.destructionPercentage())
                    .as("FIRST_ALIVE keeps hitting the hero hall it cannot destroy")
                    .isZero();
            assertThat(withArcher.destructionPercentage())
                    .as("WEAKEST_FIRST finishes the laboratory instead")
                    .isEqualTo(50);
        }

        @Test
        @DisplayName("A destroyed building publishes a UnitDiedEvent.")
        void publishesAnEventWhenABuildingFalls() {
            fullyEquipped(200).fight(barbarians(20), hallThenLaboratory());

            assertThat(publishedEvents)
                    .hasSize(2)
                    .allMatch(UnitDiedEvent.class::isInstance);
        }
    }

    @Nested
    @DisplayName("defences")
    class Defences {

        private Village villageWithACannon() {
            Village village = new Village();
            village.addBuilding(new DefensiveBuilding(BuildingType.CANNON, 1));
            return village;
        }

        @Test
        @DisplayName("A defence shoots back at the army on every turn.")
        void aDefenceShootsBack() {
            Troop barbarian = troop(TroopType.BARBARIAN, TroopType.BARBARIAN.maxLevel());
            int maxHp = barbarian.getMaxHp();

            fullyEquipped(1).fight(List.of(barbarian), villageWithACannon());

            assertThat(barbarian.getHp()).isEqualTo(maxHp - BuildingType.CANNON.damageAt(1));
        }

        @Test
        @DisplayName("A village without any defence never damages the army.")
        void aVillageWithoutDefencesNeverShootsBack() {
            Troop barbarian = troop(TroopType.BARBARIAN, 1);

            fullyEquipped(5).fight(List.of(barbarian), hallThenLaboratory());

            assertThat(barbarian.getHp()).isEqualTo(barbarian.getMaxHp());
        }

        @Test
        @DisplayName("A destroyed defence stops shooting.")
        void aDestroyedDefenceStopsShooting() {
            Village village = villageWithACannon();
            Troop barbarian = troop(TroopType.BARBARIAN, TroopType.BARBARIAN.maxLevel());

            BattleResult result = fullyEquipped(500).fight(List.of(barbarian), village);

            assertThat(result.villageDestroyed())
                    .as("the barbarian must outlast the cannon for this test to mean anything")
                    .isTrue();
            assertThat(barbarian.getHp())
                    .as("the cannon stopped firing when it fell")
                    .isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("support troops")
    class Support {

        @Test
        @DisplayName("A healer restores hit points to the weakest ally instead of attacking.")
        void aHealerHealsTheWeakestAlly() {
            Troop barbarian = troop(TroopType.BARBARIAN, 1);
            Troop healer = troop(TroopType.HEALER, 1);
            int wounded = 5;
            barbarian.takeDamage(barbarian.getMaxHp() - wounded);

            fullyEquipped(1).fight(List.of(barbarian, healer), hallThenLaboratory());

            assertThat(barbarian.getHp())
                    .isEqualTo(Math.min(barbarian.getMaxHp(), wounded + healer.getDps()));
        }

        @Test
        @DisplayName("Healing never goes above the maximum hit points.")
        void healingIsCappedAtTheMaximum() {
            Troop barbarian = troop(TroopType.BARBARIAN, 1);
            Troop healer = troop(TroopType.HEALER, 1);
            barbarian.takeDamage(1);

            fullyEquipped(3).fight(List.of(barbarian, healer), hallThenLaboratory());

            assertThat(barbarian.getHp()).isEqualTo(barbarian.getMaxHp());
        }

        @Test
        @DisplayName("A healer alone does not damage the village.")
        void aHealerAloneDoesNotDamageTheVillage() {
            BattleResult result = fullyEquipped(50)
                    .fight(List.of(troop(TroopType.HEALER, 1)), hallThenLaboratory());

            assertThat(result.destructionPercentage()).isZero();
            assertThat(result.turns()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("air and ground")
    class AirAndGround {

        private Village villageOf(BuildingType type) {
            Village village = new Village();
            village.addBuilding(new DefensiveBuilding(type, 1));
            return village;
        }

        @Test
        @DisplayName("A village that only has ground defences cannot touch an air army.")
        void groundDefencesCannotTouchAnAirArmy() {
            Troop dragon = troop(TroopType.DRAGON, 1);

            fullyEquipped(3).fight(List.of(dragon), villageOf(BuildingType.CANNON));

            assertThat(dragon.getHp()).isEqualTo(dragon.getMaxHp());
        }

        @Test
        @DisplayName("An air defence cannot touch a ground army.")
        void anAirDefenceCannotTouchAGroundArmy() {
            Troop barbarian = troop(TroopType.BARBARIAN, 1);

            fullyEquipped(3).fight(List.of(barbarian), villageOf(BuildingType.AIR_DEFENSE));

            assertThat(barbarian.getHp()).isEqualTo(barbarian.getMaxHp());
        }

        @Test
        @DisplayName("An air defence does hit an air army.")
        void anAirDefenceHitsAnAirArmy() {
            Troop dragon = troop(TroopType.DRAGON, 1);

            fullyEquipped(1).fight(List.of(dragon), villageOf(BuildingType.AIR_DEFENSE));

            assertThat(dragon.getHp()).isEqualTo(dragon.getMaxHp() - BuildingType.AIR_DEFENSE.damageAt(1));
        }

        @Test
        @DisplayName("A defence that cannot reach anyone does not block the defences that can.")
        void anUnreachableArmyDoesNotStopTheOtherDefences() {
            Village village = new Village();
            village.addBuilding(new DefensiveBuilding(BuildingType.AIR_DEFENSE, 1));
            village.addBuilding(new DefensiveBuilding(BuildingType.CANNON, 1));
            Troop barbarian = troop(TroopType.BARBARIAN, TroopType.BARBARIAN.maxLevel());

            fullyEquipped(1).fight(List.of(barbarian), village);

            assertThat(barbarian.getHp())
                    .as("the air defence skips its turn, the cannon still fires")
                    .isEqualTo(barbarian.getMaxHp() - BuildingType.CANNON.damageAt(1));
        }
    }
}
