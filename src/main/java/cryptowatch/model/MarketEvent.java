package cryptowatch.model;

import java.time.Instant;
import java.util.List;

public sealed interface MarketEvent permits MarketEvent.Loading, MarketEvent.Updated, MarketEvent.Failed {
    Instant at();

    record Loading(Instant at) implements MarketEvent {}
    record Updated(List<Coin> coins, String source, String currency, Instant at) implements MarketEvent {}
    record Failed(String message, Instant at) implements MarketEvent {}
}
