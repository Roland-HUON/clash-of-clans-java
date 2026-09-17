package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.domain.village.Village;
import com.rolandhuon.clashofclans.dto.*;
import com.rolandhuon.clashofclans.service.BattleHistoryService;
import com.rolandhuon.clashofclans.service.BattleService;
import com.rolandhuon.clashofclans.service.BuildingFactory;
import com.rolandhuon.clashofclans.service.TroopFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/battles")
public class BattleController {

    private final BattleService battleService;
    private final TroopFactory troopFactory;
    private final BuildingFactory buildingFactory;
    private final BattleHistoryService battleHistoryService;

    public BattleController(BattleService battleService, TroopFactory troopFactory, BuildingFactory buildingFactory, BattleHistoryService battleHistoryService) {
        this.battleService = battleService;
        this.troopFactory = troopFactory;
        this.buildingFactory = buildingFactory;
        this.battleHistoryService = battleHistoryService;
    }

    @PostMapping
    public BattleResultDto fight(@RequestBody BattleRequest request){
        List<Troop> army = new ArrayList<>();
        for(UnitRequest u : request.army()){
            var type = TroopType.valueOf(u.type().toUpperCase());
            var units = troopFactory.create(type, u.level(), u.count());
            army.addAll(units);
        }
        Village village = new Village();

        for(BuildingRequest b : request.village()){
            var type = BuildingType.valueOf(b.type().toUpperCase());
            var building = buildingFactory.create(type, b.level());
            village.addBuilding(building);
        }

        BattleResult result = battleService.fight(army, village);
        battleHistoryService.save(result);
        return BattleResultDto.from(result);
    }

    @GetMapping
    public List<BattleHistoryDto> findAll(){
        return battleHistoryService.findAll()
                .stream()
                .map(x -> BattleHistoryDto.from(x))
                .toList();
    }
}
