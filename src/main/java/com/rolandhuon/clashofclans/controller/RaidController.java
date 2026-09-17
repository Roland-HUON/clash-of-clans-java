package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.dto.RaidRequest;
import com.rolandhuon.clashofclans.dto.RaidResultDto;
import com.rolandhuon.clashofclans.service.RaidService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/raids")
public class RaidController {

    private final RaidService raidService;

    public RaidController(RaidService raidService) {
        this.raidService = raidService;
    }

    @PostMapping
    public RaidResultDto raid(@RequestBody RaidRequest request) {
        return RaidResultDto.from(raidService.raid(request));
    }
}
