package org.wshaheer.blackjack;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ListIterator;

public class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    private static Deck deck;
    private static Hand dealer;
    private static Player player;

    public static void main(String[] args) {
        deck = new Deck();
        dealer = new Hand(deck);

        start();

        do {
            LOGGER.info(String.format("DEALER CARDS: %s", dealer.getCards().get(0).toString()));

            play();
            deal();
            end();
        } while (next());
    }

    private static void start() {
        setup();
        bet();
    }

    private static void end() {
        player.hands.iterator().forEachRemaining(hand -> {
            int score = hand.getPoints();

            if (score > dealer.getPoints() || (hand.hasBlackJack() && dealer.hasBlackJack())) {
                int earnings = player.didWin();
                String position = player.hands.size() == 1 ? ""
                        : String.format("#%d hand", player.hands.indexOf(hand) + 1);

                LOGGER.info(String.format("HAND %s EARNED %d (CAD)", position, earnings));
            } else if (score == 0 || score < dealer.getPoints()) {
                player.didLose();

                LOGGER.info("PLAYER LOST");
            } else {
                LOGGER.info("PLAYER TIED");
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
        String balance = String.format("%d (CAD)", player.getBets() - player.getFunds());

        if (!balance.contains("-")) {
            balance = String.format("+%s", balance);
        }

        if (player.getBets() > 0) {
            String decision = query("PLAY AGAIN? (y/n)\n> ")
                    .trim()
                    .toLowerCase();

            String[] actions = {"y", "yes", "1", "true"};

            if (StringUtils.indexOfAny(decision, actions) >= 0) {
                player.reset();

                return true;
            } else {
                LOGGER.info(String.format("PLAYER BALANCE: %s (CAD)", balance));

                return false;
            }
        } else {
            LOGGER.info("PLAYER BALANCE: 0 (CAD)");

            return false;
        }
    }

    private static void setup() {
        do {
            try {
                int funds = Integer.parseInt(query("Enter fund amount (CAD)\n> "));

                if (funds < 50) {
                    LOGGER.warn("Value must be at least 50\n");
                } else {
                    player = new Player(funds, deck);
                    break;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Value must be an integer\n");
            }
        } while (true);
    }

    private static void bet() {
        LOGGER.info("*****PLAYER BET*****");

        do {
            try {
                int amount = Integer.parseInt(query("Enter bet amount (CAD)\n> "));

                if (amount > player.getFunds()) {
                    LOGGER.warn("Value must be less than OR equal to available funds");
                } else if (amount < 1) {
                    LOGGER.warn("Value must be greater than 0");
                } else {
                    player.setBets(amount);
                    break;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Value must be an integer");
            }
        } while (true);
    }

    private static void play() {
        boolean doubled = false;
        boolean split = false;

        Hand hand = player.hands.get(0);
        List<Card> cards = hand.getCards();

        LOGGER.info("*****PLAYER TURN*****");
        LOGGER.info(String.format("PLAYER FUNDS: %d (CAD)", player.getFunds()));
        LOGGER.info(String.format("PLAYER CARDS: %s and %s (%d)", cards.get(0), cards.get(1), hand.getPoints()));

        ListIterator<Hand> iterator = player.hands.listIterator();

        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Hand h = iterator.next();

            while (!concluded(hand) && (!doubled || hand.getCards().size() < 3)) {
                boolean breaking = false;
                String decision = query("Available actions: (h)it, (s)tand, (sp)lit, (d)ouble, (su)rrender\n> ")
                        .trim()
                        .toLowerCase();

                if (split) {
                    LOGGER.info(String.format("PLAYER HAND: #%d", index));
                    LOGGER.info(String.format("PLAYER CARDS: %s", hand.toString()));
                }

                switch (decision) {
                    case "h":
                    case "hit":
                        player.hit(index);

                        LOGGER.info("PLAYER ACTION: HIT");
                        LOGGER.info(String.format("PLAYER CARDS: %s", h.toString()));

                        break;

                    case "s":
                    case "stand":
                        breaking = true;

                        LOGGER.info("PLAYER ACTION: STAND");
                        break;

                    case "sp":
                    case "split":
                        if (!split) {
                            try {
                                split = true;

                                player.split();
                                LOGGER.info("PLAYER ACTION: SPLIT");
                            } catch (PlayerException e) {
                                split = false;

                                LOGGER.warn(e.getMessage());
                            }
                        } else {
                            LOGGER.warn("NOT ALLOWED: HAND SPLIT");
                        }

                        break;

                    case "d":
                    case "double":
                        if (!doubled) {
                            try {
                                doubled = true;

                                player.doubleDown();
                                LOGGER.info("PLAYER ACTION: DOUBLED");
                            } catch (PlayerException e) {
                                LOGGER.warn(e.getMessage());
                            }
                        } else {
                            LOGGER.warn("NOT ALLOWED: ALREADY DOUBLED");
                        }

                        break;

                    case "su":
                    case "surrender":
                        if (!doubled) {
                            try {
                                doubled = true;
                                breaking = true;

                                player.surrender();
                                LOGGER.info("PLAYER ACTION: SURRENDER");
                            } catch (PlayerException e) {
                                LOGGER.warn(e.getMessage());
                            }
                        } else {
                            LOGGER.warn("NOT ALLOWED: HAND DOUBLED");
                        }

                        break;

                    default:
                        LOGGER.warn("INVALID ENTRY: Available actions: (h)it, (s)tand, (sp)lit, (su)rrender\n> ");
                }

                if (breaking) {
                    break;
                }
            }
        }
    }

    private static void deal() {
        LOGGER.info("*****DEALER TURN*****");
        LOGGER.info(String.format("DEALER CARDS: %s and %s", dealer.getCards().get(0), dealer.getCards().get(1)));

        while (!didDealerBust() && dealer.getPoints() < 17) {
            dealer.deal();

            LOGGER.info("DEALER ACTION: HIT");
            LOGGER.info(String.format("DEALER CARDS: %s and %s", dealer.getCards().get(0), dealer.getCards().get(1)));
        }
    }

    // player result
    private static boolean concluded(Hand hand) {
        int score = hand.getPoints();

        if (score == 21) {
            if (hand.hasBlackJack()) {
                LOGGER.info("PLAYER POINTS: BLACK JACK");
            } else {
                LOGGER.info(String.format("PLAYER POINTS: %d", hand.getPoints()));
            }

            return true;
        } else if (score == 0) {
            LOGGER.info("PLAYER BUST");

            return true;
        }

        return false;
    }

    private static boolean didDealerBust() {
        if (dealer.getPoints() == 0) {
            LOGGER.info("DEALER BUST");

            return true;
        } else {
            return false;
        }
    }

    private static String query(String question) {
        String message = question.concat("\n> ");
        String line = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (line.length() < 1) {
                LOGGER.info(message);

                line = reader.readLine();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return line;
    }
}
