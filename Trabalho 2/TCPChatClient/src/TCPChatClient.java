import java.io.*;
import java.net.Socket;
import java.util.*;

public class TCPChatClient implements Runnable {
    private final String host;
    private final int port;

    private Socket socket;
    private ObjectOutputStream out_stream;
    private ObjectInputStream in_stream;

    private String username;

    private volatile String current_target = null;
    private final Map<String, List<Mensagem>> history = new HashMap<>();

    private volatile boolean running = true;

    public TCPChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);

            out_stream = new ObjectOutputStream(socket.getOutputStream());
            in_stream = new ObjectInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);

            System.out.print("Username: ");
            username = scanner.nextLine();

            out_stream.writeUTF(username);
            out_stream.flush();

            new Thread(this::receiveLoop).start();

            System.out.println("Conectado.");
            System.out.println("Digite /local help para comandos locais");
            System.out.println("Digite /help para comandos do servidor");

            while (running) {

                printPrompt();

                String input = scanner.nextLine();

                if (input.startsWith("/local ")) {
                    handleLocalCommand(input.substring(7));
                }
                else if (input.startsWith("/")) {
                    sendSystemCommand(input);
                }
                else if (current_target == null) {
                    System.out.println("Selecione um destino com /local use usuario ou @grupo");
                }
                else {
                    Mensagem msg = new Mensagem(username, current_target, input);
                    out_stream.writeObject(msg);
                    out_stream.flush();

                    addToHistory(current_target, msg);
                }
            }

        } catch (Exception e) {
            System.out.println("[CLIENT] Erro: " + e.getMessage());
        }
    }

    // ===================== RECEBIMENTO =====================

    private void receiveLoop() {
        try {
            while (running) {
                Mensagem msg = (Mensagem) in_stream.readObject();
                if (msg != null){
                    // CORREÇÃO: Adiciona ao histórico usando a chave correta
                    String historyKey = getString(msg);

                    addToHistory(historyKey, msg);

                    if (shouldDisplay(msg)) {
                        printMessage(msg);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\n[CLIENT] conexão encerrada");
            running = false;
        }
    }

    private static String getString(Mensagem msg) {
        String historyKey;
        String dest = msg.getDestinatario();
        String sender = msg.getRemetente();

        if (dest != null && dest.startsWith("@")) {
            // É mensagem de grupo - usar o grupo como chave
            historyKey = dest;
        } else if (dest != null && !dest.startsWith("@")) {
            // É mensagem privada - usar o remetente como chave
            historyKey = sender;
        } else if (dest == null && !sender.equals("SERVER")) {
            // É broadcast - usar "broadcast" como chave
            historyKey = "broadcast";
        } else {
            // Mensagem do servidor
            historyKey = "SERVER";
        }
        return historyKey;
    }

    private boolean shouldDisplay(Mensagem msg) {
        String sender = msg.getRemetente();
        String dest = msg.getDestinatario();

        // Sempre mostra mensagens do servidor
        if (sender.equals("SERVER")) return true;

        // Mensagens broadcast (para todos)
        if (dest == null) return true;

        // Mensagens privadas enviadas diretamente para você
        if (dest.equals(username)) return true;

        // Mensagens relacionadas ao destino atual
        if (current_target != null) {
            // Se você está em um grupo, mostra mensagens enviadas para esse grupo
            if (dest.equals(current_target)) return true;

            // Se você está conversando com um usuário específico, mostra mensagens dele
            return sender.equals(current_target);
        }

        return false;
    }

    // ===================== PROMPT / OUTPUT =====================

    private synchronized void printMessage(Mensagem msg) {
        System.out.print("\r\033[K"); // limpa linha atual
        System.out.println(formatMessage(msg));
        printPrompt();
    }

    private synchronized void printPrompt() {
        if (current_target == null) {
            System.out.print("[nenhum destino] > ");
        } else {
            System.out.print("[" + current_target + "] > ");
        }
        System.out.flush();
    }

    private String formatMessage(Mensagem msg) {
        return "[" + msg.getHorario() + "] "
                + msg.getRemetente()
                + " -> "
                + (msg.getDestinatario() == null ? "Todos" : msg.getDestinatario())
                + ": "
                + msg.getConteudo();
    }

    // ===================== COMANDOS LOCAIS =====================

    private void handleLocalCommand(String input) {
        String[] args = input.split(" ");

        if (args.length == 0) {
            System.out.println("Comando local inválido");
            return;
        }

        String command = args[0];

        switch (command) {
            case "help" -> System.out.println("""
                === COMANDOS LOCAIS ===
                /local use <usuario|@grupo>  - Seleciona destino para mensagens
                /local history                - Mostra histórico do destino atual
                /local history <alvo>         - Mostra histórico com alvo específico
                /local clear                  - Limpa todo o histórico
                /local quit                   - Sai do cliente
               \s
                Exemplos:
                /local use @admins            - Enviar mensagens para o grupo @admins
                /local use Nailson            - Enviar mensagens privadas para Nailson
                /local history                - Ver histórico com destino atual
                /local history @devs          - Ver histórico com grupo @devs
           \s""");

            case "use" -> {
                if (args.length != 2) {
                    System.out.println("Uso: /local use <usuario|@grupo>");
                    System.out.println("Exemplo: /local use @admins");
                    return;
                }

                current_target = args[1];
                System.out.println("Destino selecionado: " + current_target);
                showHistory(current_target);
            }

            case "history" -> {
                if (args.length == 2) {
                    showHistory(args[1]);
                } else if (current_target != null) {
                    showHistory(current_target);
                } else {
                    System.out.println("Nenhum destino selecionado. Use /local history <alvo> ou selecione um destino com /local use");
                }
            }

            case "clear" -> {
                history.clear();
                System.out.println("Histórico completamente limpo");
            }

            case "quit" -> {
                System.out.println("Encerrando cliente...");
                running = false;
                close();
            }

            default -> System.out.println("Comando local inválido. Use /local help para ajuda");
        }
    }

    // ===================== ENVIO SERVIDOR =====================

    private void sendSystemCommand(String input) {
        try {
            Mensagem msg = new Mensagem(username, null, input);
            out_stream.writeObject(msg);
            out_stream.flush();
        } catch (IOException e) {
            System.out.println("[CLIENT] erro ao enviar comando");
        }
    }

    // ===================== HISTÓRICO =====================

    private void addToHistory(String key, Mensagem msg) {
        synchronized (history) {
            history.putIfAbsent(key, new ArrayList<>());
            history.get(key).add(msg);

            // Limita o histórico a 100 mensagens por destinatário para evitar memory leak
            if (history.get(key).size() > 100) {
                history.get(key).removeFirst();
            }
        }
    }

    private void showHistory(String key) {
        List<Mensagem> msgs;
        synchronized (history) {
            msgs = history.get(key);
        }

        if (msgs == null || msgs.isEmpty()) {
            System.out.println("Sem histórico de mensagens com " + key);
            return;
        }

        System.out.println("\n=== HISTÓRICO: " + key + " ===");
        System.out.println("Total de " + msgs.size() + " mensagens:");
        System.out.println("-------------------");

        for (Mensagem m : msgs) {
            System.out.println(formatMessage(m));
        }

        System.out.println("===================\n");
    }

    // ===================== FINALIZAÇÃO =====================

    private void close() {
        try {
            if (out_stream != null) out_stream.close();
            if (in_stream != null) in_stream.close();
            if (socket != null) socket.close();
            System.out.println("Desconectado do servidor");
        } catch (IOException ignored) {}
    }
}