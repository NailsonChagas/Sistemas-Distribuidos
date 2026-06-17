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

        /*
        * Inicialmente, todos os ases são considerados como valendo 11 pontos.
        * Caso a soma ultrapasse 21, cada iteração do laço reduz o valor de um
        * Ás de 11 para 1 (subtraindo 10 da pontuação total), repetindo o processo
        * até que a mão não esteja mais estourada ou não existam mais ases para ajustar.
        */
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
