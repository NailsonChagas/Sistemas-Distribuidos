package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BlackjackRemoteInterface extends Remote {
    // Inicia uma nova partida para o jogador
    String startRound(String name) throws RemoteException;

    // Pega uma nova carta para jogador
    String hit(String name) throws RemoteException;

    // Encerra o turno do jogador e passa para o dealer
    String stand(String name) throws RemoteException;

    // Pega o placar
    String score(String name) throws RemoteException;

    // Heartbeat
    void heartbeat(String name) throws RemoteException;

    // Jogadores conectados
    String players() throws RemoteException;

    // Sair
    void quit(String name) throws RemoteException;
}
