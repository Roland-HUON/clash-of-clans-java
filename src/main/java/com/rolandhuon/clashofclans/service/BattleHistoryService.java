package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.model.BattleRecord;
import com.rolandhuon.clashofclans.repository.BattleRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BattleHistoryService {

    private final BattleRecordRepository battleRecordRepository;

    public BattleHistoryService(BattleRecordRepository battleRecordRepository) {
        this.battleRecordRepository = battleRecordRepository;
    }

    public BattleRecord save(BattleResult result){
        BattleRecord battleRecord = new BattleRecord(
                Instant.now(),
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
