public class LinearSearch {

    public static int sequentialSearch(int[] arr, int x)
    {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x)
                return i;
        }
        return -1;
    }

    public static int parallelSearch(int[] arr, int x)
    {
        int max_num_threads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[max_num_threads];

        SearchResult result = new SearchResult(); //compartilhado entre as threads

        // tamanho do array que cada thread vai trabalahr
        int chunk_size = (int) Math.ceil((double) arr.length / max_num_threads);

        for (int i = 0; i < max_num_threads; i++) {
            int start_index = i * chunk_size;
            int end_index = Math.min(start_index + chunk_size, arr.length); // n passar do tamanho max do array

            threads[i] = new Thread(() -> {
                for (int j = start_index; j < end_index; j++) {
                    if (result.index != -1) return; // já encontrado

                    if (arr[j] == x) {
                        result.setIndex(j);
                        return;
                    }
                }
            });

            threads[i].start();
        }

        // esperar todas terminarem de procurar
        for (int i = 0; i < max_num_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result.index;
    }



    static class SearchResult {
        /*
        volatile em Java é um modificador usado em variáveis para garantir que todas
        as threads vejam sempre o valor mais recente dessa variável.

        Em programas com múltiplas threads, cada thread pode trabalhar com cópias
        locais (cache) das variáveis.

        Visibilidade -> mudanças feitas por uma thread são vistas pelas outras imediatamente.
         */
        volatile int index = -1;

        // metodo com synchronized -> apenas uma thread executa por vez
        synchronized void setIndex(int i) {
            if (index == -1) {
                index = i;
            }
        }
    }
}
