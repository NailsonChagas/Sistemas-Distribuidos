package server;

import interfaces.BlackjackRemoteInterface;
import models.DeckCards;
import models.GameState;
import models.Hand;
import models.Card;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackjackServerLogic extends UnicastRemoteObject implements BlackjackRemoteInterface {
    /**
     * https://docs.oracle.com/en/java/javase/17/docs/specs/rmi/arch.html
     * A method dispatched by the RMI runtime to a remote object implementation 
     * may or may not execute in a separate thread. The RMI runtime makes no 
     * guarantees with respect to mapping remote object invocations to threads. 
     * Since remote method invocation on the same remote object may execute 
     * concurrently, a remote object implementation needs to make sure its 
     * implementation is thread-safe.
     * 
     * Como o hashmap abaixo pode ser usado em diferentes threads ao mesmo 
     * tempo ele precisa ser ConcurrentHashMap, resto dos objetos como estão
     * dentro de GameState não precisam
     */
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

        Card aux = game.getDeck().buyCard();

        game.getPlayerHand().addCard(aux);

        int points = game.getPlayerHand().calculateScore();

        if (points > 21) {
            game.setFinished(true);
            return aux.toString() + ": Você estourou!";
        }

        return aux.toString() + ": Pontuação = " + points;
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