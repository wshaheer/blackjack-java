package org.wshaheer.blackjack;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ListIterator;

public class Application {

    private static Deck deck;
    private static Hand dealer;
    private static Player player;

    public static void main(String[] args) {
        deck = new Deck();
        dealer = new Hand(deck);

        start();

        do {
            System.out.printf("DEALER CARDS: %s%n", dealer.getCards().get(0).toString());

            play();
            deal();
            stop();
        } while (next());
    }

    private static synchronized void start() {
        do {
            try {
                int funds = Integer.parseInt(query("Enter fund amount (CAD)"));

                if (funds < 50) {
                    System.out.println("Value must be at least 50");
                } else {
                    player = new Player(funds, deck);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Value must be an integer");
            }
        } while (true);
    }

    private static void stop() {
        player.hands.iterator().forEachRemaining(hand -> {
            int score = hand.getPoints();

            if (score > dealer.getPoints() || (hand.blackjack() && dealer.blackjack())) {
                int earnings = player.won();

                System.out.printf("PLAYER EARNED %d (CAD)%n", earnings);
            } else if (score == 0 || score < dealer.getPoints()) {
                player.lost();
                
                System.out.println("PLAYER LOST");
            } else {
                System.out.println("PLAYER TIED");
            }
        });
    }

    private static boolean next() {
        if (reset()) {
            dealer.initialize();

            return true;
        } else {
            return false;
        }
    }

    private static boolean reset() {
        int balance = player.getBets() - player.getFunds();

        if (player.getBets() > 0) {
            String decision = query("PLAY AGAIN? (y/n)")
                    .trim()
                    .toLowerCase();

            String[] actions = {"y", "yes", "1", "true"};

            if (StringUtils.indexOfAny(decision, actions) >= 0) {
                player.reset();

                return true;
            } else {
                System.out.printf("PLAYER BALANCE: %d (CAD)", balance);

                return false;
            }
        } else {
            System.out.println("PLAYER BALANCE: 0 (CAD)");

            return false;
        }
    }

    private static synchronized void bet() {
        System.out.println("*****PLAYER BET*****");

        while (true) {
            try {
                int amount = Integer.parseInt(query("Enter bet amount (CAD)"));

                if (amount > player.getFunds()) {
                    System.out.println("Value must be less than OR equal to available funds");
                } else if (amount < 1) {
                    System.out.println("Value must be greater than 0");
                } else {
                    player.setBets(amount);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Value must be an integer");
            }
        }
    }

    private static synchronized void play() {
        boolean doubled = false;
        boolean split = false;

        Hand hand = player.hands.get(0);
        List<Card> cards = hand.getCards();

        System.out.println("*****PLAYER TURN*****");
        System.out.printf("PLAYER FUNDS: %d (CAD)%n", player.getFunds());

        bet();

        System.out.printf("PLAYER CARDS: %s and %s (%d)%n", cards.get(0), cards.get(1), hand.getPoints());

        ListIterator<Hand> iterator = player.hands.listIterator();

        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Hand h = iterator.next();

            while (!concluded(hand) && (!doubled || hand.getCards().size() < 3)) {
                boolean breaking = false;
                String decision = query("Available actions: (h)it, (s)tand, (sp)lit, (d)ouble, (su)rrender")
                        .trim()
                        .toLowerCase();

                if (split) {
                    System.out.printf("PLAYER HAND: #%d%n", index);
                    System.out.printf("PLAYER CARDS: %s%n", hand.toString());
                }

                switch (decision) {
                    case "h":
                    case "hit":
                        player.hit(index);

                        System.out.println("PLAYER ACTION: HIT");
                        System.out.printf("PLAYER CARDS: %s%n", h.toString());

                        break;

                    case "s":
                    case "stand":
                        breaking = true;

                        System.out.println("PLAYER ACTION: STAND");
                        break;

                    case "sp":
                    case "split":
                        if (!split) {
                            try {
                                split = true;

                                player.split();
                                System.out.println("PLAYER ACTION: SPLIT");
                            } catch (PlayerException e) {
                                split = false;

                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("NOT ALLOWED: HAND SPLIT");
                        }

                        break;

                    case "d":
                    case "double":
                        if (!doubled) {
                            try {
                                doubled = true;

                                player.doubleDown();
                                System.out.println("PLAYER ACTION: DOUBLED");
                            } catch (PlayerException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("NOT ALLOWED: ALREADY DOUBLED");
                        }

                        break;

                    case "su":
                    case "surrender":
                        if (!doubled) {
                            try {
                                doubled = true;
                                breaking = true;

                                player.surrender();
                                System.out.println("PLAYER ACTION: SURRENDER");
                            } catch (PlayerException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("NOT ALLOWED: HAND DOUBLED");
                        }

                        break;

                    default:
                        System.out.println("INVALID ENTRY: Available actions: (h)it, (s)tand, (sp)lit, (su)rrender");
                }

                if (breaking) {
                    break;
                }
            }
        }
    }

    private static synchronized void deal() {
        System.out.println("*****DEALER TURN*****");
        System.out.printf("DEALER CARDS: %s and %s%n", dealer.getCards().get(0), dealer.getCards().get(1));

        while (!bust() && dealer.getPoints() < 17) {
            dealer.deal();

            System.out.println("DEALER ACTION: HIT");
            System.out.printf("DEALER CARDS: %s and %s%n", dealer.getCards().get(0), dealer.getCards().get(1));
        }
    }

    // player result
    private static boolean concluded(Hand hand) {
        int score = hand.getPoints();

        if (score == 21) {
            if (hand.blackjack()) {
                System.out.println("PLAYER POINTS: BLACK JACK");
            } else {
                System.out.printf("PLAYER POINTS: %d%n", hand.getPoints());
            }

            return true;
        } else if (score == 0) {
            System.out.println("PLAYER BUST");

            return true;
        }

        return false;
    }

    private static boolean bust() {
        if (dealer.getPoints() == 0) {
            System.out.println("DEALER BUST");

            return true;
        } else {
            return false;
        }
    }

    private static synchronized String query(String question) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = new String();

        System.out.println(question);

        try {
            while (input.length() < 1) {
                System.out.print(">");

                input = reader.readLine();
            }

            return input;
        } catch (IOException e) {
            System.out.println(e.getMessage());

            return new String();
        }
    }
}
