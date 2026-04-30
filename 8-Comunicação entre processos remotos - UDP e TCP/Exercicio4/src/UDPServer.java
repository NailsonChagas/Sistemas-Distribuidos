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
            System.out.println("  - Soma de dois numeros");
            System.out.println("  - Subtracao de dois numeros");
            System.out.println("  - Multiplicacao de dois numeros");
            System.out.println("  - Divisao de dois numeros\n");

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
        String[] parts = request.split(":", 3);

        if (parts.length != 3) {
            return "Erro: Formato invalido. Use operacao:num1:num2";
        }

        String operation = parts[0].toLowerCase();
        String value1Str = parts[1];
        String value2Str = parts[2];

        try {
            double num1 = Double.parseDouble(value1Str);
            double num2 = Double.parseDouble(value2Str);

            switch (operation) {
                case "soma":
                    double soma = num1 + num2;
                    return String.format("%.2f + %.2f = %.2f", num1, num2, soma);

                case "subtracao":
                    double subtracao = num1 - num2;
                    return String.format("%.2f - %.2f = %.2f", num1, num2, subtracao);

                case "multiplicacao":
                    double multiplicacao = num1 * num2;
                    return String.format("%.2f * %.2f = %.2f", num1, num2, multiplicacao);

                case "divisao":
                    if (num2 == 0) {
                        return "Erro: Divisao por zero nao permitida";
                    }
                    double divisao = num1 / num2;
                    return String.format("%.2f / %.2f = %.2f", num1, num2, divisao);

                default:
                    return "Erro: Operacao desconhecida. Use: soma, subtracao, multiplicacao, divisao";
            }
        } catch (NumberFormatException e) {
            return "Erro: Valor numerico invalido. Certifique-se de enviar numeros validos.";
        }
    }
}