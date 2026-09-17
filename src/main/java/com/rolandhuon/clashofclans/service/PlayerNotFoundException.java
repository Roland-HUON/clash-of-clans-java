package com.rolandhuon.clashofclans.service;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(Long id) {
        super("Player " + id + " not found.");
    }
}
