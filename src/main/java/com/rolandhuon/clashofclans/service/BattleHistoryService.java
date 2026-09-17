package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.config.GameConfiguration;
import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.model.BattleRecord;
import com.rolandhuon.clashofclans.repository.BattleRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class BattleHistoryService {

    private final BattleRecordRepository battleRecordRepository;
    private final Clock clock;

    public BattleHistoryService(BattleRecordRepository battleRecordRepository, Clock clock) {
        this.battleRecordRepository = battleRecordRepository;
        this.clock = clock;
    }

    public BattleRecord save(BattleResult result){
        BattleRecord battleRecord = new BattleRecord(
                clock.instant(),
                result.destructionPercentage(),
                result.stars(),
                result.turns(),
                result.survivingTroops());
        return battleRecordRepository.save(battleRecord);
    }

    public List<BattleRecord> findAll(){
        return battleRecordRepository.findAll();
    }
}
