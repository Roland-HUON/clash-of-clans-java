package com.rolandhuon.clashofclans.service;

public class InsufficientResourcesException extends RuntimeException {

    public InsufficientResourcesException(String resource, int available, int required) {
        super("Not enough " + resource + ": " + available + " available, " + required + " required.");
    }
}
