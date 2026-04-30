import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UDPServer implements Runnable {
    private final int port;
    private final byte[] buffer = new byte[1024];
    private DatagramSocket socket;

    public UDPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            // Passo 1: criar socket em porta especifica.
            socket = new DatagramSocket(port);

            System.out.println("[Servidor] Iniciando e escutando na porta " + port);
            System.out.println("[Servidor] Preparado para receber requisicoes de data/hora");

            while (true) {
                // Passo 2: realizar a comunicacao com o cliente
                // cria um datagrama para receber requisicao do cliente
                DatagramPacket recv_packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(recv_packet); // bloqueia ate receber um pacote

                // Processa a mensagem recebida pelo cliente
                String received_message = new String(
                        recv_packet.getData(), 0, recv_packet.getLength()
                );

                System.out.println("[Servidor] Recebi requisicao de " +
                        recv_packet.getAddress() + ":" + recv_packet.getPort());
                System.out.println("[Servidor] Mensagem: " + received_message);

                // Obtem data e hora atual
                LocalDateTime now = LocalDateTime.now();
                String response_message;

                // Verifica qual mensagem foi enviada usando switch case
                switch (received_message.toLowerCase()) {
                    case "data":
                        // Obtem a data atual
                        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        response_message = "Data: " + now.format(date_formatter);
                        break;

                    case "hora":
                        // Obtem a hora atual
                        DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        response_message = "Hora: " + now.format(time_formatter);
                        break;

                    case "data_hora":
                    case "datetime":
                        // Obtem data e hora atuais
                        DateTimeFormatter datetime_formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        response_message = "Data e Hora: " + now.format(datetime_formatter);
                        break;

                    case "sair":
                        System.out.println("[Servidor] Encerrando...");
                        response_message = "Servidor encerrando conexao";
                        byte[] close_data = response_message.getBytes();
                        DatagramPacket close_packet = new DatagramPacket(
                                close_data,
                                close_data.length,
                                recv_packet.getAddress(),
                                recv_packet.getPort()
                        );
                        socket.send(close_packet);
                        socket.close();
                        return;

                    default:
                        response_message = "Comando invalido. Use: 'data', 'hora', 'data_hora' ou 'sair'";
                        break;
                }

                byte[] response_data = response_message.getBytes();

                // cria um datagrama com a resposta para o cliente
                DatagramPacket send_packet = new DatagramPacket(
                        response_data,
                        response_data.length,
                        recv_packet.getAddress(),
                        recv_packet.getPort()
                );

                socket.send(send_packet); // envia resposta
                System.out.println("[Servidor] Enviada resposta para o cliente\n");
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