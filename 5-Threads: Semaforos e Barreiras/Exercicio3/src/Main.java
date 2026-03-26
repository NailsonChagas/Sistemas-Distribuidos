/* Jantar dos Filósofos: N filósofos estão sentados ao redor de uma mesa circular. Entre cada par de filósofos, há um
único garfo, totalizando cinco garfos. Para comer, um filósofo precisa pegar os dois garfos ao seu lado (o da esquerda e
 o da direita).

Cada filósofo alterna entre pensar e comer:

Para pensar: o filósofo apenas imprime uma mensagem.
Para comer: o filósofo deve pegar os dois garfos, um de cada lado.

Após comer, ele devolve os garfos para que outros filósofos possam usá-los.

Objetivo: implementar uma simulação deste problema utilizando threads para representar os filósofos e semáforos para
representar os garfos. Cada garfo deve ser protegido por um Semaphore com uma única permissão.
*/
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        int num_forks = 5;
        Semaphore[] forks = new Semaphore[num_forks];

        for (int i = 0; i < num_forks; i++) {
            forks[i] = new Semaphore(1);
        }

        // circular -> p f p f p f p f p f -> circular

        Thread[] p_threads = new Thread[num_forks];

        for (int i = 0; i < num_forks; i++) {
            Semaphore l_fork = forks[i];
            Semaphore r_fork = forks[(i + 1) % num_forks];
            p_threads[i] = new Thread(new Philosopher(i, l_fork, r_fork));
            p_threads[i].start();
        }
    }
}