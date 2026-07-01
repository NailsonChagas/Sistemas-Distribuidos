import interfaces.BlackjackRemoteInterface;

import java.rmi.Naming;
import java.util.Scanner;

void main() {
    try {
        BlackjackRemoteInterface game =
                (BlackjackRemoteInterface) Naming.lookup("rmi://localhost/Blackjack");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite seu nome: ");
        String name = scanner.nextLine();

        Thread heartbeatThread = startHeartbeat(game, name);

        runGame(game, scanner, name);

        heartbeatThread.interrupt();

        System.out.println("Até a próxima!");

    } catch (Exception e) {
        System.out.println("Erro ao conectar ao servidor.");
        e.printStackTrace();
    }
}

private static Thread startHeartbeat(
        BlackjackRemoteInterface game,
        String name) {

    Thread heartbeatThread = new Thread(() -> {

        while (!Thread.currentThread().isInterrupted()) {

            try {
                game.heartbeat(name);

                // envia heartbeat a cada 2 s
                Thread.sleep(2000);

            } catch (Exception e) {
                break;
            }
        }
    });

    heartbeatThread.setDaemon(true);
    heartbeatThread.start();

    return heartbeatThread;
}

private static void runGame(
        BlackjackRemoteInterface game,
        Scanner scanner,
        String name) throws Exception {

    boolean continueGame = true;

    while (continueGame) {

        System.out.println("\n=== Nova rodada ===");
        System.out.println(game.startRound(name));

        boolean keepPlaying = playRound(game, scanner, name);

        if (!keepPlaying) {
            break;
        }

        System.out.print(game.score(name));

        System.out.print("\nDeseja jogar novamente? (s/n): ");
        scanner.nextLine(); // limpa buffer

        String answer = scanner.nextLine();

        if (!answer.equalsIgnoreCase("s")) {
            continueGame = false;
        }
    }
}

private static boolean playRound(
        BlackjackRemoteInterface game,
        Scanner scanner,
        String name) throws Exception {

    boolean playing = true;

    while (playing) {

        showMenu();

        System.out.print("Selecionado: ");
        int option = scanner.nextInt();

        switch (option) {

            case 1:
                String result = game.hit(name);

                System.out.println(result);

                if (result.contains("estourou") || result.isBlank()) {
                    playing = false;
                }
                break;

            case 2:
                System.out.println(game.stand(name));
                playing = false;
                break;

            case 3:
                System.out.println(game.players());
                break;

            case 4:
                game.quit(name);
                return false;

            default:
                System.out.println("Opção inválida.");
        }
    }

    return true;
}

private static void showMenu() {
    System.out.println("\n1 - Pedir carta");
    System.out.println("2 - Parar");
    System.out.println("3 - Players");
    System.out.println("4 - Sair");
}