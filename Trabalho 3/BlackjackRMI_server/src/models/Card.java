package models;

public class Card {
    private String suit; //naipe
    private String value;

    public Card(String value, String suit) {
        this.suit = suit;
        this.value = value;
    }

    @Override
    public String toString() {
        return value + " de " + suit;
    }

    public int getNumericValue() {
        return switch (value) {
            case "A" -> 11;
            case "J", "Q", "K" -> 10;
            default -> Integer.parseInt(value);
        };
    }
}
