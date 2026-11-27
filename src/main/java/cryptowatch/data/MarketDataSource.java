package cryptowatch.data;

import cryptowatch.model.Coin;
import java.io.IOException;
import java.util.List;

public interface MarketDataSource {
    List<Coin> fetch(List<String> ids, String currency) throws IOException, InterruptedException;
    String name();
}
