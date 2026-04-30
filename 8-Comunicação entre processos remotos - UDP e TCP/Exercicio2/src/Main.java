// Faça um programa distribuído utilizando Sockets UDP onde o Cliente envia uma requisição ao
// Servidor perguntando a data e a hora, e o mesmo responde com os valores corretos.

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