package cryptowatch.model;

import java.util.List;

public record Coin(
        String id, String symbol, String name, double price,
        double change24h, double marketCap, double volume24h,
        double high24h, double low24h, List<Double> sparkline) {
    public Coin {
        sparkline = sparkline == null ? List.of() : List.copyOf(sparkline);
    }
}
