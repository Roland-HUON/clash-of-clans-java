package com.rolandhuon.clashofclans.exceptions;

public class InsufficientResourcesException extends RuntimeException {

    public InsufficientResourcesException(String resource, long available, long required) {
        super("Not enough " + resource + ": " + available + " available, " + required + " required.");
    }
}
