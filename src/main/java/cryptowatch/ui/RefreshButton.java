package cryptowatch.ui;

import cryptowatch.core.Observer;
import cryptowatch.model.MarketEvent;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public final class RefreshButton extends JButton implements Observer<MarketEvent>{
    private boolean loading;
    public RefreshButton(Consumer<Void> action){
        super("Actualizar");setFocusPainted(false);addActionListener(e->action.accept(null));
    }
    @Override public void update(MarketEvent event){
        if(event instanceof MarketEvent.Loading)loading=true;
        else if(event instanceof MarketEvent.Updated || event instanceof MarketEvent.Failed)loading=false;
        setText(loading?"Consultando…":"Actualizar");setEnabled(!loading);
    }
}
