package com.rolandhuon.clashofclans.service;

public class PlayerNotFoundException extends NotFoundException {

    public PlayerNotFoundException(Long id) {
        super("Player", id);
    }
}
