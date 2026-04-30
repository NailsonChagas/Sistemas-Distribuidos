// Faça um programa distribuído utilizando sockets UDP,
// em que o cliente envia uma mensagem ao servidor,
// e o servidor responde com a mensagem invertida.

public class Main {
    public static void main(String[] args) throws InterruptedException {
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