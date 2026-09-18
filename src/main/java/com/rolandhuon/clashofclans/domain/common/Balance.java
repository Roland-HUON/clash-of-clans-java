package com.rolandhuon.clashofclans.domain.common;

public final class Balance {

    private Balance() {}

    public static long plus(long balance, long amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be >= 0");
        return balance > Long.MAX_VALUE - amount ? Long.MAX_VALUE : balance + amount;
    }
}
