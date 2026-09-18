package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.model.Player;

public record PlayerDto(Long id, String name, int level, long gold, long elixir, long darkElixir, int trophies) {

    public static PlayerDto from(Player player){
        return new PlayerDto(player.getId(), player.getName(), player.getLevel(),
                player.getGold(), player.getElixir(), player.getDarkElixir(), player.getTrophies());
    }
}
