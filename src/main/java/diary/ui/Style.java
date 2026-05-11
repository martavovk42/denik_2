package diary.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Centrální místo pro barvy, písma a styly tlačítek.
 * Cílem je jednotný a teplý vzhled napříč celou aplikací.
 */
public class Style {

    // 🎨 paleta - tlumená, ale barevná (krémová + zelená šalvěj + terakota)
    public static final Color BG           = new Color(0xF2FAF5);
    public static final Color SURFACE      = new Color(0xF7F8F8);
    public static final Color PRIMARY      = new Color(0x5B8D72);
    public static final Color PRIMARY_DARK = new Color(0x3F6651);
    public static final Color ACCENT       = new Color(0x2B6B46);
    public static final Color ACCENT_SOFT  = new Color(0xC1E8D2);
    public static final Color TEXT         = new Color(0x2D2A32);
    public static final Color TEXT_MUTED   = new Color(0x6B6770);
    public static final Color BORDER       = new Color(0xD7E3E3);
    public static final Color TODAY        = new Color(0x2B6B46);
    public static final Color DANGER       = new Color(0xB85450);

    // odstín pro dny podle počtu zápisů (1, 2, 3, 4, 5+)
    public static final Color[] COUNT_COLORS = {
            new Color(0xEBF2EC),
            new Color(0xCDE0CF),
            new Color(0xACCBB0),
            new Color(0x86B393),
            new Color(0x5B8D72)
    };
    public static final String FONT_NAME = pickFont();

    public static final Font FONT_SMALL   = new Font(FONT_NAME, Font.PLAIN, 12);
    public static final Font FONT_REG     = new Font(FONT_NAME, Font.PLAIN, 14);
    public static final Font FONT_BOLD    = new Font(FONT_NAME, Font.BOLD, 14);
    public static final Font FONT_MEDIUM  = new Font(FONT_NAME, Font.PLAIN, 16);
    public static final Font FONT_BIG     = new Font(FONT_NAME, Font.BOLD, 20);
    public static final Font FONT_HUGE    = new Font(FONT_NAME, Font.BOLD, 28);

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

    public static JButton primaryButton(String text) {
        return styledButton(text, PRIMARY, Color.WHITE, FONT_BOLD);
    }

    public static JButton accentButton(String text) {
        return styledButton(text, ACCENT, Color.WHITE, FONT_BOLD);
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBackground(SURFACE);
        b.setForeground(TEXT);
        b.setFont(FONT_REG);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton iconButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(TEXT);
        b.setFont(FONT_BIG);
        b.setBorder(new EmptyBorder(4, 10, 4, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JButton styledButton(String text, Color bg, Color fg, Font font) {
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

    public static JTextField inputField() {
        JTextField f = new JTextField();
        f.setFont(FONT_REG);
        f.setForeground(TEXT);
        f.setBackground(SURFACE);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    public static JTextArea textArea() {
        JTextArea a = new JTextArea();
        a.setFont(FONT_REG);
        a.setForeground(TEXT);
        a.setBackground(SURFACE);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(8, 10, 8, 10));
        return a;
    }

    public static JLabel headingLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BIG);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_REG);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return p;
    }

    /** Aplikuje pozadí, písmo a okraj na top-level kontejner. */
    public static void decorateFrame(JFrame f) {
        f.getContentPane().setBackground(BG);
    }
}
