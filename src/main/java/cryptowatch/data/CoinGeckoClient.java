package cryptowatch.data;

import cryptowatch.model.Coin;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CoinGeckoClient implements MarketDataSource {
    private static final String ENDPOINT = "https://api.coingecko.com/api/v3/coins/markets";
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL).build();
    private final String apiKey;
    public CoinGeckoClient(String apiKey) {
        this.apiKey = apiKey == null || apiKey.isBlank() ? null : apiKey.trim();
    }

    @Override
    public List<Coin> fetch(List<String> ids, String currency) throws IOException, InterruptedException {
        String url = ENDPOINT
                + "?vs_currency=" + enc(currency)
                + "&ids=" + enc(String.join(",", ids))
                + "&order=market_cap_desc&per_page=" + ids.size()
                + "&page=1&sparkline=true&price_change_percentage=24h";

        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .header("User-Agent", "CryptoWatch/1.0")
                .GET();
        if (apiKey != null) b.header("x-cg-demo-api-key", apiKey);

        HttpResponse<String> r = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 429)
            throw new IOException("CoinGecko: límite de peticiones alcanzado (HTTP 429).");
        if (r.statusCode() == 401 || r.statusCode() == 403)
            throw new IOException("CoinGecko requiere una clave Demo válida. Configura COINGECKO_API_KEY.");
        if (r.statusCode() != 200)
            throw new IOException("CoinGecko respondió con HTTP " + r.statusCode());
        return parse(r.body());
    }

    public static List<Coin> parse(String json) throws IOException {
        final Object root;
        try { root = MiniJson.parse(json); }
        catch (IllegalArgumentException e) { throw new IOException("JSON inválido: " + e.getMessage(), e); }
        if (!(root instanceof List<?> list)) throw new IOException("Respuesta inesperada: no es una lista");

        List<Coin> result = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?,?> m)) continue;
            List<Double> spark = new ArrayList<>();
            Object sp = m.get("sparkline_in_7d");
            if (sp instanceof Map<?,?> sm && sm.get("price") instanceof List<?> values) {
                for (Object v : values) if (v instanceof Number n) spark.add(n.doubleValue());
            }
            result.add(new Coin(
                    MiniJson.str(m,"id",""),
                    MiniJson.str(m,"symbol",""),
                    MiniJson.str(m,"name",""),
                    MiniJson.num(m,"current_price",0),
                    MiniJson.num(m,"price_change_percentage_24h",0),
                    MiniJson.num(m,"market_cap",0),
                    MiniJson.num(m,"total_volume",0),
                    MiniJson.num(m,"high_24h",0),
                    MiniJson.num(m,"low_24h",0),
                    spark));
        }
        return result;
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override public String name() { return "CoinGecko"; }
}
