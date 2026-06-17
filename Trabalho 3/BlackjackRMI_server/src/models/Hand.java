package models;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> hand = new ArrayList<>();

    public void addCard(Card card) {
        hand.add(card);
    }

    public int calculateScore() {
        int total = 0;
        int aces = 0;

        for (Card c : hand) {
            total += c.getNumericValue();

            if (c.toString().startsWith("A"))
                aces++;
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public List<Card> getCards() {
        return hand;
    }
}
