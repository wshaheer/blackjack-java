package org.wshaheer.blackjack;

import java.util.List;

public class Hand {
    private int points;
    private Integer aces;
    private List<Card> cards;
    private Deck deck;

    public Hand(Deck deck) {
        this.aces = 0;
        this.deck = deck;
        this.cards = this.deck.distribute();
        this.points = calculatePoints();

        checkForAces();
        evaluateAcePoints();
    }

    public Hand(Deck deck, List<Card> cards) {
        this.aces = 0;
        this.deck = deck;
        this.cards = cards;
        this.points = calculatePoints();

        checkForAces();
        evaluateAcePoints();
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void initialize() {
        this.aces = 0;
        this.cards = this.deck.distribute();
        this.points = calculatePoints();


    }

    public void deal() {
        cards.add(deck.deal());
        checkForAces();
        updatePoints();
    }

    private int calculatePoints() {
        int score = 0;

        for (Card c : cards) {
            score += c.getRank().value;
        }

        return score;
    }

    private void checkForAces() {
        for (Card c : cards) {
            if (c.getRank().label.equals("ACE")) {
                aces++;
            }
        }
    }

    private void evaluateAcePoints() {
        while (points > 21 && aces > 0) {
            points -= 10;
            aces--;
        }
    }

    private void updatePoints() {
        points = calculatePoints();
    }
}
