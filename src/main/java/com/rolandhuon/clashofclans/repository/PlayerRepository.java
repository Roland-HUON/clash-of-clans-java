package com.rolandhuon.clashofclans.repository;

import com.rolandhuon.clashofclans.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByName(String name);

    List<Player> findAllByOrderByTrophiesDescNameAsc();

    List<Player> findAllByOrderByIdAsc();
}
