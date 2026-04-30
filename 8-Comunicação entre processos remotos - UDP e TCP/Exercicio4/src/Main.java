// Desenvolva um programa distribuído utilizando Sockets UDP capaz de realizar as quatro operações
// básicas de uma calculadora. O Servidor deverá centralizar todas as operações de processamento, ou
// seja, o Cliente envia apenas os valores e a operação desejada. O resultado da operação deve ser
// apresentado na tela do Cliente.

public class Main {
    static void main(String[] args) throws InterruptedException {
        int port = 1234;
        String host = "127.0.0.1";

        Thread server_thread = new Thread(new UDPServer(port));
        server_thread.start();

        // Aguarda o servidor iniciar
        Thread.sleep(1000);

        Thread client_thread = new Thread(new UDPClient(host, port));
        client_thread.start();

        // Aguarda o cliente terminar (por exemplo, ao digitar "sair")
        client_thread.join();

        System.out.println("[Main] Aplicação finalizada.");
    }
}