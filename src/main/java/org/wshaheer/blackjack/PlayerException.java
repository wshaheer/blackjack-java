package org.wshaheer.blackjack;

public class PlayerException extends Exception {
    private static final long serialVersionUID = -1710920225661730009L;

    private final PlayerActionError error;

    public PlayerException(PlayerActionError error) {
        super(error.getMessage());

        this.error = error;
    }

    public PlayerActionError getError() {
        return error;
    }
}
