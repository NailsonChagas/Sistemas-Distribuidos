import java.util.Random;

public class Moeda implements IMoeda {
    private final Random random;
    private int contadorCara;
    private int contadorCoroa;

    public Moeda() {
        this.random = new Random();
        this.contadorCara = 0;
        this.contadorCoroa = 0;
    }

    @Override
    public int arremessar() {
        int resultado = random.nextInt(2);

        if (resultado == 0) {
            contadorCara++;
        } else {
            contadorCoroa++;
        }

        return resultado;
    }

    @Override
    public int getContadorCara() {
        return contadorCara;
    }

    @Override
    public int getContadorCoroa() {
        return contadorCoroa;
    }

    @Override
    public void zerarContadores() {
        contadorCara = 0;
        contadorCoroa = 0;
    }
}