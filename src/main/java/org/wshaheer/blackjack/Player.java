package org.wshaheer.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Player implements IPlayer {

    public final List<Hand> hands;

    private int bets;
    private int funds;
    private Deck deck;

    public Player(int funds, Deck deck) {
        this.bets = 0;
        this.funds = funds;
        this.deck = deck;
        this.hands = new ArrayList<>();

        this.hands.add(new Hand(deck));
    }

    public int getBets() {
        return bets;
    }

    public void setBets(int bets) {
        this.bets = bets;
    }

    public int getFunds() {
        return funds;
    }

    public void setFunds(int funds) {
        this.funds = funds;
    }

    public void reset() {
        hands.iterator().forEachRemaining(Hand::initialize);
    }

    public int didWin() {
        int balance = funds;

        if (hands.get(0).hasBlackJack()) {
            funds += bets / 2;
        } else if (hands.size() > 1 && hands.get(1).hasBlackJack()) {
            funds += bets / 2;
        } else {
            funds += bets;
        }

        return funds - balance;
    }

    public void didLose() {
        funds -= bets;
    }

    @Override
    public String toString() {
        return String.format("PLAYER FUNDS: %d\nPLAYER CARDS: %s", funds, hands.get(0).toString());
    }

    @Override
    public void hit(int index) {
        hands.get(index).deal();
    }

    @Override
    public void split() throws PlayerException {
        final List<Card> firstHandCards = hands.get(0).getCards();

        List<Card> cards;

        if (bets * 2 > funds) {
            throw new PlayerException(PlayerActionError.FUNDS);
        } else if (hands.size() > 1) {
            throw new PlayerException(PlayerActionError.SPLIT);
        } else if (firstHandCards.size() != 2) {
            throw new PlayerException(PlayerActionError.HIT);
        } else if (!firstHandCards.get(0).getRank().label
                .equals(firstHandCards.get(1).getRank().label)) {
            throw new PlayerException(PlayerActionError.HANDS);
        } else {
            bets *= 2;
            cards = new ArrayList<>();

            cards.add(hands.get(0).getCards().remove(firstHandCards.size() - 1));
            cards.add(deck.deal());

            hands.add(new Hand(deck, cards));
            hands.get(0).deal();
        }
    }

    @Override
    public void doubleDown() throws PlayerException {
        if (bets * 2 > funds) {
            throw new PlayerException(PlayerActionError.FUNDS);
        } else if (hands.get(0).getCards().size() != 2) {
            throw new PlayerException(PlayerActionError.HIT);
        } else if (hands.size() > 1) {
            throw new PlayerException(PlayerActionError.SPLIT);
        } else {
            bets *= 2;
        }
    }

    @Override
    public void surrender() throws PlayerException {
        if (hands.get(0).getCards().size() != 2) {
            throw new PlayerException(PlayerActionError.HIT);
        } else if (hands.size() > 1) {
            throw new PlayerException(PlayerActionError.SPLIT);
        } else {
            bets /= 2;

            hands.get(0).setPoints(0);
        }
    }
}
