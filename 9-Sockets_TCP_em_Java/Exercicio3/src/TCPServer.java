import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    private final int accept_timeout_ms = 1000;
    private final ServerSocket sever_socket;
    private final int timeout_ms;
    private final boolean debug;
    private int client_num;

    public TCPServer(int port, int backlog, int timeout_ms, boolean debug) throws IOException {
        this.sever_socket = new ServerSocket(port, backlog);
        sever_socket.setSoTimeout(accept_timeout_ms); //vai ficar travado em accept por accept_timeout_ms só
        this.timeout_ms = timeout_ms;
        this.client_num = 0;
        this.debug = debug;

        printDebug(
                "[TCP SERVER] Servidor iniciado em "
                        + sever_socket.getInetAddress().getHostAddress()
                        + ":"
                        + sever_socket.getLocalPort()
        );
    }

    public void printDebug(String message) { // n tem synchronized por ser thread safe
        if (debug) {
            System.out.println(message);
        }
    }

    public synchronized void incrementClientNum() {
        client_num++;
    }

    public synchronized void decrementClientNum() {
        client_num--;
    }

    public synchronized int getClientNum() {
        return client_num;
    }

    @Override
    public void run() {
        long start_time = -1;
        boolean running = true;

        while (running) {
            printDebug("[TCP SERVER] Clientes: " + getClientNum());

            if (getClientNum() == 0) {
                printDebug("[TCP SERVER] Sem clientes.");

                if (start_time == -1) {
                    start_time = System.currentTimeMillis();
                }

                long elapsed_time = System.currentTimeMillis() - start_time;

                if (elapsed_time >= timeout_ms) {
                    printDebug(
                            "[TCP SERVER] Sem clientes por "
                                    + timeout_ms / 1000
                                    + "s. Encerrando servidor."
                    );

                    try {
                        sever_socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    running = false;
                    continue;
                }

            } else {
                // reset timer quando houver clientes
                start_time = -1;
            }

            try {
                printDebug("[TCP SERVER] Aguardando tentativa de conexão...");
                Socket client_socket = sever_socket.accept();
                Thread new_client =
                        new Thread(
                                new TCPServerWorker(client_socket, this)
                        );
                new_client.start();
            } catch (java.net.SocketTimeoutException e) {
                printDebug(
                        "[TCP SERVER] Sem nova conexão no ultimo "
                                + accept_timeout_ms / 1000
                                + "s, desconectando em "
                                + timeout_ms / 1000
                                + "s."
                );
            } catch (IOException e) {
                printDebug("[TCP SERVER] Erro ao aceitar conexão.");
                printDebug("[TCP SERVER] Mensagem: " + e.getMessage());
            }
        }
    }
}
