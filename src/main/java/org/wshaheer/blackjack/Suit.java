package org.wshaheer.blackjack;

public enum Suit {
    SPADES("♠"),
    DIAMONDS("♦"),
    CLUBS("♣"),
    HEARTS("♥");

    public final String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }
}
