package cryptowatch.ui;

import cryptowatch.core.Observer;
import cryptowatch.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DetailPanel extends JComponent implements Observer<MarketEvent>{
    private final Map<String,Coin> latest=new LinkedHashMap<>();
    private String selected;
    private String currency="EUR";

    public DetailPanel(String initial){selected=initial;setPreferredSize(new Dimension(700,250));}
    public void select(String id){selected=id;repaint();}
    @Override public void update(MarketEvent event){
        if(event instanceof MarketEvent.Updated u){
            currency=u.currency();
            for(Coin c:u.coins())latest.put(c.id(),c);
            repaint();
        }
    }
    @Override protected void paintComponent(Graphics g0){
        Graphics2D g=(Graphics2D)g0.create();Theme.quality(g);
        g.setColor(Theme.PANEL);g.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
        Coin c=latest.get(selected);
        g.setColor(Theme.TEXT);g.setFont(Theme.font(Font.BOLD,16));
        g.drawString(c==null?"Evolución de precio":"Evolución 7 días · "+c.name(),20,28);
        if(c==null||c.sparkline().size()<2){g.setColor(Theme.MUTED);g.drawString("Esperando datos…",20,70);g.dispose();return;}
        List<Double>s=c.sparkline();double min=s.stream().mapToDouble(Double::doubleValue).min().orElse(0),max=s.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        int left=45,top=48,right=getWidth()-22,bottom=getHeight()-42;
        g.setColor(Theme.MUTED);g.drawLine(left,bottom,right,bottom);
        g.setColor(Theme.ACCENT);
        Path2D path=new Path2D.Double();
        for(int i=0;i<s.size();i++){
            double x=left+(right-left)*i/(double)(s.size()-1);
            double y=bottom-(bottom-top)*(s.get(i)-min)/Math.max(1e-9,max-min);
            if(i==0)path.moveTo(x,y);else path.lineTo(x,y);
        }
        g.draw(path);
        g.setColor(Theme.MUTED);g.setFont(Theme.font(Font.PLAIN,11));
        g.drawString(String.format("%.2f %s",max,currency),left,top-8);
        g.drawString(String.format("%.2f %s",min,currency),left,bottom+22);
        g.drawString("7 días",right-35,bottom+22);
        g.dispose();
    }
}
