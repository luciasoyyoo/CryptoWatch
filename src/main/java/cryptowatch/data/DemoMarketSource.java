package cryptowatch.data;

import cryptowatch.model.Coin;
import java.util.*;

public final class DemoMarketSource implements MarketDataSource {
    private record Seed(String symbol, String name, double usd, double cap, double volume) {}
    private static final Map<String,Seed> SEEDS = Map.of(
            "bitcoin", new Seed("btc","Bitcoin",84000,1.67e12,4.1e10),
            "ethereum", new Seed("eth","Ethereum",2750,3.3e11,1.9e10),
            "solana", new Seed("sol","Solana",128,6.2e10,3.2e9),
            "ripple", new Seed("xrp","XRP",2.05,1.2e11,3.9e9),
            "cardano", new Seed("ada","Cardano",0.56,2.0e10,6.5e8),
            "dogecoin", new Seed("doge","Dogecoin",0.15,2.2e10,1.4e9));
    private static final int POINTS=168;
    private final Random random=new Random();
    private final Map<String,List<Double>> series=new HashMap<>();

    @Override public synchronized List<Coin> fetch(List<String> ids,String currency) {
        double fx="eur".equalsIgnoreCase(currency)?0.92:1.0;
        List<Coin> out=new ArrayList<>();
        for(String id:ids){
            Seed seed=SEEDS.get(id); if(seed==null) continue;
            List<Double> s=series.computeIfAbsent(id,k->history(seed));
            double previous=s.get(s.size()-1);
            double next=previous*(1+random.nextGaussian()*0.003);
            s.add(next); s.remove(0);
            double high=s.stream().mapToDouble(Double::doubleValue).max().orElse(next);
            double low=s.stream().mapToDouble(Double::doubleValue).min().orElse(next);
            double change=(next/previous-1)*100;
            List<Double> spark=s.stream().map(v->v*fx).toList();
            out.add(new Coin(id,seed.symbol,seed.name,next*fx,change,
                    seed.cap*(next/seed.usd)*fx,seed.volume*(0.9+random.nextDouble()*0.2)*fx,
                    high*fx,low*fx,spark));
        }
        return out;
    }

    private List<Double> history(Seed seed){
        List<Double> s=new ArrayList<>(POINTS);
        double v=seed.usd();
        for(int i=0;i<POINTS;i++){ v*=1+random.nextGaussian()*0.008; s.add(v); }
        return s;
    }
    @Override public String name(){return "Demo";}
}
