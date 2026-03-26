import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        int[][] matrix = {
                {1, 2, 3},
                {1, 2, 3},
                {1, 2, 3},
                {1, 2, 3}
        };

        // esperar as matrix.length threads terminarem antes de dar spawn na thread q printa
        CyclicBarrier barrier = new CyclicBarrier(matrix.length, () -> {
            System.out.println("Result:");
            for (int[] row : matrix) {
                for (int val : row) {
                    System.out.print(val + " ");
                }
                System.out.println();
            }
        });

//        for (int i = 0; i < matrix.length; i++) {
//            new Thread(new RowInverter(matrix[i], barrier)).start();
//        }
        for (int[] ints : matrix) {
            new Thread(new RowInverterAux(ints, barrier)).start();
        }

    }
}