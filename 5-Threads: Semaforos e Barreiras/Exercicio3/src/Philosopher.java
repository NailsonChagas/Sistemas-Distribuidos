import java.util.concurrent.Semaphore;

public class Philosopher implements Runnable {
    private int id;
    private Semaphore l_fork;
    private Semaphore r_fork;

    public Philosopher(int id, Semaphore l_fork, Semaphore r_fork) {
        this.id = id;
        this.l_fork = l_fork;
        this.r_fork = r_fork;
    }

    private void think() throws InterruptedException {
        System.out.println(id + " pensando");
        Thread.sleep((long) (Math.random() * 100)); // 0 a 100
    }

    private void eat() throws InterruptedException {
        System.out.println(id + " comendo");
        Thread.sleep((long) (Math.random() * 100)); // 0 a 100
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();

                // para n travar par começar pelo esquerdo e impar pelo direito
                if (id % 2 == 0) {
                    l_fork.acquire();
                    r_fork.acquire();
                } else {
                    r_fork.acquire();
                    l_fork.acquire();
                }

                eat();

                l_fork.release();
                r_fork.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
