package cryptowatch.ui;

import cryptowatch.alerts.AlertService;
import cryptowatch.core.Observer;
import cryptowatch.model.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class LogPanel extends JPanel{
    private final DefaultListModel<String> model=new DefaultListModel<>();
    public LogPanel(AlertService alerts){
        setOpaque(false);setLayout(new BorderLayout(0,6));
        JLabel title=new JLabel("Actividad y alertas");title.setForeground(Theme.TEXT);title.setFont(Theme.font(Font.BOLD,15));
        JPanel head=new JPanel(new BorderLayout());head.setOpaque(false);head.add(title,BorderLayout.WEST);
        JPanel chips=new JPanel(new FlowLayout(FlowLayout.RIGHT,4,0));chips.setOpaque(false);
        for(double t:new double[]{0.1,0.5,1.0}){
            JButton b=new JButton(t+" %");b.setMargin(new Insets(2,6,2,6));b.addActionListener(e->alerts.setThresholdPct(t));chips.add(b);
        }
        head.add(chips,BorderLayout.EAST);add(head,BorderLayout.NORTH);
        JList<String> list=new JList<>(model);list.setBackground(Theme.PANEL);list.setForeground(Theme.TEXT);
        add(new JScrollPane(list),BorderLayout.CENTER);setPreferredSize(new Dimension(360,180));
    }
    public Observer<MarketEvent> marketObserver(){return e->{
        if(e instanceof MarketEvent.Loading)add("INFO","Consultando datos…");
        else if(e instanceof MarketEvent.Updated u)add("OK","Datos recibidos: "+u.coins().size()+" monedas");
        else if(e instanceof MarketEvent.Failed f)add("ERROR",f.message());
    };}
    public Observer<Alert> alertObserver(){return a->add(a.kind()==Alert.Kind.SURGE?"SUBIDA":"BAJADA",
            a.coinName()+" "+String.format("%+.2f %%",a.changePct())+" · "+a.price()+" "+a.currency());}
    private void add(String level,String text){
        SwingUtilities.invokeLater(()->{
            model.add(0,LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" · "+level+" · "+text);
            if(model.size()>150)model.removeElementAt(model.size()-1);
        });
    }
}
