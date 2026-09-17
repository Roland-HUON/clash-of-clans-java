package com.rolandhuon.clashofclans.repository;

import com.rolandhuon.clashofclans.model.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VillageRepository extends JpaRepository<Village, Long> {

    List<Village> findByPlayerId(Long playerId);
}
