package org.wshaheer.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> stack;

    public Deck() {
        this.stack = new ArrayList<>(8);

        for (int i = 0; i < 8; i++) {
            for (Suit s: Suit.values()) {
                for (Rank r: Rank.values()) {
                    this.stack.add(new Card(r, s));
                }
            }
        }

        Collections.shuffle(this.stack);
    }

    public Card deal() {
        return this.stack.remove(this.stack.size() - 1);
    }

    public List<Card> distribute() {
        ArrayList<Card> distribution = new ArrayList<>(2);

        distribution.add(this.stack.remove(this.stack.size() - 1));
        distribution.add(this.stack.remove(this.stack.size() - 1));

        return distribution;
    }
}
