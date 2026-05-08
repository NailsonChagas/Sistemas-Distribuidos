import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TCPChatServer implements Runnable {
    private final ServerSocket server_socket;
    private final Map<String, ClientHandler> connected_users;
    private final Map<String, List<String>> groups;
    private final int empty_timeout_ms;
    private final boolean debug;
    private long empty_since;

    public TCPChatServer(int port, int backlog, int empty_timeout_ms, boolean debug) throws IOException {
        this.server_socket = new ServerSocket(port, backlog);
        this.server_socket.setSoTimeout(1000);
        this.connected_users = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
        this.empty_timeout_ms = empty_timeout_ms;
        this.empty_since = System.currentTimeMillis();
        this.debug = debug;

        createDefaultGroups();
    }

    @Override
    public void run() {
        printDebug("[SERVER] servidor iniciado");

        boolean running = true;

        while (running) {
            try {
                Socket socket = server_socket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();

            } catch (SocketTimeoutException e) {
                if (connected_users.isEmpty()) {
                    long elapsed = System.currentTimeMillis() - empty_since;

                    if (elapsed >= empty_timeout_ms) {
                        printDebug("[SERVER] Timeout sem clientes");
                        running = false;
                    }
                }

            } catch (IOException e) {
                printDebug("[SERVER] Erro: " + e.getMessage());
            }
        }

        shutdown();
    }

    public void printDebug(String txt) {
        if (debug) {
            System.out.println(txt);
        }
    }

    public boolean isAdmin(String user) {
        List<String> admins = groups.get("@admins");
        return admins != null && admins.contains(user);
    }

    public synchronized void addUser(String username, ClientHandler handler) {
        connected_users.put(username, handler);
        empty_since = 0;

        printDebug("[SERVER] Conectado: " + username);
    }

    public synchronized void removeUser(String username) {
        connected_users.remove(username);

        printDebug("[SERVER] Desconectado: " + username);

        if (connected_users.isEmpty()) {
            empty_since = System.currentTimeMillis();
        }
    }

    public void processMessage(Mensagem msg) {
        String dest = msg.getDestinatario();

        if (dest == null) {
            broadcast(msg);
        } else if (dest.startsWith("@")) {
            groupMessage(msg);
        } else {
            privateMessage(msg);
        }
    }

    private void broadcast(Mensagem msg) {
        for (ClientHandler c : connected_users.values()) {
            c.sendMessage(msg);
        }
    }

    private void privateMessage(Mensagem msg) {
        ClientHandler c = connected_users.get(msg.getDestinatario());
        if (c != null) c.sendMessage(msg);
    }

    private void groupMessage(Mensagem msg) {
        List<String> users = groups.get(msg.getDestinatario());

        if (users == null) {
            printDebug("[SERVER] Grupo inexistente");
            return;
        }

        for (String u : users) {
            ClientHandler c = connected_users.get(u);
            if (c != null) c.sendMessage(msg);
        }
    }

    public synchronized String getOnlineUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ONLINE ===\n");

        for (String u : connected_users.keySet()) {
            sb.append(u).append("\n");
        }

        return sb.toString();
    }

    public synchronized boolean createGroup(String group, String[] users) {
        if (!group.startsWith("@")) return false;
        if (groups.containsKey(group)) return false;

        groups.put(group, java.util.Arrays.asList(users));

        printDebug("[SERVER] Grupo criado: " + group);
        return true;
    }

    public synchronized boolean deleteGroup(String group) {
        if (!groups.containsKey(group)) return false;
        groups.remove(group);
        return true;
    }

    private void createDefaultGroups() {
        groups.put("@admins", new java.util.ArrayList<>(List.of("NailsonChagas", "MatheusXavier")));
    }

    public synchronized boolean addUserToGroup(String group, String user) {
        List<String> users = groups.get(group);
        if (users == null) return false;

        if (!users.contains(user)) {
            users.add(user);
        }
        return true;
    }

    public synchronized boolean removeUserFromGroup(String group, String user) {
        List<String> users = groups.get(group);
        if (users == null) return false;

        return users.remove(user);
    }

    public synchronized String getGroups() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GRUPOS ===\n");

        for (String g : groups.keySet()) {
            sb.append(g)
                    .append(" -> ")
                    .append(groups.get(g))
                    .append("\n");
        }

        return sb.toString();
    }

    public synchronized String getGroupMembers(String group) {
        List<String> users = groups.get(group);

        if (users == null) {
            return "Grupo não existe";
        }

        return group + ": " + users;
    }

    public String getHelpText() {
        return """
                    === COMANDOS DISPONÍVEIS ===
                    /help                         - Mostra todos os comandos
                    /quit                         - Sai do servidor
                    /online_users                 - Lista usuários conectados
                    /create_group @g u1 u2 ...    - Cria grupo
                    /groups                       - Lista grupos existentes
                    /join_group @g                - Entrar em grupo
                    /leave_group @g               - Sair de grupo
                    /group_info @g                - Ver membros do grupo
                
                    === ADMIN (apenas @admins) ===
                    /admin_add_user @g user      - Adiciona usuário ao grupo
                    /admin_remove_user @g user   - Remove usuário do grupo
                    /admin_delete_group @g       - Apaga grupo
                
                    === MENSAGENS ===
                    broadcast                    - mensagem para todos os usuários online
                    private (@usuario)           - mensagem enviada diretamente para um usuário
                    group (@grupo)               - mensagem enviada para todos do grupo
                
                    === SIGLAS / CONCEITOS ===
                    @usuario   = usuário específico conectado no servidor
                    @grupo     = grupo de usuários (ex: @devs, @admins)
                    u1 u2 ...  = lista de usuários usados ao criar grupo
                    @admins    = grupo especial de administradores do sistema
                
                    ============================
                """.stripIndent();
    }

    private void shutdown() {
        try {
            printDebug("[SERVER] Encerrando");
            server_socket.close();
        } catch (IOException e) {
            printDebug("[SERVER] Erro ao fechar");
        }
    }
}