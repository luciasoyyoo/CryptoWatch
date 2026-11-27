package cryptowatch.ui;

import cryptowatch.core.Observer;
import cryptowatch.data.PriceFeed;
import cryptowatch.model.MarketEvent;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class StatusBar extends JComponent implements Observer<MarketEvent>{
    private final PriceFeed feed;
    private String text="Conectando…";
    private Color color=Theme.MUTED;
    private LocalTime last;
    public StatusBar(PriceFeed feed){this.feed=feed;setPreferredSize(new Dimension(100,34));new Timer(500,e->repaint()).start();}
    @Override public void update(MarketEvent event){
        if(event instanceof MarketEvent.Loading){text="Consultando CoinGecko…";color=Theme.AMBER;}
        else if(event instanceof MarketEvent.Updated u){text="Conectado · "+u.source()+" · "+u.currency();color=Theme.UP;last=LocalTime.now();}
        else if(event instanceof MarketEvent.Failed f){text=f.message();color=Theme.DOWN;}
        repaint();
    }
    @Override protected void paintComponent(Graphics g0){
        Graphics2D g=(Graphics2D)g0.create();Theme.quality(g);
        g.setColor(color);g.fillOval(8,12,9,9);
        g.setColor(Theme.MUTED);g.setFont(Theme.font(Font.PLAIN,12));
        String suffix=last==null?"":" · última "+last.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        g.drawString(text+suffix,24,21);
        String next="Próxima: "+feed.secondsUntilNext()+" s";
        int w=g.getFontMetrics().stringWidth(next);g.drawString(next,getWidth()-w-12,21);g.dispose();
    }
}
