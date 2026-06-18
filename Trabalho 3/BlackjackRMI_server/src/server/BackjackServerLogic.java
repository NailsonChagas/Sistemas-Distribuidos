package server;

import interfaces.BlackjackRemoteInterface;
import models.DeckCards;
import models.GameState;
import models.Hand;
import models.Card;
import models.Score;

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
    private final Map<String, Score> scores = new ConcurrentHashMap<>();
    private final Map<String, Long> lastSeen = new ConcurrentHashMap<>(); // para o heartbeat checando se desconectou

    public BackjackServerLogic() throws RemoteException {
        super();

        /**
         * Checa se o cliente ainda esta conectado
         * caso n -> remover
         */
        Thread cleaner = new Thread(() -> {
            while (true) {
                long now = System.currentTimeMillis();

                for (String name : lastSeen.keySet()) {

                    if (now - lastSeen.get(name) > 4000) { // 4 s sem contato
                        System.out.println("Removendo jogador " + name);

                        players.remove(name);
                        scores.remove(name);
                        lastSeen.remove(name);
                    }
                }

                try {
                    System.out.println("Teste");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    @Override
    public String startRound(String name) throws RemoteException {
        GameState game = new GameState();
        
        players.put(name, game);
        scores.putIfAbsent(name, new Score()); // Apenas criar novo score se o nome não existir

        return game.getPlayerHand().getCards().toString();
    }

    @Override
    public String hit(String name) throws RemoteException {
        GameState game = players.get(name);
        Score score = scores.get(name);

        if (game.isFinished()) return ""; // ver o que fazer nesse caso

        Card aux = game.getDeck().buyCard();

        game.getPlayerHand().addCard(aux);

        int points = game.getPlayerHand().calculateScore();

        if (points > 21) {
            score.addLoss();
            game.setFinished(true);
            return aux.toString() + ": Você estourou!";
        }

        return aux.toString() + ": Pontuação = " + points;
    }

    @Override
    public String stand(String name) throws RemoteException {
        GameState game = players.get(name);
        Score score = scores.get(name);

        Hand dealerHand = game.getDealerHand();
        Hand playerHand = game.getPlayerHand();
        DeckCards deck = game.getDeck();

        while (dealerHand.calculateScore() < 17) {
            dealerHand.addCard(deck.buyCard());
        }

        game.setFinished(true);

        int dealerScore = dealerHand.calculateScore();
        int playerScore = playerHand.calculateScore();

        if (dealerScore > 21 || playerScore > dealerScore) {
            score.addWin();
            return "Jogador venceu";
        }

        score.addLoss();
        return "Dealer venceu";
    }

    @Override
    public String score(String name) throws RemoteException {
        return scores.get(name).toString();
    }

    @Override
    public void heartbeat(String name) throws RemoteException {
        lastSeen.put(name, System.currentTimeMillis());
    }
}