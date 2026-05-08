void main() {
    try {
        int server_port = 1234;
        TCPChatClient client = new TCPChatClient("localhost", server_port);
        Thread client_thread = new Thread(client);

        client_thread.start();
        System.out.println("[MAIN TCP CLIENT] TCPChatClient iniciado na porta");

        client_thread.join();
        System.out.println("[MAIN TCP CLIENT] TCPChatClient encerrado");
    } catch (Exception e) {
        System.out.println("[MAIN TCP CLIENT Erro ao iniciar cliente: " + e.getMessage());
        e.printStackTrace();
    }
}