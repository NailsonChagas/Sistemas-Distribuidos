import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
            System.out.println("[Servidor] Preparado para receber requisicoes:");
            System.out.println("  - Conversao Celsius -> Fahrenheit");
            System.out.println("  - Conversao m/h -> km/h");
            System.out.println("  - Calculo de fatorial\n");

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

                String response_message;

                // Verifica se o cliente solicitou encerramento
                if (received_message.equalsIgnoreCase("sair")) {
                    System.out.println("[Servidor] Cliente solicitou encerramento");
                    break;
                }

                // Processa o comando recebido
                response_message = processRequest(received_message);

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

    private String processRequest(String request) {
        String[] parts = request.split(":", 2);

        if (parts.length != 2) {
            return "Erro: Formato invalido. Use comando:valor";
        }

        String command = parts[0].toLowerCase();
        String valueStr = parts[1];

        try {
            switch (command) {
                case "celsius":
                    // a) Converte Celsius para Fahrenheit
                    double celsius = Double.parseDouble(valueStr);
                    double fahrenheit = (celsius * 9 / 5) + 32;
                    return String.format("%.2f°C = %.2f°F", celsius, fahrenheit);

                case "velocidade":
                    // b) Converte m/h para km/h (1 milha = 1.60934 km)
                    double mph = Double.parseDouble(valueStr);
                    double kmh = mph * 1.60934;
                    return String.format("%.2f m/h = %.2f km/h", mph, kmh);

                case "fatorial":
                    // c) Calcula fatorial de um numero inteiro
                    int n = Integer.parseInt(valueStr);
                    if (n < 0) {
                        return "Erro: Nao existe fatorial de numero negativo";
                    }
                    long fatorial = calcularFatorial(n);
                    return String.format("Fatorial de %d! = %d", n, fatorial);

                default:
                    return "Erro: Comando desconhecido. Use: celsius, velocidade, fatorial";
            }
        } catch (NumberFormatException e) {
            return "Erro: Valor numerico invalido. Certifique-se de enviar um numero valido.";
        }
    }

    private long calcularFatorial(int n) {
        long resultado = 1;
        for (int i = 2; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }
}