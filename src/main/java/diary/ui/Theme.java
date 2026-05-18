package diary.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Theme {
    Theme theme = new Twilight();

     Color BG()    ;
     Color SURFACE() ;
     Color PRIMARY() ;
     Color PRIMARY_DARK() ;
     Color ACCENT() ;
     Color ACCENT_TEXT();
     Color ACCENT_SOFT() ;
     Color TEXT()  ;
     Color TEXT_MUTED() ;
     Color BORDER() ;
     Color TODAY() ;
     Color DANGER()   ;


     String FONT_NAME = pickFont();

     Font FONT_SMALL   = new Font(FONT_NAME, Font.PLAIN, 12);
     Font FONT_REG     = new Font(FONT_NAME, Font.PLAIN, 14);
     Font FONT_BOLD    = new Font(FONT_NAME, Font.BOLD, 14);
     Font FONT_MEDIUM  = new Font(FONT_NAME, Font.PLAIN, 16);
     Font FONT_BIG     = new Font(FONT_NAME, Font.BOLD, 20);
     Font FONT_HUGE    = new Font(FONT_NAME, Font.BOLD, 28);

    default void applyHover(JComponent c, Color normalBg, Color hoverBg, Color normalFg, Color hoverFg) {
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                c.setBackground(hoverBg);
                c.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                c.setBackground(normalBg);
                c.setForeground(normalFg);
            }
        });
    }

    private static String pickFont() {
        String[] preferred = {
                "Lato"
        };

        String[] available = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        Set<String> set = new HashSet<>(Arrays.asList(available));

        for (String p : preferred) {
            if (set.contains(p)) {
                return p;
            }
        }

        return "SansSerif";
    }

    default JButton primaryButton(String text) {
        return styledButton(text, PRIMARY(), ACCENT_TEXT(), FONT_BOLD);
    }

    default JButton accentButton(String text) {
        return styledButton(text, ACCENT(), ACCENT_TEXT(), FONT_BOLD);
    }

    default JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBackground(SURFACE());
        b.setForeground(TEXT());
        b.setFont(FONT_REG);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER(), 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    default JButton iconButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(TEXT());
        b.setFont(FONT_BIG);
        b.setBorder(new EmptyBorder(4, 10, 4, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    default JButton styledButton(String text, Color bg, Color fg, Font font) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(font);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JTextField inputField() {
        JTextField f = new JTextField();
        f.setFont(FONT_REG);
        f.setForeground(theme.TEXT());
        f.setBackground(theme.SURFACE());
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(theme.BORDER(), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    static JTextArea textArea() {
        JTextArea a = new JTextArea();
        a.setFont(FONT_REG);
        a.setForeground(theme.TEXT());
        a.setBackground(theme.SURFACE());
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(8, 10, 8, 10));
        return a;
    }

    default JLabel headingLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BIG);
        l.setForeground(TEXT());
        return l;
    }

    default JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_REG);
        l.setForeground(TEXT_MUTED());
        return l;
    }

    default JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(SURFACE());
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER(), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return p;
    }

    default JMenuBar menuBar() {
    JMenuBar bar = new JMenuBar();
    bar.setBackground(ACCENT());
    bar.setForeground(ACCENT_TEXT());
    bar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    bar.setBorderPainted(false);
    bar.setBorder(BorderFactory.createEmptyBorder());

        return bar;

}

    default JMenu menu(String text) {
        JMenu m = new JMenu(text);
        m.setBackground(ACCENT());
        m.setForeground(ACCENT_TEXT());
        m.setFont(FONT_BOLD);
        m.setBorderPainted(false);
        m.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return m;
    }

    default JMenuItem menuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setOpaque(true); // u itemu to nevadí
        item.setBackground(SURFACE());
        item.setForeground(TEXT());
        item.setFont(FONT_BOLD);
        item.setBorder(new EmptyBorder(8, 18, 8, 18));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return item;
    }


   // Aplikuje pozadí, písmo a okraj na top-level kontejner.
    static void decorateFrame(JFrame f) {
        f.getContentPane().setBackground(theme.BG());
    }
    // odstín pro dny podle počtu zápisů (1, 2, 3, 4, 5+)
    default Color COUNT_COLORS(int count, Color bg) {
        return null;
    }

    default Color ACCESIBILITY(int count, Color fg){
        return null;
    }


    public class Twilight implements Theme {
        @Override public Color BG()           { return new Color(0x000000); }
        @Override public Color SURFACE()      { return new Color(0x201B49); }
        @Override public Color PRIMARY()      { return new Color(0x5D53C1); }
        @Override public Color PRIMARY_DARK() { return new Color(0x693EBA); }
        @Override public Color ACCENT()       { return new Color(0xEDD58F); }
        @Override public Color ACCENT_TEXT()  { return new Color(0x201B49); }
        @Override public Color ACCENT_SOFT()  { return new Color(0x453AA5); }
        @Override public Color TEXT()         { return new Color(0xFFFFFF); }
        @Override public Color TEXT_MUTED()   { return new Color(0xBCA9B7); }
        @Override public Color BORDER()       { return new Color(0x000000); }
        @Override public Color TODAY()        { return new Color(0x6255CA); }
        @Override public Color DANGER()       { return new Color(0xE1354E); }

        Color[] gradient = {
                new Color(0x463F88),
                new Color(0x625AAE),
                new Color(0x6F66C1),
                new Color(0x877FD5),
                new Color(0x9C94E4)
        };

        @Override
        public Color COUNT_COLORS(int count, Color bg) {
            int idx = Math.min(count - 1, gradient.length -1);
            bg = gradient[idx];
            return bg;
        }

        @Override
        public Color ACCESIBILITY(int count, Color fg){
            int idx = Math.min(count - 1, gradient.length -1);
            if (idx >= 3) fg = Color.WHITE;
            return fg;
        }



        }


    public class Green implements Theme{


        @Override  public Color BG()           { return new Color(0xF2FAF5); }
        @Override public Color SURFACE()      { return new Color(0xF7F8F8); }
        @Override  public Color PRIMARY()      { return new Color(0x5B8D72); }
        @Override public Color PRIMARY_DARK() { return new Color(0x3F6651); }
        @Override  public Color ACCENT() { return new Color(0x2B6B46); }
        @Override public Color ACCENT_TEXT()  { return new Color(0xF2FAF5); }
        @Override public Color ACCENT_SOFT()  { return new Color(0xC1E8D2); }
        @Override public Color TEXT()         { return new Color(0x2D2A32); }
        @Override public Color TEXT_MUTED()   { return new Color(0x6B6770); }
        @Override public Color BORDER()       { return new Color(0xD7E3E3); }
        @Override  public Color TODAY()        { return new Color(0x2B6B46); }
        @Override public Color DANGER()       { return new Color(0xB85450); }

        Color[] gradient = {
                new Color(0xD0EAD3),
                new Color(0xB6E6BC),
                new Color(0x90DF9A),
                new Color(0x5FB178),
                new Color(0x53B17D)
        };

        @Override
        public Color COUNT_COLORS(int count, Color bg) {
            int idx = Math.min(count - 1, gradient.length -1);
            bg = gradient[idx];
            return bg;
        }

        @Override
        public Color ACCESIBILITY(int count, Color fg){
            int idx = Math.min(count - 1, gradient.length -1);
            if (idx >= 3) fg = Color.WHITE;
            return fg;
        }
    }


    public class Pink implements Theme{


        @Override public Color BG()           { return new Color(0xF2FAF5); }
        @Override public Color SURFACE()      { return new Color(0xF7F8F8); }
        @Override public Color PRIMARY()      { return new Color(0xF47FD4); }
        @Override public Color PRIMARY_DARK() { return new Color(0xB553A0); }
        @Override public Color ACCENT()       { return new Color(0x800E79); }
        @Override public Color ACCENT_TEXT()  { return new Color(0xF2FAF5); }
        @Override public Color ACCENT_SOFT()  { return new Color(0xE8BADE); }
        @Override public Color TEXT()         { return new Color(0x2D2A32); }
        @Override public Color TEXT_MUTED()   { return new Color(0x6B6770); }
        @Override public Color BORDER()       { return new Color(0xD7E3E3); }
        @Override public Color TODAY()        { return new Color(0x800E79); }
        @Override public Color DANGER()       { return new Color(0x940E23); }

        Color[] gradient = {
                new Color(0xF1C7E5),
                new Color(0xEFA8DD),
                new Color(0xEA8DD4),
                new Color(0xF68CDE),
                new Color(0xF164D2)
        };

        @Override
        public Color COUNT_COLORS(int count, Color bg) {
            int idx = Math.min(count - 1, gradient.length -1);
            bg = gradient[idx];
            return bg;
        }

        @Override
        public Color ACCESIBILITY(int count, Color fg){
            int idx = Math.min(count - 1, gradient.length -1);
            if (idx >= 3) fg = Color.WHITE;
            return fg;
        }
    }

}
