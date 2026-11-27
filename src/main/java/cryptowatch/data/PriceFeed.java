package cryptowatch.data;

import cryptowatch.core.AbstractSubject;
import cryptowatch.model.Coin;
import cryptowatch.model.MarketEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

public final class PriceFeed extends AbstractSubject<MarketEvent> implements AutoCloseable {
    private final List<String> ids;
    private final ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(r->{
        Thread t=new Thread(r,"price-feed"); t.setDaemon(true); return t;
    });
    private volatile MarketDataSource source;
    private volatile String currency="eur";
    private volatile int intervalSeconds=60;
    private volatile Instant nextPoll=Instant.now();
    private ScheduledFuture<?> task;

    public PriceFeed(List<String> ids, MarketDataSource source){this.ids=List.copyOf(ids);this.source=source;}
    public void start(){schedule(0);}
    public void refreshNow(){schedule(0);}
    public void setSource(MarketDataSource source){this.source=source;refreshNow();}
    public void setCurrency(String currency){this.currency=currency.toLowerCase(Locale.ROOT);refreshNow();}
    public void setIntervalSeconds(int seconds){this.intervalSeconds=Math.max(10,seconds);schedule(0);}
    public long secondsUntilNext(){return Math.max(0,Duration.between(Instant.now(),nextPoll).getSeconds());}

    private synchronized void schedule(long delay){
        if(task!=null) task.cancel(false);
        nextPoll=Instant.now().plusSeconds(delay);
        task=scheduler.scheduleWithFixedDelay(this::poll,delay,intervalSeconds,TimeUnit.SECONDS);
    }

    private void poll(){
        MarketDataSource src=source; String cur=currency;
        try{
            nextPoll=Instant.now().plusSeconds(intervalSeconds);
            notifyObservers(new MarketEvent.Loading(Instant.now()));
            List<Coin> coins=src.fetch(ids,cur);
            notifyObservers(new MarketEvent.Updated(coins,src.name(),cur.toUpperCase(Locale.ROOT),Instant.now()));
        }catch(InterruptedException e){Thread.currentThread().interrupt();}
        catch(Exception e){notifyObservers(new MarketEvent.Failed(message(e),Instant.now()));}
        finally{nextPoll=Instant.now().plusSeconds(intervalSeconds);}
    }

    private String message(Exception e){
        String m=e.getMessage(); return m==null?e.getClass().getSimpleName():m;
    }
    @Override public void close(){scheduler.shutdownNow();}
}
