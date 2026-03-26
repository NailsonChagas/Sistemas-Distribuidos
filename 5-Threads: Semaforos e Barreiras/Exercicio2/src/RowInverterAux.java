import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class RowInverterAux implements Runnable {
    private final int[] row;
    private final CyclicBarrier barrier;

    public RowInverterAux(int[] row, CyclicBarrier barrier) {
        this.row = row;
        this.barrier = barrier;
    }

    private void invertRow() {
        int left = 0, right = row.length - 1;
        while (left < right) { // aproximar left e right até chegar no meio
            int temp = row[left];
            row[left] = row[right];
            row[right] = temp;
            left++;
            right--;
        }
    }

    @Override
    public void run() {
        invertRow();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}