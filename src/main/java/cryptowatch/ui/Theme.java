package cryptowatch.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Theme {
    public static final Color BG=new Color(14,17,23);
    public static final Color PANEL=new Color(22,27,34);
    public static final Color PANEL2=new Color(28,34,43);
    public static final Color TEXT=new Color(235,238,242);
    public static final Color MUTED=new Color(145,155,168);
    public static final Color ACCENT=new Color(95,155,255);
    public static final Color UP=new Color(54,211,153);
    public static final Color DOWN=new Color(255,91,110);
    public static final Color AMBER=new Color(245,180,70);
    public static final int GAP=12;
    private Theme(){}
    public static Font font(int style,float size){return new Font("SansSerif",style,Math.round(size));}
    public static void quality(Graphics2D g){g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);}
    public static BufferedImage logoImage(int size){
        BufferedImage img=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=img.createGraphics(); quality(g);
        g.setColor(ACCENT);g.fillRoundRect(2,2,size-4,size-4,18,18);
        g.setColor(Color.WHITE);g.setFont(font(Font.BOLD,size*.42f));
        FontMetrics fm=g.getFontMetrics();String s="₿";
        g.drawString(s,(size-fm.stringWidth(s))/2,(size-fm.getAscent()+fm.getDescent())/2+fm.getAscent());
        g.dispose();return img;
    }
}
