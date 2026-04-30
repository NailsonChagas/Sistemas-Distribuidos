import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient implements Runnable {

    private final String host; // Ip do servidor
    private final int port; // Porta do servidor
    private final byte[] buffer = new byte[1024];
    private DatagramSocket socket;
    private InetAddress server_address;

    public UDPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("[Cliente] Iniciando ...");

            // Passar o endereco ip do servidor
            server_address = InetAddress.getByName(host);

            // Passo 1: criar socket
            socket = new DatagramSocket();

            while (true) {
                // Passo 2: ler entrada do usuario
                System.out.println("[Cliente] Operacoes disponiveis:");
                System.out.println("  - 'soma:<num1>:<num2>' : Soma dois numeros");
                System.out.println("  - 'subtracao:<num1>:<num2>' : Subtrai dois numeros");
                System.out.println("  - 'multiplicacao:<num1>:<num2>' : Multiplica dois numeros");
                System.out.println("  - 'divisao:<num1>:<num2>' : Divide dois numeros");
                System.out.println("  - 'sair' : Encerra o cliente\n");
                System.out.print("Digite o comando (soma/subtracao/multiplicacao/divisao/sair): ");
                String message = scanner.nextLine();

                // verifica condicao de saida
                if (message.equalsIgnoreCase("sair")) {
                    byte[] send_data = message.getBytes();
                    DatagramPacket send_packet = new DatagramPacket(
                            send_data,
                            send_data.length,
                            server_address,
                            port
                    );
                    socket.send(send_packet);
                    System.out.println("[Cliente] Encerrando...");
                    break;
                }

                // valida formato do comando
                String[] parts = message.split(":");
                if (parts.length != 3) {
                    System.out.println("[Cliente] Formato invalido! Use: operacao:num1:num2 (ex: soma:10:5)");
                    continue;
                }

                byte[] send_data = message.getBytes();

                // cria datagrama de envio
                DatagramPacket send_packet = new DatagramPacket(
                        send_data,
                        send_data.length,
                        server_address,
                        port
                );

                socket.send(send_packet); // envia requisicao

                // prepara para receber resposta
                DatagramPacket recv_packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(recv_packet); // bloqueia ate receber resposta

                // processa resposta
                String received_message = new String(
                        recv_packet.getData(), 0, recv_packet.getLength()
                );

                System.out.println("[Cliente] Resposta do servidor: " + received_message);
                System.out.println("[Cliente] Enviado por: " + recv_packet.getAddress() + ":" + recv_packet.getPort() + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed())
                // Passo 3: fecha socket
                socket.close();

            scanner.close();
            System.out.println("[Cliente] Socket fechado");
        }
    }
}