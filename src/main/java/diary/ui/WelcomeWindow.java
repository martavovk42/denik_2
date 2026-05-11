package diary.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Loading/uvítací okno - zobrazí se při dalších spuštěních
 * a po krátké chvíli automaticky otevře kalendář.
 */
public class WelcomeWindow extends JFrame {

    private static final int DURATION_MS = 1400;

    public WelcomeWindow(String name, Runnable onDone) {
        setTitle("Deník");
        setSize(440, 320);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(Icons.calendarIcon(64));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Style.BG);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.PRIMARY, 2),
                new EmptyBorder(28, 32, 28, 32)
        ));

        // ikona kalendáře
        JLabel icon = new JLabel(new ImageIcon(Icons.calendarIcon(96)));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(icon, BorderLayout.NORTH);

        // pozdrav
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(18, 0, 0, 0));

        JLabel greet = new JLabel("Vítej zpět, " + name + "!", SwingConstants.CENTER);
        greet.setFont(Style.FONT_BIG);
        greet.setForeground(Style.TEXT);
        greet.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Načítám tvůj deník…", SwingConstants.CENTER);
        sub.setFont(Style.FONT_REG);
        sub.setForeground(Style.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(greet);
        center.add(Box.createVerticalStrut(6));
        center.add(sub);
        root.add(center, BorderLayout.CENTER);

        // progress bar
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setForeground(Style.PRIMARY);
        bar.setBackground(Style.SURFACE);
        bar.setBorder(BorderFactory.createLineBorder(Style.BORDER, 1));
        bar.setPreferredSize(new Dimension(0, 8));
        bar.setStringPainted(false);
        root.add(bar, BorderLayout.SOUTH);

        add(root);

        // jednoduchá animace progress baru
        Timer timer = new Timer(DURATION_MS / 50, null);
        final int[] tick = {0};
        timer.addActionListener(e -> {
            tick[0] += 2;
            bar.setValue(Math.min(tick[0], 100));
            if (tick[0] >= 100) {
                timer.stop();
                dispose();
                onDone.run();
            }
        });
        timer.start();

        setVisible(true);
    }
}
