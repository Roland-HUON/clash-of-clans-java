package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.PlayerTroop;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final long UNLIMITED = 999_999_999_999L;

    private record Chief(String name, String villageName, int trophies) {}

    private static final List<Chief> ROSTER = List.of(
            new Chief("Dutofa", "Dutofa Keep", 4931),
            new Chief("Ada", "Analytical Valley", 4720),
            new Chief("Linus", "Kernel Ridge", 4588),
            new Chief("Grace", "Compiler Hill", 4460),
            new Chief("Margaret", "Apollo Bastion", 4341),
            new Chief("Katherine", "Orbit Point", 4218),
            new Chief("Alan", "Enigma Hollow", 4102),
            new Chief("Barbara", "Voyager Camp", 3988),
            new Chief("Donald", "Knuth Gardens", 3871),
            new Chief("Edsger", "Shortest Path", 3760),
            new Chief("Ken", "Bell Harbour", 3645),
            new Chief("Dennis", "Pointer Cliff", 3534),
            new Chief("Bjarne", "Template Reach", 3421),
            new Chief("James", "Bytecode Bay", 3318),
            new Chief("Guido", "Indent Falls", 3207),
            new Chief("Yukihiro", "Ruby Shores", 3099),
            new Chief("Anders", "Delegate Fort", 2984),
            new Chief("Rasmus", "Elephant Ford", 2877),
            new Chief("Brendan", "Prototype Pass", 2766),
            new Chief("John", "Lambda Grove", 2658),
            new Chief("Niklaus", "Pascal Spire", 2549),
            new Chief("Ole", "Simula Sound", 2441),
            new Chief("Kristen", "Object Cove", 2333),
            new Chief("Adele", "Dispatch Downs", 2228),
            new Chief("Tim", "Hyperlink Isle", 2117),
            new Chief("Vint", "Packet Reach", 2008),
            new Chief("Radia", "Spanning Tree", 1901),
            new Chief("Leslie", "Timestamp Tor", 1795),
            new Chief("Shafi", "Cipher Vault", 1688),
            new Chief("Whitfield", "Key Exchange", 1580),
            new Chief("Ronald", "Prime Quarry", 1474),
            new Chief("Frances", "Fortran Flats", 1369),
            new Chief("Jean", "Ada Overlook", 1262),
            new Chief("Mary", "Debug Den", 1155),
            new Chief("Carol", "Batch Basin", 1050),
            new Chief("Betty", "Eniac Yard", 944),
            new Chief("Marlyn", "Punchcard Post", 838),
            new Chief("Ruth", "Ballistic Bluff", 733),
            new Chief("Jean-Bartik", "Vacuum Vale", 627),
            new Chief("Klara", "Monte Carlo", 522),
            new Chief("Hedy", "Frequency Fen", 417),
            new Chief("Steve", "Garage Green", 355),
            new Chief("Bill", "Basement Bay", 296),
            new Chief("Linus-Jr", "Fork Field", 241),
            new Chief("Ada-Jr", "First Loop", 188),
            new Chief("Tommy", "Sandbox Shore", 140),
            new Chief("Nora", "Novice Nook", 96),
            new Chief("Pim", "Starter Strand", 55),
            new Chief("Wil", "Rookie Rock", 20)
    );

    private final PlayerRepository playerRepository;
    private final Clock clock;

    public DataSeeder(PlayerRepository playerRepository, Clock clock) {
        this.playerRepository = playerRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (playerRepository.count() > 0) {
            log.info("Database already populated, nothing to do.");
            return;
        }

        List<Player> players = new ArrayList<>();
        players.add(champion());
        ROSTER.forEach(chief -> players.add(challenger(chief)));

        playerRepository.saveAll(players);
        log.info("{} players created, Roland on top with 5110 trophies.", players.size());
    }

    private Player champion() {
        Player roland = new Player("Roland", 20, UNLIMITED, UNLIMITED, UNLIMITED, 5110);
        Village village = new Village("Northern Stronghold", 2_000_000, 1_500_000, 60_000);

        maxOut(village);
        roland.addVillage(village);
        unlock(roland, Integer.MAX_VALUE);

        return roland;
    }

    private Player challenger(Chief chief) {
        int tier = tierOf(chief.trophies());

        Player player = new Player(chief.name(), tier + 4,
                12_000L * tier * tier, 9_000L * tier * tier, 400L * tier, chief.trophies());

        Village village = new Village(chief.villageName(),
                6_000L * tier, 4_000L * tier, 150L * tier);

        equip(village, tier);
        player.addVillage(village);
        unlock(player, Math.max(1, tier - 1));

        return player;
    }

    private int tierOf(int trophies) {
        if (trophies >= 4500) return 6;
        if (trophies >= 3500) return 5;
        if (trophies >= 2500) return 4;
        if (trophies >= 1500) return 3;
        if (trophies >= 700) return 2;
        return 1;
    }

    private void equip(Village village, int tier) {
        add(village, BuildingType.LABORATORY, Math.min(tier * 2, BuildingType.LABORATORY.maxLevel()), 1);
        add(village, BuildingType.HEROHALL, Math.min(tier * 2, BuildingType.HEROHALL.maxLevel()), 1);
        add(village, BuildingType.SPELL_FACTORY, 1, 1);
        add(village, BuildingType.PET_HOUSE, 1, 1);

        add(village, BuildingType.CANNON, capped(BuildingType.CANNON, tier), Math.min(tier, 4));
        add(village, BuildingType.ARCHER_TOWER, capped(BuildingType.ARCHER_TOWER, tier), Math.min(tier, 4));
        add(village, BuildingType.MORTAR, capped(BuildingType.MORTAR, tier - 1), Math.max(1, tier - 2));
        add(village, BuildingType.WIZARD_TOWER, capped(BuildingType.WIZARD_TOWER, tier - 2), Math.max(0, tier - 3));
        add(village, BuildingType.AIR_DEFENSE, capped(BuildingType.AIR_DEFENSE, tier - 1), Math.max(0, tier - 2));
        add(village, BuildingType.HIDDEN_TESLA, capped(BuildingType.HIDDEN_TESLA, tier - 2), Math.max(0, tier - 3));
        add(village, BuildingType.MONOLITH, capped(BuildingType.MONOLITH, tier - 3), tier >= 5 ? 1 : 0);

        add(village, BuildingType.GOLD_MINE, capped(BuildingType.GOLD_MINE, tier), Math.min(tier, 4));
        add(village, BuildingType.ELIXIR_COLLECTOR, capped(BuildingType.ELIXIR_COLLECTOR, tier), Math.min(tier, 4));
        add(village, BuildingType.GOLD_STORAGE, capped(BuildingType.GOLD_STORAGE, tier - 1), Math.max(1, tier - 3));
        add(village, BuildingType.ELIXIR_STORAGE, capped(BuildingType.ELIXIR_STORAGE, tier - 1), Math.max(1, tier - 3));

        add(village, BuildingType.DARK_ELIXIR_DRILL, capped(BuildingType.DARK_ELIXIR_DRILL, tier - 2), Math.max(0, tier - 3));
        add(village, BuildingType.DARK_ELIXIR_STORAGE, capped(BuildingType.DARK_ELIXIR_STORAGE, tier - 2), tier >= 4 ? 1 : 0);

        add(village, BuildingType.MILITARY_CAMP, capped(BuildingType.MILITARY_CAMP, tier * 2), Math.min(tier, 4));
    }

    private void maxOut(Village village) {
        for (BuildingType type : BuildingType.values()) {
            add(village, type, type.maxLevel(), type.maxCount());
        }
    }

    private int capped(BuildingType type, int level) {
        return Math.max(1, Math.min(level, type.maxLevel()));
    }

    private void add(Village village, BuildingType type, int level, int count) {
        Instant now = clock.instant();
        for (int i = 0; i < Math.min(count, type.maxCount()); i++) {
            village.addBuilding(new VillageBuilding(type, level, now));
        }
    }

    private void unlock(Player player, int level) {
        for (TroopType type : TroopType.values()) {
            player.addTroop(new PlayerTroop(type, Math.max(1, Math.min(level, type.maxLevel()))));
        }
    }
}
