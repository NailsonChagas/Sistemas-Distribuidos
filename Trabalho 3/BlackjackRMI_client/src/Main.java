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

        boolean continueGame = true;

        while (continueGame) {

            System.out.println("\n=== Nova rodada ===");
            System.out.println(game.startRound(name));

            boolean playing = true;

            while (playing) {

                System.out.println("\n1 - Pedir carta");
                System.out.println("2 - Parar");
                System.out.println("3 - Sair");

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
                        playing = false;
                        continueGame = false;
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }
            }

            if (continueGame) {
                System.out.print("\nDeseja jogar novamente? (s/n): ");
                scanner.nextLine(); // limpa o buffer
                String answer = scanner.nextLine();

                if (!answer.equalsIgnoreCase("s")) {
                    continueGame = false;
                }
            }
        }

        System.out.println("Até a próxima!");

    } catch (Exception e) {
        System.out.println("Erro ao conectar ao servidor.");
        e.printStackTrace();
    }
}