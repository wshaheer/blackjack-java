package org.wshaheer.blackjack;

public enum PlayerActionError {

    HIT("NOT ALLOWED: HAND ALREADY HIT"),
    SPLIT("NOT ALLOWED: HAND ALREADY SPLIT"),
    FUNDS("NOT ALLOWED: INSUFFICIENT FUNDS"),
    HANDS("NOT ALLOWED: INVALID HAND");

    private final String message;

    PlayerActionError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
