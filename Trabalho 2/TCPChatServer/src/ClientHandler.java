import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Map<String, Command> commands = new HashMap<>();
    private final Socket client_socket;
    private final TCPChatServer server_ref;
    private ObjectInputStream in_stream;
    private ObjectOutputStream out_stream;
    private String username;
    
    public ClientHandler(Socket client_socket, TCPChatServer server_ref) {
        this.client_socket = client_socket;
        this.server_ref = server_ref;
        initCommands();
    }

    private void initCommands() {
        commands.put("/quit", _ -> {
            sendSystem("Saindo...");
            return false;
        });

        commands.put("/online_users", _ -> {
            sendSystem(server_ref.getOnlineUsers());
            return true;
        });

        commands.put("/groups", _ -> {
            sendSystem(server_ref.getGroups());
            return true;
        });

        commands.put("/help", _ -> {
            sendSystem(server_ref.getHelpText());
            return true;
        });

        commands.put("/group_info", args -> {
            if (args.length != 2) {
                sendSystem("Uso: /group_info @grupo");
                return true;
            }

            sendSystem(server_ref.getGroupMembers(args[1]));
            return true;
        });

        commands.put("/join_group", args -> {
            if (args.length != 2) {
                sendSystem("Uso: /join_group @grupo");
                return true;
            }

            boolean ok = server_ref.addUserToGroup(args[1], username);
            sendSystem(ok ? "Entrou no grupo " + args[1] : "Grupo não existe");
            return true;
        });

        commands.put("/leave_group", args -> {
            if (args.length != 2) {
                sendSystem("Uso: /leave_group @grupo");
                return true;
            }

            boolean ok = server_ref.removeUserFromGroup(args[1], username);
            sendSystem(ok ? "Saiu do grupo " + args[1] : "Grupo não existe");
            return true;
        });

        commands.put("/create_group", args -> {
            if (args.length < 3) {
                sendSystem("Uso: /create_group @grupo user1 user2 ...");
                return true;
            }

            String group = args[1];
            String[] users = java.util.Arrays.copyOfRange(args, 2, args.length);

            boolean ok = server_ref.createGroup(group, users);

            sendSystem(ok ? "Grupo criado: " + group : "Erro ao criar grupo");
            return true;
        });

        commands.put("/admin_remove_user", args -> {
            if (!server_ref.isAdmin(username)) {
                sendSystem("Acesso negado (admin only)");
                return true;
            }

            if (args.length != 3) {
                sendSystem("Uso: /admin_remove_user @grupo usuario");
                return true;
            }

            String group = args[1];
            String user = args[2];

            boolean ok = server_ref.removeUserFromGroup(group, user);

            sendSystem(ok
                    ? "Usuário removido do grupo"
                    : "Erro: grupo ou usuário inválido");

            return true;
        });

        commands.put("/admin_add_user", args -> {
            if (!server_ref.isAdmin(username)) {
                sendSystem("Acesso negado (admin only)");
                return true;
            }

            if (args.length != 3) {
                sendSystem("Uso: /admin_add_user @grupo usuario");
                return true;
            }

            String group = args[1];
            String user = args[2];

            boolean ok = server_ref.addUserToGroup(group, user);

            sendSystem(ok
                    ? "Usuário adicionado ao grupo"
                    : "Erro: grupo inválido");

            return true;
        });

        commands.put("/admin_delete_group", args -> {
            if (!server_ref.isAdmin(username)) {
                sendSystem("Acesso negado (admin only)");
                return true;
            }

            if (args.length != 2) {
                sendSystem("Uso: /admin_delete_group @grupo");
                return true;
            }

            String group = args[1];

            boolean ok = server_ref.deleteGroup(group);

            sendSystem(ok
                    ? "Grupo removido com sucesso"
                    : "Grupo não encontrado");

            return true;
        });
    }

    @Override
    public void run() {
        try {
            out_stream = new ObjectOutputStream(client_socket.getOutputStream());
            in_stream = new ObjectInputStream(client_socket.getInputStream());

            username = in_stream.readUTF();
            server_ref.addUser(username, this);

            while (true) {

                Mensagem msg = (Mensagem) in_stream.readObject();

                String content = msg.getConteudo();

                if (content.startsWith("/")) {
                    running = handleCommand(content);
                    continue;
                }

                server_ref.processMessage(msg);
            }

        } catch (IOException | ClassNotFoundException e) {
            server_ref.printDebug("[ClientHandler] Cliente desconectado: " + username);
        } finally {
            server_ref.removeUser(username);
            close();
        }
    }

    private boolean handleCommand(String command) {
        String[] args = command.split(" ");
        String base = args[0];

        Command cmd = commands.get(base);

        if (cmd == null) {
            sendSystem("Comando inválido");
            return true;
        }

        return cmd.execute(args);
    }

    public synchronized void sendMessage(Mensagem msg) { // só enviar uma mensagem por vez
        try {
            out_stream.writeObject(msg);
            out_stream.flush();
        } catch (IOException e) {
            server_ref.printDebug("[ClientHandler] Erro envio: " + username);
        }
    }

    private void sendSystem(String text) {
        Mensagem sys = new Mensagem("SERVER", username, "[SERVER] " + text);
        sendMessage(sys);
    }

    private void close() {
        try {
            if (in_stream != null) in_stream.close();
            if (out_stream != null) out_stream.close();
            if (client_socket != null) client_socket.close();
        } catch (IOException e) {
            server_ref.printDebug("[ClientHandler] erro close");
        }
    }

    @FunctionalInterface
    interface Command {
        boolean execute(String[] args);
    }
}
