package server;

import interfaces.BlackjackRemoteInterface;
import models.DeckCards;
import models.GameState;
import models.Hand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackjackServerLogic extends UnicastRemoteObject implements BlackjackRemoteInterface {
    private final Map<String, GameState> players = new ConcurrentHashMap<>();

    public BackjackServerLogic() throws RemoteException {
        super();
    }

    @Override
    public String startRound(String name) throws RemoteException {
        GameState game = new GameState();
        players.put(name, game);
        return game.getPlayerHand().getCards().toString();
    }

    @Override
    public String hit(String name) throws RemoteException {
        GameState game = players.get(name);

        if (game.isFinished()) return ""; // ver o que fazer nesse caso

        game.getPlayerHand().addCard(
                game.getDeck().buyCard()
        );

        int points = game.getPlayerHand().calculateScore();

        if (points > 21) {
            game.setFinished(true);
            return "Você estourou!";
        }

        return "Pontuação = " + points;
    }

    @Override
    public String stand(String name) throws RemoteException {
        GameState game = players.get(name);

        Hand dealerHand = game.getDealerHand();
        Hand playerHand = game.getPlayerHand();
        DeckCards deck = game.getDeck();

        while (dealerHand.calculateScore() < 17) {
            dealerHand.addCard(deck.buyCard());
        }

        game.setFinished(true);

        int dealerScore = dealerHand.calculateScore();
        int playerScore = playerHand.calculateScore();

        if (dealerScore > 21)
            return "Jogador venceu";

        if (playerScore > dealerScore)
            return "Jogador venceu";

        return "Dealer venceu";
    }
}