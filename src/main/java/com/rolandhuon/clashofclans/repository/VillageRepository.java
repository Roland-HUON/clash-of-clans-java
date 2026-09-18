package com.rolandhuon.clashofclans.repository;

import com.rolandhuon.clashofclans.model.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VillageRepository extends JpaRepository<Village, Long> {

    @Query("select distinct v from Village v join fetch v.player left join fetch v.buildings order by v.id")
    List<Village> findAllWithBuildings();

    @Query("select distinct v from Village v join fetch v.player p left join fetch v.buildings where p.id = :playerId order by v.id")
    List<Village> findByPlayerIdWithBuildings(@Param("playerId") Long playerId);

    @Query("select v from Village v join fetch v.player left join fetch v.buildings where v.id = :id")
    Optional<Village> findByIdWithBuildings(@Param("id") Long id);
}
