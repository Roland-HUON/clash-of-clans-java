package com.rolandhuon.clashofclans.domain.battle;

import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.village.Village;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class BattleResultTest {
    @ParameterizedTest
    @CsvSource({"0,0", "49,0", "50,1", "74,1", "75,2", "99,2", "100,3"})
    @DisplayName("Only pass the cap of 50/75/100 % percentage of destruction give another star.")
    void scaleOfStars(int percentage, int stars){
        BattleResult result = new BattleResult(percentage, 1, 0);
        assertThat(result.stars()).isEqualTo(stars);
    }

    @Test
    @DisplayName("The village is destroyed is true only when percentage destruction is at 100%.")
    void villageDestroyTrueWhen100PercentageDestruction(){
        Village village = new Village();
        var laboratory = new Laboratory(5);
        village.addBuilding(laboratory);

        laboratory.takeDamage(999);

        assertThat(new BattleResult(100, 1, 0).villageDestroyed()).isTrue();
        assertThat(new BattleResult(99, 1, 0).villageDestroyed()).isFalse();
    }

    @Test
    @DisplayName("Percentage out of 0 to 100 is rejected")
    void percentageOutOfBoundsIsRejected() {
        assertThatThrownBy(() -> new BattleResult(101, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new BattleResult(-1, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Turns and remaining troops negative values is prohibited")
    void negativeValueForTurnsAndRemainingTroopsIsRejected() {
        assertThatThrownBy(() -> new BattleResult(100, -1, 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new BattleResult(0, 1, -10))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
