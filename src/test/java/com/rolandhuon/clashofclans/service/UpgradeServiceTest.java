package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerTroopRepository;
import com.rolandhuon.clashofclans.repository.VillageBuildingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("upgrading a producer")
class UpgradeServiceTest {

    private static final Instant BUILT_AT = Instant.parse("2026-09-18T08:00:00Z");
    private static final long PURSE = 100_000_000L;

    private record Fixture(UpgradeService service, Village village, Player player, VillageBuilding mine) {}

    private Fixture fixtureAt(int level, Duration waited) {
        Player player = new Player("Chief", 10, PURSE, PURSE, PURSE, 0);
        Village village = new Village("Home", 0, 0, 0);
        VillageBuilding mine = new VillageBuilding(BuildingType.GOLD_MINE, level, BUILT_AT);

        village.addBuilding(mine);
        player.addVillage(village);

        VillageBuildingRepository buildings = mock(VillageBuildingRepository.class);
        when(buildings.findById(any())).thenReturn(Optional.of(mine));
        when(buildings.save(any())).thenAnswer(call -> call.getArgument(0));

        Clock clock = Clock.fixed(BUILT_AT.plus(waited), ZoneOffset.UTC);
        UpgradeService service =
                new UpgradeService(buildings, mock(PlayerTroopRepository.class), clock);

        return new Fixture(service, village, player, mine);
    }

    @Test
    @DisplayName("What the mine produced is paid at the rate it was produced at, not at the new one.")
    void theAccruedHourIsPricedAtTheOldRate() {
        int level = 5;
        Fixture fixture = fixtureAt(level, Duration.ofHours(1));
        int oldRate = BuildingType.GOLD_MINE.productionPerHourAt(level);
        int newRate = BuildingType.GOLD_MINE.productionPerHourAt(level + 1);

        fixture.service().upgradeBuilding(1L);

        assertThat(newRate)
                .as("the test is only meaningful while the two levels differ")
                .isGreaterThan(oldRate);
        assertThat(fixture.village().getGold())
                .as("one hour at level %d is %d gold, never %d", level, oldRate, newRate)
                .isEqualTo(oldRate);
    }

    @Test
    @DisplayName("The upgrade leaves nothing behind to collect a second time.")
    void theUpgradeEmptiesTheMine() {
        Fixture fixture = fixtureAt(5, Duration.ofHours(1));

        fixture.service().upgradeBuilding(1L);

        assertThat(fixture.mine().pendingProduction(BUILT_AT.plus(Duration.ofHours(1)))).isZero();
    }

    @Test
    @DisplayName("Upgrading twice in a row cannot mint a second hour.")
    void upgradingTwiceMintsNothing() {
        int level = BuildingType.GOLD_MINE.maxLevel() - 2;
        Fixture fixture = fixtureAt(level, Duration.ofHours(1));

        fixture.service().upgradeBuilding(1L);
        fixture.service().upgradeBuilding(1L);

        assertThat(fixture.village().getGold())
                .isEqualTo(BuildingType.GOLD_MINE.productionPerHourAt(level));
    }

    @Test
    @DisplayName("Upgrading something that produces nothing banks nothing.")
    void upgradingANonProducerBanksNothing() {
        Player player = new Player("Chief", 10, PURSE, PURSE, PURSE, 0);
        Village village = new Village("Home", 0, 0, 0);
        VillageBuilding cannon = new VillageBuilding(BuildingType.CANNON, 1, BUILT_AT);

        village.addBuilding(cannon);
        player.addVillage(village);

        VillageBuildingRepository buildings = mock(VillageBuildingRepository.class);
        when(buildings.findById(any())).thenReturn(Optional.of(cannon));
        when(buildings.save(any())).thenAnswer(call -> call.getArgument(0));

        UpgradeService service = new UpgradeService(buildings, mock(PlayerTroopRepository.class),
                Clock.fixed(BUILT_AT.plus(Duration.ofDays(1)), ZoneOffset.UTC));

        service.upgradeBuilding(1L);

        assertThat(village.getGold()).isZero();
        assertThat(cannon.getLevel()).isEqualTo(2);
    }
}
