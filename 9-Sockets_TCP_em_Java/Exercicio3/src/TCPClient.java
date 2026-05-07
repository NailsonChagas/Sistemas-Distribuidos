import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient implements Runnable {

    private final int server_port;
    private final Scanner scanner;
    private Socket client_socket;
    private ObjectOutputStream out_stream;
    private ObjectInputStream in_stream;

    public TCPClient(int server_port) {
        this.server_port = server_port;
        this.scanner = new Scanner(System.in);
    }

    private void connect() throws Exception {
        InetAddress address = InetAddress.getByName("localhost");

        // Passo 1: conecta ao servidor
        client_socket = new Socket(address, server_port);

        // IMPORTANTE:
        // ObjectOutputStream primeiro
        out_stream = new ObjectOutputStream(client_socket.getOutputStream());
        in_stream = new ObjectInputStream(client_socket.getInputStream());

        String welcome = in_stream.readUTF();

        System.out.println("[SERVER RESPONSE] " + welcome);
    }

    private void close() {

        try {

            if (in_stream != null) {
                in_stream.close();
            }

            if (out_stream != null) {
                out_stream.close();
            }

            if (client_socket != null && !client_socket.isClosed()) {
                client_socket.close();
            }

            System.out.println("[CLIENT] Conexão encerrada");

        } catch (Exception e) {
            System.out.println("[CLIENT] Erro ao fechar conexão");
        }
    }

    private void printMenu() {

        System.out.println();
        System.out.println("===== MENU =====");
        System.out.println("1 - Arremessar moeda");
        System.out.println("2 - Get contador cara");
        System.out.println("3 - Get contador coroa");
        System.out.println("4 - Zerar contadores");
        System.out.println("5 - Get numero de clientes");
        System.out.println("6 - Echo");
        System.out.println("7 - Fechar conexão via servidor");
        System.out.println("8 - Fechar conexão via client");
        System.out.println("9 - Get contador total");
        System.out.print("Escolha: ");
    }

    @Override
    public void run() {
        try {
            connect();
            boolean running = true;

            while (running) {
                printMenu();
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "arremessar",
                                        null
                                )
                        );
                        out_stream.flush();
                        System.out.println("[CLIENT] Moeda arremessada");
                        int result = in_stream.readInt();
                        if (result == 0) {
                            System.out.println("[SERVER RESPONSE] Moeda: Cara");
                        } else {
                            System.out.println("[SERVER RESPONSE] Moeda: Coroa");
                        }
                        break;
                    }
                    case 2: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "getContadorCara",
                                        null
                                )
                        );
                        out_stream.flush();
                        int caras = in_stream.readInt();
                        System.out.println("[SERVER RESPONSE] Contador cara: " + caras);
                        break;
                    }
                    case 3: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "getContadorCoroa",
                                        null
                                )
                        );
                        out_stream.flush();
                        int coroas = in_stream.readInt();
                        System.out.println("[SERVER RESPONSE] Contador coroa: " + coroas);
                        break;
                    }
                    case 4: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "zerarContadores",
                                        null
                                )
                        );
                        out_stream.flush();
                        System.out.println("[CLIENT] Contadores zerados");
                        break;
                    }
                    case 5: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "getClientNum",
                                        null
                                )
                        );
                        out_stream.flush();
                        int clients = in_stream.readInt();
                        System.out.println("[SERVER RESPONSE] Clientes conectados: " + clients);
                        break;
                    }
                    case 6: {
                        System.out.print("Digite mensagem: ");
                        String msg = scanner.nextLine();
                        out_stream.writeObject(
                                new CustomRequest(
                                        "POST",
                                        "echo",
                                        msg
                                )
                        );
                        out_stream.flush();
                        Object response = in_stream.readObject();
                        System.out.println("[SERVER RESPONSE] Echo: " + response);
                        break;
                    }
                    case 7: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "closeConnection",
                                        null
                                )
                        );
                        out_stream.flush();
                        String response_close = in_stream.readUTF();
                        System.out.println("[SERVER RESPONSE] " + response_close);
                        running = false;
                        break;
                    }
                    case 8: {
                        System.out.println("[CLIENT] Fechando conexão localmente");
                        running = false;
                        break;
                    }
                    case 9: {
                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "getContadorCoroa",
                                        null
                                )
                        );
                        out_stream.flush();
                        int coroas = in_stream.readInt();

                        out_stream.writeObject(
                                new CustomRequest(
                                        "GET",
                                        "getContadorCara",
                                        null
                                )
                        );
                        out_stream.flush();
                        int caras = in_stream.readInt();

                        System.out.println("[SERVER RESPONSE] Contador cara: " + caras + " / Contador coroa: " + coroas);
                        break;
                    }
                    default: {
                        System.out.println("[CLIENT] Opção inválida");
                        break;
                    }
                }
            }

            close();

        } catch (Exception e) {
            System.out.println("[CLIENT] Erro: " + e.getMessage());
            close();
        }
    }
}