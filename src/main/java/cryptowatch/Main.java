package cryptowatch;

import cryptowatch.alerts.AlertService;
import cryptowatch.data.*;
import cryptowatch.ui.*;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public final class Main{
    private static final List<String> COINS=List.of("bitcoin","ethereum","solana","ripple","cardano","dogecoin");
    private Main(){}
    public static void main(String[] args){
        boolean demo=Arrays.asList(args).contains("--demo");
        String key=System.getenv("COINGECKO_API_KEY");
        if(key==null||key.isBlank()) key=System.getProperty("coingecko.api.key");
        String apiKey=key;
        SwingUtilities.invokeLater(()->{
            MarketDataSource gecko=new CoinGeckoClient(apiKey);
            MarketDataSource demoSource=new DemoMarketSource();
            PriceFeed feed=new PriceFeed(COINS,demo?demoSource:gecko);
            AlertService alerts=new AlertService();
            feed.attach(alerts);
            JFrame frame=new JFrame("CryptoWatch · Monitor de criptomonedas");
            frame.setIconImage(Theme.logoImage(64));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new MainPanel(feed,alerts,COINS,gecko,demoSource,demo));
            frame.setMinimumSize(new Dimension(1200,760));frame.setSize(1350,820);frame.setLocationRelativeTo(null);
            frame.addWindowListener(new java.awt.event.WindowAdapter(){public void windowClosing(java.awt.event.WindowEvent e){feed.close();}});
            frame.setVisible(true);feed.start();
        });
    }
}
