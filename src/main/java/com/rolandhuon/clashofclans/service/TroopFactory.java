package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.troop.StandardTroop;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TroopFactory {

    public Troop create(TroopType type, int level){
        return new StandardTroop(type, level);
    }

    public List<Troop> create(TroopType type, int level, int count) {
        if(count <= 0) throw new IllegalArgumentException("Count must be at least 1.");
        if(count > TroopType.MAX_ARMY_SIZE) {
            throw new IllegalArgumentException(
                    "Count must be at most " + TroopType.MAX_ARMY_SIZE + ", the largest army the game can host.");
        }
        List<Troop> troops = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            troops.add(create(type, level));
        }
        return troops;
    }
}
