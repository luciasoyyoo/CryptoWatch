package cryptowatch.alerts;

import cryptowatch.core.AbstractSubject;
import cryptowatch.core.Observer;
import cryptowatch.model.*;
import java.util.HashMap;
import java.util.Map;

public final class AlertService extends AbstractSubject<Alert> implements Observer<MarketEvent> {
    private final Map<String,Double> lastPrices=new HashMap<>();
    private String context="";
    private volatile double thresholdPct=0.5;

    public void setThresholdPct(double value){thresholdPct=Math.max(0.01,value);}
    public double thresholdPct(){return thresholdPct;}

    @Override public synchronized void update(MarketEvent event){
        if(!(event instanceof MarketEvent.Updated u)) return;
        String ctx=u.source()+"|"+u.currency();
        if(!ctx.equals(context)){lastPrices.clear();context=ctx;}
        for(Coin c:u.coins()){
            Double previous=lastPrices.put(c.id(),c.price());
            if(previous==null || previous==0) continue;
            double pct=(c.price()/previous-1)*100;
            if(Math.abs(pct)>=thresholdPct){
                notifyObservers(new Alert(c.id(),c.name(),c.symbol(),
                        pct>=0?Alert.Kind.SURGE:Alert.Kind.DROP,pct,c.price(),u.currency(),u.at()));
            }
        }
    }
}
