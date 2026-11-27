package cryptowatch.ui;

import cryptowatch.alerts.AlertService;
import cryptowatch.data.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class MainPanel extends JPanel{
    private final List<CoinCard> cards=new ArrayList<>();
    private final DetailPanel detail;
    public MainPanel(PriceFeed feed, AlertService alerts, List<String> ids,
                     MarketDataSource gecko, MarketDataSource demo, boolean demoMode){
        setBackground(Theme.BG);setLayout(new BorderLayout(0,10));
        JPanel header=new JPanel(new BorderLayout(10,4));header.setOpaque(false);
        JLabel title=new JLabel("CryptoWatch");
        title.setForeground(Theme.TEXT);title.setFont(Theme.font(Font.BOLD,28));
        JLabel sub=new JLabel("Monitorización de criptomonedas · Patrón Observador");
        sub.setForeground(Theme.MUTED);sub.setFont(Theme.font(Font.PLAIN,12));
        JPanel labels=new JPanel(new GridLayout(2,1));labels.setOpaque(false);labels.add(title);labels.add(sub);
        header.add(labels,BorderLayout.WEST);

        JPanel controls=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,4));controls.setOpaque(false);
        JComboBox<String> source=new JComboBox<>(new String[]{"CoinGecko","Demo"});
        source.setSelectedIndex(demoMode?1:0);source.addActionListener(e->feed.setSource(source.getSelectedIndex()==0?gecko:demo));
        JComboBox<String> currency=new JComboBox<>(new String[]{"EUR","USD"});
        currency.addActionListener(e->feed.setCurrency(((String)currency.getSelectedItem()).toLowerCase()));
        JComboBox<String> interval=new JComboBox<>(new String[]{"30 s","60 s","120 s"});
        interval.setSelectedIndex(1);interval.addActionListener(e->feed.setIntervalSeconds(30*(interval.getSelectedIndex()+1)));
        RefreshButton refresh=new RefreshButton(x->feed.refreshNow());
        controls.add(new JLabel("Fuente"));controls.add(source);controls.add(new JLabel("Moneda"));controls.add(currency);
        controls.add(new JLabel("Intervalo"));controls.add(interval);controls.add(refresh);header.add(controls,BorderLayout.EAST);
        add(header,BorderLayout.NORTH);

        detail=new DetailPanel(ids.get(0));
        JPanel grid=new JPanel(new GridLayout(2,3,10,10));grid.setOpaque(false);
        for(String id:ids){
            CoinCard card=new CoinCard(id).onSelect(this::select);
            cards.add(card);grid.add(card);feed.attach(EdtObserver.of(card));
        }
        feed.attach(EdtObserver.of(detail));feed.attach(EdtObserver.of(refresh));
        StatusBar status=new StatusBar(feed);feed.attach(EdtObserver.of(status));
        LogPanel log=new LogPanel(alerts);
        feed.attach(EdtObserver.of(log.marketObserver()));alerts.attach(EdtObserver.of(log.alertObserver()));

        JPanel center=new JPanel(new BorderLayout(10,10));center.setOpaque(false);center.add(grid,BorderLayout.CENTER);center.add(detail,BorderLayout.SOUTH);
        add(center,BorderLayout.CENTER);add(log,BorderLayout.EAST);add(status,BorderLayout.SOUTH);
        select(ids.get(0));
    }
    private void select(String id){
        detail.select(id);
        for(CoinCard c:cards)c.setSelected(c.coinId().equals(id));
    }
}
