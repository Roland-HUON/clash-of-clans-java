package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.dto.RaidUnitRequest;
import com.rolandhuon.clashofclans.dto.UnitRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("the size of an army")
class TroopFactoryTest {

    private final TroopFactory factory = new TroopFactory();

    @Test
    @DisplayName("The ceiling is the largest army the game can ever host.")
    void theCeilingIsTheGameCeiling() {
        BuildingType camp = BuildingType.MILITARY_CAMP;
        int hostable = camp.housingCapacityAt(camp.maxLevel()) * camp.maxCount();

        assertThat(TroopType.MAX_ARMY_SIZE)
                .as("%d camps of %d housing space", camp.maxCount(), camp.housingCapacityAt(camp.maxLevel()))
                .isEqualTo(hostable);
    }

    @Test
    @DisplayName("An army may be as large as that ceiling.")
    void theCeilingItselfIsAllowed() {
        assertThat(factory.create(TroopType.BARBARIAN, 1, TroopType.MAX_ARMY_SIZE))
                .hasSize(TroopType.MAX_ARMY_SIZE);
    }

    @Test
    @DisplayName("One unit past the ceiling is refused before anything is allocated.")
    void pastTheCeilingIsRefused() {
        assertThatThrownBy(() -> factory.create(TroopType.BARBARIAN, 1, TroopType.MAX_ARMY_SIZE + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(TroopType.MAX_ARMY_SIZE));
    }

    @Test
    @DisplayName("A count of a billion is refused, not attempted.")
    void aBillionUnitsIsRefused() {
        assertThatThrownBy(() -> factory.create(TroopType.BARBARIAN, 1, 1_000_000_000))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("An empty army is refused.")
    void anEmptyArmyIsRefused() {
        assertThatThrownBy(() -> factory.create(TroopType.BARBARIAN, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("The API refuses an oversized count before a controller ever sees it.")
    void validationStopsAnOversizedCount() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            int tooMany = TroopType.MAX_ARMY_SIZE + 1;

            assertThat(validator.validate(new UnitRequest("BARBARIAN", 1, tooMany)))
                    .as("POST /api/battles")
                    .isNotEmpty();
            assertThat(validator.validate(new RaidUnitRequest("BARBARIAN", tooMany)))
                    .as("POST /api/raids")
                    .isNotEmpty();

            assertThat(validator.validate(new UnitRequest("BARBARIAN", 1, TroopType.MAX_ARMY_SIZE))).isEmpty();
            assertThat(validator.validate(new RaidUnitRequest("BARBARIAN", TroopType.MAX_ARMY_SIZE))).isEmpty();
        }
    }
}
