import server.BackjackServerLogic;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) {
        try {
            // cria o registry na porta padrão 1099
            LocateRegistry.createRegistry(1099);

            // cria o objeto remoto
            BackjackServerLogic server = new BackjackServerLogic();

            // registra o objeto com o nome "Blackjack"
            Naming.rebind("Blackjack", server);

            System.out.println("Servidor iniciado!");

        } catch (Exception e) {
            System.out.println("Erro ao iniciar servidor");
            e.printStackTrace();
        }
    }
}