import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer implements Runnable {
    private final int port;
    private DatagramSocket socket;
    private final byte[] buffer = new byte[1024];

    public UDPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {

            // Passo 1: criar socket em porta especifica.
            socket = new DatagramSocket(port);

            System.out.println("[Servidor] Iniciando e escutando na porta " + port);

            System.out.println("[Servidor] Preparando para receber mensagem");

            while (true) {
                // Passo 2: realizar a comunicacao com o cliente

                // cria um datagrama para receber requisicao do cliente
                DatagramPacket recv_packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(recv_packet); // bloqueia até receber um pacote

                // Processa a mensagem recebida pelo cliente
                String received_message = new String(
                        recv_packet.getData(), 0, recv_packet.getLength()
                );

                System.out.println("[Servidor] recebi " + received_message + " de " +
                        recv_packet.getAddress() + ":" + recv_packet.getPort());

                // Mensagem de resposta
                // Inverte a mensagem
                String reversed_message = new StringBuilder(received_message)
                        .reverse()
                        .toString();

                byte[] response_data = reversed_message.getBytes();

                // cria um datagrama com a resposta para o cliente
                DatagramPacket send_packet = new DatagramPacket(
                        response_data,
                        response_data.length,
                        recv_packet.getAddress(),
                        recv_packet.getPort()
                );

                socket.send(send_packet); // envia resposta

                if (received_message.equalsIgnoreCase("sair")) {
                    System.out.println("[Servidor] Encerrando...");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed())
                // Passo 3: fecha o socket (somente quando terminar o servidor)
                socket.close();

            System.out.println("[Servidor] Socket fechado");
        }
    }
}