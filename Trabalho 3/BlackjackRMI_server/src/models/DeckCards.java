package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckCards {
    private List<Card> deck = new ArrayList<>();

    public DeckCards() {
        String[] suit = {"Copas", "Espadas", "Paus", "Ouros"};
        String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};

        for (String s : suit) {
            for (String v : values) {
                deck.add(new Card(v, s));
            }
        }

        Collections.shuffle(deck); // embaralhar deck
    }

    public Card buyCard() {
        return deck.removeFirst(); //pegar carta no topo do deck
    }
}
