package cryptowatch.model;

import java.time.Instant;

public record Alert(
        String coinId, String coinName, String symbol, Kind kind,
        double changePct, double price, String currency, Instant at) {
    public enum Kind { SURGE, DROP }
}
