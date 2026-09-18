package com.rolandhuon.clashofclans.exceptions;

public class PlayerNotFoundException extends NotFoundException {

    public PlayerNotFoundException(Long id) {
        super("Player", id);
    }
}
