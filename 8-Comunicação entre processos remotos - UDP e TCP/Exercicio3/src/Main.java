//Faça um programa distribuído utilizando Sockets UDP que permita a comunicação
//Cliente/Servidor. O cliente deverá enviar uma requisição a ser atendida pelo servidor nas seguintes
//situações:
//a) Cliente envia temperatura em graus Celsius e o Servidor retorna em Fahrenheit;
//b) Cliente envia velocidade em m/h e o Servidor retorna em km/h;
//c) Cliente envia um valor inteiro e o Servidor retorna fatorial.
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