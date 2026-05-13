void main() {
    try {
        int port = 1234;
        int backlog = 50;
        int empty_timeout_ms = 30000; // 30 segundos
        boolean debug = true;

        TCPChatServer server = new TCPChatServer(
            port,
            backlog,
            empty_timeout_ms,
            debug
        );

        Thread server_thread = new Thread(server);
        server_thread.start();

        System.out.println("[MAIN TCP SERVER] Servidor iniciado na porta " + port);
        server_thread.join();
    } catch (Exception e) {
        System.out.println("[MAIN TCP SERVER] Erro ao iniciar servidor: " + e.getMessage());
        e.printStackTrace();
    }
}