import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient implements Runnable {

    private final String host; // Ip do servidor
    private final int port; // Porta do servidor
    private DatagramSocket socket;
    private InetAddress server_address;
    private final byte[] buffer = new byte[1024];

    public UDPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);

        try {

            System.out.println("[Cliente] Iniciando ...");

            // Passar o endereço ip do servidor
            server_address = InetAddress.getByName(host);

            // Passo 1: criar socket
            socket = new DatagramSocket();

            System.out.println("[Cliente] Digite mensagens (digite 'sair' para encerrar)");

            while (true) {

                // Passo 2: ler entrada do usuário
                System.out.print("Mensagem: ");
                String message = scanner.nextLine();

                byte[] send_data = message.getBytes();

                // cria datagrama de envio
                DatagramPacket send_packet = new DatagramPacket(
                        send_data,
                        send_data.length,
                        server_address,
                        port
                );

                socket.send(send_packet); // envia mensagem

                // verifica condição de saída
                if (message.equalsIgnoreCase("sair")) {
                    System.out.println("[Cliente] Encerrando...");
                    break;
                }

                // prepara para receber resposta
                DatagramPacket recv_packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(recv_packet); // bloqueia até receber resposta

                // processa resposta
                String received_message = new String(
                        recv_packet.getData(), 0, recv_packet.getLength()
                );

                System.out.println("[Cliente] recebi " + received_message + " de " +
                        recv_packet.getAddress() + ":" + recv_packet.getPort());
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