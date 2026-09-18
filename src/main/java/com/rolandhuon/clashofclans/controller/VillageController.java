package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.rolandhuon.clashofclans.dto.BuildingRequest;
import com.rolandhuon.clashofclans.dto.VillageBuildingDto;
import com.rolandhuon.clashofclans.dto.VillageDto;
import com.rolandhuon.clashofclans.dto.VillageRequest;
import com.rolandhuon.clashofclans.dto.HarvestDto;
import com.rolandhuon.clashofclans.service.ProductionService;
import com.rolandhuon.clashofclans.service.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Villages", description = "The bases a chief owns, and the buildings standing in them.")
@RestController
public class VillageController {

    private final VillageService villageService;
    private final ProductionService productionService;

    public VillageController(VillageService villageService, ProductionService productionService) {
        this.villageService = villageService;
        this.productionService = productionService;
    }

        @Operation(summary = "List every village",
            description = "Returns every village on the server, ordered by id, each with its buildings and its stock of lootable resources. This is what a client uses to pick a raid target without asking for each chief in turn.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/api/villages")
    public List<VillageDto> all() {
        return villageService.findAll().stream()
                .map(village -> VillageDto.from(village, productionService.pending(village)))
                .toList();
    }

    @Operation(summary = "List a player's villages",
            description = "Returns every village owned by that chief, each with its buildings and its stock of lootable resources.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/api/players/{playerId}/villages")
    public List<VillageDto> byPlayer(@PathVariable Long playerId) {
        return villageService.findByPlayer(playerId).stream()
                .map(village -> VillageDto.from(village, productionService.pending(village)))
                .toList();
    }

        @Operation(summary = "Found a village",
            description = "Creates an empty village for that chief, with the starting resources given in the body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The village was created and is returned with its generated id."),
            @ApiResponse(responseCode = "400", description = "The body is malformed, or a field breaks its validation rule."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping("/api/players/{playerId}/villages")
    @ResponseStatus(HttpStatus.CREATED)
    public VillageDto create(@PathVariable Long playerId, @Valid @RequestBody VillageRequest request) {
        return VillageDto.from(villageService.create(playerId, request));
    }

        @Operation(summary = "Read one village",
            description = "Returns the village with every building: its level, hit points, camp capacity and the cost of its next upgrade.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No village carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/api/villages/{id}")
    public VillageDto find(@PathVariable Long id) {
        var village = villageService.findById(id);
        return VillageDto.from(village, productionService.pending(village));
    }

        @Operation(summary = "Raze a village",
            description = "Deletes the village and every building in it. The owner keeps their resources.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The village was deleted; nothing is returned."),
            @ApiResponse(responseCode = "404", description = "No village carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @DeleteMapping("/api/villages/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        villageService.delete(id);
    }

        @Operation(summary = "Collect the mines",
            description = "Empties every producer in the village - Gold Mine, Elixir Collector and Dark Elixir Drill - of what it has made since the last collection. The amount lands both in the owner's purse, where it can be spent, and in the village's stock, where raiders can reach it. A producer stops filling once it holds six hours of its own output.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The mines were emptied; what was collected is returned."),
            @ApiResponse(responseCode = "404", description = "No village carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping("/api/villages/{id}/collect")
    public HarvestDto collect(@PathVariable Long id) {
        return HarvestDto.from(productionService.collect(id));
    }

    @Operation(summary = "Put up a building",
            description = "Charges the owner the build cost of that type and adds the building to the village. A type cannot be built beyond its maximum count.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The building was put up and is returned with its generated id."),
            @ApiResponse(responseCode = "400", description = "Unknown building type, malformed body, or that type has reached its maximum count."),
            @ApiResponse(responseCode = "404", description = "No village carries that id."),
            @ApiResponse(responseCode = "409", description = "The owner cannot afford the build cost."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping("/api/villages/{villageId}/buildings")
    @ResponseStatus(HttpStatus.CREATED)
    public VillageBuildingDto addBuilding(@PathVariable Long villageId, @Valid @RequestBody BuildingRequest request) {
        return VillageBuildingDto.from(villageService.addBuilding(villageId, request));
    }
}
