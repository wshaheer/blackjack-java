package org.wshaheer.blackjack;

public interface IPlayer {

    void hit(int index);

    void split() throws PlayerException;

    void doubleDown() throws PlayerException;

    void surrender() throws PlayerException;
}
