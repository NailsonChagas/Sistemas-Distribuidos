/**
 * Implementar um programa cliente/servidor no qual o servidor deve realizar
 * a simulação do lançamento de uma moeda e ser capaz de atender múltiplas
 * requisições simultaneamente.
 * <p>
 * A aplicação também deve permitir consultar a quantidade de vezes que cada
 * face apareceu desde a criação do objeto ou desde a última reinicialização
 * dos contadores.
 * <p>
 * O servidor deve implementar a interface abaixo:
 * <p>
 * public interface IMoeda {
 * public int arremessar(); // 0: Cara e 1: Coroa
 * public int getContadorCara();
 * public int getContadorCoroa();
 * public void zerarContadores();
 * }
 * <p>
 * Formato da requisição:
 * - String contendo o nome do método a ser invocado no servidor.
 * <p>
 * Formato da resposta:
 * - String contendo "cara" ou "coroa" para o método arremessar().
 * - String contendo o valor retornado para os métodos
 * getContadorCara() e getContadorCoroa().
 */

void main() {
    try {
        int server_port = 1234;
        int backlog = 5;
        int timeout_ms = 5000;

        TCPServer server = new TCPServer(
                server_port,
                backlog,
                timeout_ms,
                false
        );

        TCPClient client = new TCPClient(server_port);

        Thread server_thread = new Thread(server);
        Thread client_thread = new Thread(client);

        server_thread.start();
        Thread.sleep(500); // pequeno delay para garantir que o servidor iniciou

        client_thread.start();
        client_thread.join(); // esperar a client_thread termianar
    } catch (Exception e) {
        System.out.println("[MAIN] Erro: " + e.getMessage());
    }

    System.out.println("[Main] Aplicação finalizada.");
}