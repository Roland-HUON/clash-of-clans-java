package com.rolandhuon.clashofclans.service;

public class InsufficientResourcesException extends RuntimeException {

    public InsufficientResourcesException(String resource, long available, long required) {
        super("Not enough " + resource + ": " + available + " available, " + required + " required.");
    }
}
