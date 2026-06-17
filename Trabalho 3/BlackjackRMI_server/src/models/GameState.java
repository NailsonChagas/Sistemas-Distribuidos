package models;

public class GameState {
    private DeckCards deck;
    private Hand playerHand;
    private Hand dealerHand;
    private boolean finished;

    public GameState() {
        deck = new DeckCards();
        playerHand = new Hand();
        dealerHand = new Hand();

        // duas cartas iniciais
        playerHand.addCard(deck.buyCard());
        playerHand.addCard(deck.buyCard());
        dealerHand.addCard(deck.buyCard());
        dealerHand.addCard(deck.buyCard());

        finished = false;
    }

    public DeckCards getDeck() {
        return deck;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
