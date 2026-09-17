package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.PlayerTroop;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final PlayerRepository playerRepository;

    public DataSeeder(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (playerRepository.count() > 0) {
            System.out.println("[seed] Database already populated, nothing to do.");
            return;
        }

        playerRepository.save(newPlayer("Roland", 12, 250_000, 120_000, 2_000, 1_450,
                "Northern Stronghold", 40_000, 25_000, 800, 5, 4));

        playerRepository.save(newPlayer("Ada", 9, 80_000, 60_000, 500, 1_120,
                "Analytical Valley", 18_000, 12_000, 300, 3, 3));

        playerRepository.save(newPlayer("Grace", 7, 30_000, 20_000, 100, 860,
                "Compiler Hill", 9_000, 6_000, 120, 2, 2));

        System.out.println("[seed] 3 players created, each with 1 village, 2 buildings and 2 troops.");
    }

    private Player newPlayer(String name, int level, int gold, int elixir, int darkElixir, int trophies,
                             String villageName, int villageGold, int villageElixir, int villageDarkElixir,
                             int labLevel, int hallLevel) {

        Player player = new Player(name, level, gold, elixir, darkElixir, trophies);

        Village village = new Village(villageName, villageGold, villageElixir, villageDarkElixir);
        village.addBuilding(new VillageBuilding(BuildingType.LABORATORY, labLevel));
        village.addBuilding(new VillageBuilding(BuildingType.HEROHALL, hallLevel));
        player.addVillage(village);

        player.addTroop(new PlayerTroop(TroopType.BARBARIAN, 1));
        player.addTroop(new PlayerTroop(TroopType.ARCHER, 1));

        return player;
    }
}
