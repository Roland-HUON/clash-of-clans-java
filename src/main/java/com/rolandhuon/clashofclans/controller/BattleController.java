package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.rolandhuon.clashofclans.domain.battle.BattleKind;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Battles", description = "Sandbox fights: an army you describe against a village you describe, with nothing persisted but the result.")
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

    @Operation(summary = "Simulate a battle",
            description = "Runs one battle between an army and a village built from the body alone. Nothing is charged, no loot is taken and no trophy changes hands; only the outcome is kept in the history.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The battle was resolved."),
            @ApiResponse(responseCode = "400", description = "Unknown troop or building type, malformed body, or a building placed beyond its maximum count."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping
    public BattleResultDto fight(@Valid @RequestBody BattleRequest request){
        List<Troop> army = new ArrayList<>();
        for(UnitRequest u : request.army()){
            var type = TroopType.from(u.type());
            var units = troopFactory.create(type, u.level(), u.count());
            army.addAll(units);
        }
        Village village = new Village();

        for(BuildingRequest b : request.village()){
            var type = BuildingType.from(b.type());
            var building = buildingFactory.create(type, b.level());
            village.addBuilding(building);
        }

        BattleResult result = battleService.fight(army, village);
        battleHistoryService.save(BattleKind.SIMULATION, result);
        return BattleResultDto.from(result);
    }

    @Operation(summary = "List past battles",
            description = "Returns every battle recorded so far, oldest first, each one tagged SIMULATION or RAID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping
    public List<BattleHistoryDto> findAll(){
        return battleHistoryService.findAll()
                .stream()
                .map(x -> BattleHistoryDto.from(x))
                .toList();
    }
}
