package cryptowatch.ui;

import cryptowatch.core.Observer;
import cryptowatch.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public final class CoinCard extends JComponent implements Observer<MarketEvent>{
    private final String id;
    private Coin coin;
    private boolean selected;
    private Consumer<String> onSelect=x->{};
    private float flash;
    private String currency="EUR";

    public CoinCard(String id){
        this.id=id; setPreferredSize(new Dimension(280,155));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){onSelect.accept(id);}});
        new Timer(40,e->{if(flash>0){flash-=.08f;repaint();}}){{start();}};
    }
    public String coinId(){return id;}
    public CoinCard onSelect(Consumer<String> c){onSelect=c;return this;}
    public void setSelected(boolean v){selected=v;repaint();}
    @Override public void update(MarketEvent event){
        if(event instanceof MarketEvent.Updated u) for(Coin c:u.coins()) if(c.id().equals(id)){
            currency=u.currency();
            if(coin!=null && c.price()!=coin.price()) flash=1;
            coin=c; repaint();
        }
    }
    @Override protected void paintComponent(Graphics g0){
        Graphics2D g=(Graphics2D)g0.create();Theme.quality(g);
        g.setColor(selected?new Color(36,48,64):Theme.PANEL);
        g.fillRoundRect(2,2,getWidth()-4,getHeight()-4,16,16);
        if(flash>0){g.setColor(new Color(Theme.ACCENT.getRed(),Theme.ACCENT.getGreen(),Theme.ACCENT.getBlue(),(int)(70*flash)));g.fillRoundRect(2,2,getWidth()-4,getHeight()-4,16,16);}
        if(coin==null){draw(g,"Cargando…",18,30,Theme.MUTED,14);g.dispose();return;}
        draw(g,coin.name()+"  ("+coin.symbol().toUpperCase()+")",18,30,Theme.TEXT,15);
        draw(g,formatPrice(coin.price())+" "+currency(),18,70,Theme.TEXT,25);
        Color c=coin.change24h()>=0?Theme.UP:Theme.DOWN;
        draw(g,String.format("%+.2f %% (24 h)",coin.change24h()),18,98,c,14);
        draw(g,"Máx "+formatPrice(coin.high24h())+"   Mín "+formatPrice(coin.low24h()),18,125,Theme.MUTED,12);
        g.dispose();
    }
    private String currency(){return "EUR";}
    private String formatPrice(double v){
        if(v>=1000)return String.format("%,.0f",v);
        if(v>=1)return String.format("%,.2f",v);
        return String.format("%,.4f",v);
    }
    private void draw(Graphics2D g,String s,int x,int y,Color c,float size){g.setColor(c);g.setFont(Theme.font(Font.PLAIN,size));g.drawString(s,x,y);}
}
