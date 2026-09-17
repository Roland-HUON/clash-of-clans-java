package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.troop.Archer;
import com.rolandhuon.clashofclans.domain.troop.Barbarian;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TroopFactory {

    public Troop create(TroopType type, int level){
        return switch (type){
            case BARBARIAN -> new Barbarian(level);
            case ARCHER -> new Archer(level);
        };
    }

    public List<Troop> create(TroopType type, int level, int count) {
        if(count <= 0) throw new IllegalArgumentException("Add more of this troop pls.");
        List<Troop> troops = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Troop t = create(type, level);
            troops.add(t);
        }
        return troops;
    }
}
