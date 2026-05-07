import java.io.Serializable;

public record CustomRequest(
        String type,
        String func,
        Object data
) implements Serializable {
}