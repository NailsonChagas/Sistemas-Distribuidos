import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class TCPServerWorker implements Runnable {
    private final IMoeda coin; // especifico do exercicio
    Socket worker_socket;
    TCPServer main_server;
    ObjectInputStream in_stream;
    ObjectOutputStream out_stream;

    public TCPServerWorker(Socket client, TCPServer server) {
        this.worker_socket = client;
        this.main_server = server;
        this.coin = new Moeda();

        try {
            // IMPORTANTE segundo o ChatGPT: ObjectOutputStream PRIMEIRO
            this.out_stream = new ObjectOutputStream(worker_socket.getOutputStream());
            this.in_stream = new ObjectInputStream(worker_socket.getInputStream());

            main_server.incrementClientNum();
            main_server.printDebug("[TCP SERVER WK] Cliente conectado: " + getClientInfo());
            out_stream.writeUTF("Bem vindo ao servidor " + getClientInfo());
            out_stream.flush();
        } catch (IOException e) {
            closeClient();
            throw new RuntimeException(e);
        }
    }

    private String getClientInfo() {
        return worker_socket.getInetAddress().getHostAddress() + ":" + worker_socket.getPort();
    }

    private void closeClient() {
        try {
            if (in_stream != null) in_stream.close();
            if (out_stream != null) out_stream.close();
            if (worker_socket != null && !worker_socket.isClosed()) {
                worker_socket.close();
                main_server.printDebug("[TCP SERVER WK] Cliente " + getClientInfo() + " desconectado");
            }
            main_server.decrementClientNum();
        } catch (IOException e) {
            main_server.printDebug("[TCP SERVER WK] Erro ao fechar conexão: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            boolean running = true;
            while (running) {
                CustomRequest request = (CustomRequest) in_stream.readObject();

                String type = request.type();
                String func = request.func();

                main_server.printDebug(
                        "[TCP SERVER WK] Requisição recebida de "
                                + getClientInfo()
                                + " -> "
                                + type
                                + " "
                                + func
                );

                switch (type.toUpperCase()) {
                    case "GET":
                        switch (func) {
                            case "getClientNum":
                                out_stream.writeInt(main_server.getClientNum());
                                out_stream.flush();
                                break;
                            case "closeConnection":
                                out_stream.writeUTF("Conexão encerrada pelo cliente");
                                out_stream.flush();
                                closeClient();
                                running = false;
                                continue;
                            case "arremessar": // especificos do exercicio
                                out_stream.writeInt(coin.arremessar());
                                out_stream.flush();
                                break;
                            case "getContadorCara":
                                out_stream.writeInt(coin.getContadorCara());
                                out_stream.flush();
                                break;
                            case "getContadorCoroa":
                                out_stream.writeInt(coin.getContadorCoroa());
                                out_stream.flush();
                                break;
                            case "zerarContadores":
                                coin.zerarContadores();
                                break;
                            default:
                                out_stream.writeUTF("Erro: função GET inválida");
                                out_stream.flush();
                                break;
                        }
                        break;
                    case "POST":
                        Object data = request.data();
                        switch (func) {
                            case "echo":
                                main_server.printDebug("[TCP SERVER WK] Dados recebidos: " + data);
                                out_stream.writeObject(data);
                                out_stream.flush();
                                break;
                            default:
                                out_stream.writeUTF("Erro: função POST inválida");
                                out_stream.flush();
                                break;
                        }
                        break;
                    default:
                        out_stream.writeUTF("Erro: tipo de requisição inválida");
                        out_stream.flush();
                        break;
                }
            }
        }
        catch (java.io.EOFException e) {
            main_server.printDebug(
                    "[TCP SERVER WK] Cliente "
                            + getClientInfo()
                            + " fechou a conexão"
            );
            closeClient();
        }
        catch (Exception e) {
            main_server.printDebug("[TCP SERVER WK] Erro com o cliente" + getClientInfo() + ": " + e.getMessage());
            closeClient();
            throw new RuntimeException(e);
        }
    }
}