package com.rolandhuon.clashofclans.repository;

import com.rolandhuon.clashofclans.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByName(String name);

    List<Player> findAllByOrderByTrophiesDescNameAsc();

    List<Player> findAllByOrderByIdAsc();

    @Query("""
            select count(p) from Player p
            where p.trophies > :trophies or (p.trophies = :trophies and p.name < :name)
            """)
    long countAhead(@Param("trophies") int trophies, @Param("name") String name);
}
