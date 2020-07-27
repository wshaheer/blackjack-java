package org.wshaheer.blackjack;

public class PlayerException extends Exception {
    private static final long serialVersionUID = -1710920225661730009L;

    public PlayerException(PlayerActionError error) {
        super(error.getMessage());
    }

    public PlayerException(String message) {
        super(message);
    }


    public PlayerException(Throwable cause) {
        super(cause);
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
