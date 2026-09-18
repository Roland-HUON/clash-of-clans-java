package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.DefensiveBuilding;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.troop.StandardTroop;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CombatServiceTest {

    private final List<Object> publishedEvents = new ArrayList<>();
    private final CombatService combatService = new CombatService(publishedEvents::add);

    @Nested
    @DisplayName("single target")
    class SingleTarget {

        @Test
        @DisplayName("A single-target attacker only hits what it aimed at.")
        void onlyTheAimedTargetIsHit() {
            Troop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);
            Laboratory aimed = new Laboratory(1);
            Laboratory bystander = new Laboratory(1);

            combatService.resolveAttack(barbarian, aimed, List.of(aimed, bystander));

            assertThat(aimed.getHp()).isEqualTo(aimed.getMaxHp() - barbarian.getDps());
            assertThat(bystander.getHp()).isEqualTo(bystander.getMaxHp());
        }

        @Test
        @DisplayName("A dead target is not hit again.")
        void aDeadTargetIsLeftAlone() {
            Troop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);
            Laboratory target = new Laboratory(1);
            target.takeDamage(target.getMaxHp());

            combatService.resolveAttack(barbarian, target, List.of(target));

            assertThat(publishedEvents).isEmpty();
        }
    }

    @Nested
    @DisplayName("splash")
    class Splash {

        @Test
        @DisplayName("A wizard catches one bystander beside its target.")
        void aWizardCatchesOneBystander() {
            Troop wizard = new StandardTroop(TroopType.WIZARD, 1);
            Laboratory aimed = new Laboratory(1);
            Laboratory caught = new Laboratory(1);
            Laboratory spared = new Laboratory(1);

            combatService.resolveAttack(wizard, aimed, List.of(aimed, caught, spared));

            assertThat(TroopType.WIZARD.attackProfile().splashTargets()).isEqualTo(1);
            assertThat(aimed.getHp()).isEqualTo(aimed.getMaxHp() - wizard.getDps());
            assertThat(caught.getHp()).isEqualTo(caught.getMaxHp() - wizard.getDps());
            assertThat(spared.getHp()).isEqualTo(spared.getMaxHp());
        }

        @Test
        @DisplayName("A balloon has a wider blast than a wizard.")
        void aBalloonHasAWiderBlast() {
            assertThat(TroopType.BALLOON.attackProfile().splashTargets())
                    .isGreaterThan(TroopType.WIZARD.attackProfile().splashTargets());

            Troop balloon = new StandardTroop(TroopType.BALLOON, 1);
            List<Damageable> field = List.of(new Laboratory(1), new Laboratory(1), new Laboratory(1), new Laboratory(1));

            combatService.resolveAttack(balloon, field.get(0), field);

            assertThat(field.stream().filter(b -> b.getHp() < ((Laboratory) b).getMaxHp()).count())
                    .isEqualTo(1 + TroopType.BALLOON.attackProfile().splashTargets());
        }

        @Test
        @DisplayName("Splash never reaches a unit the attacker cannot target.")
        void splashRespectsWhatTheAttackerCanReach() {
            DefensiveBuilding mortar = new DefensiveBuilding(BuildingType.MORTAR, 1);
            Troop barbarian = new StandardTroop(TroopType.BARBARIAN, TroopType.BARBARIAN.maxLevel());
            Troop dragon = new StandardTroop(TroopType.DRAGON, 1);

            combatService.resolveAttack(mortar, barbarian, List.of(barbarian, dragon));

            assertThat(dragon.getHp())
                    .as("a mortar cannot splash a flying dragon")
                    .isEqualTo(dragon.getMaxHp());
        }

        @Test
        @DisplayName("A kill by splash publishes its own event.")
        void aSplashKillIsPublished() {
            Troop dragon = new StandardTroop(TroopType.DRAGON, 1);
            Laboratory aimed = new Laboratory(1);
            Laboratory caught = new Laboratory(1);
            aimed.takeDamage(aimed.getMaxHp() - 1);
            caught.takeDamage(caught.getMaxHp() - 1);

            combatService.resolveAttack(dragon, aimed, List.of(aimed, caught));

            assertThat(aimed.isAlive()).isFalse();
            assertThat(caught.isAlive()).isFalse();
            assertThat(publishedEvents).hasSize(2);
        }
    }
}
