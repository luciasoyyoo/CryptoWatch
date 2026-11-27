package cryptowatch;

import cryptowatch.core.*;
import cryptowatch.data.*;
import cryptowatch.model.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Checks{
    public static void main(String[] args) throws Exception{
        String json="""
[{"id":"bitcoin","symbol":"btc","name":"Bitcoin","current_price":84123.45,
        "market_cap":1670000000000,"total_volume":4.1E10,"high_24h":85000,
        "low_24h":83000.5,"price_change_percentage_24h":-1.2345,
        "sparkline_in_7d":{"price":[84000.1,84100,8.39505e4]}}]
""";
        List<Coin> coins=CoinGeckoClient.parse(json);
        check(coins.size()==1,"parser devuelve una moneda");
        check(Math.abs(coins.get(0).price()-84123.45)<.001,"parser lee precio");
        check(coins.get(0).sparkline().size()==3,"parser lee sparkline");

        AbstractSubject<String> subject=new AbstractSubject<>(){};
        AtomicInteger count=new AtomicInteger();
        Observer<String> obs=e->count.incrementAndGet();
        subject.attach(obs);subject.notifyObservers("evento");
        check(count.get()==1,"Subject notifica al Observer");
        subject.detach(obs);subject.notifyObservers("evento");
        check(count.get()==1,"detach elimina el Observer");

        DemoMarketSource demo=new DemoMarketSource();
        List<Coin> first=demo.fetch(List.of("bitcoin"),"eur");
        List<Coin> second=demo.fetch(List.of("bitcoin"),"eur");
        check(!first.isEmpty()&&!second.isEmpty(),"fuente demo genera datos");
        check(first.get(0).price()!=second.get(0).price(),"fuente demo cambia el precio");
        System.out.println("Pruebas finalizadas.");
    }
    private static void check(boolean ok,String msg){
        System.out.println((ok?"OK   ":"FAIL ")+msg);
        if(!ok)throw new AssertionError(msg);
    }
}
