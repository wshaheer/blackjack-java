package org.wshaheer.blackjack;

import java.util.List;
import java.util.stream.Collectors;

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

        discoverAceCards();
        evaluateAcePoints();
    }

    public Hand(Deck deck, List<Card> cards) {
        this.aces = 0;
        this.deck = deck;
        this.cards = cards;
        this.points = calculatePoints();

        discoverAceCards();
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

        discoverAceCards();
        evaluateAcePoints();
    }

    public void deal() {
        cards.add(deck.deal());
        discoverAceCards();
        updatePoints();
    }

    public boolean hasBlackJack() {
        return cards.size() == 2 && points == 21;
    }

    @Override
    public String toString() {
        String cardsInHand = cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(", "));

        return String.format("%s (%d)", cardsInHand, points);
    }

    private int calculatePoints() {
        int score = 0;

        for (Card c : cards) {
            score += c.getRank().value;
        }

        return score;
    }

    private void discoverAceCards() {
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
