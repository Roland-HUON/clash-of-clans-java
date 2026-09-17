package com.rolandhuon.clashofclans.repository;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.model.PlayerTroop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerTroopRepository extends JpaRepository<PlayerTroop, Long> {

    List<PlayerTroop> findByPlayerId(Long playerId);

    Optional<PlayerTroop> findByPlayerIdAndType(Long playerId, TroopType type);
}
