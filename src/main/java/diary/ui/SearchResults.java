package diary.ui;

import diary.model.Entry;
import diary.storage.FileStorage;
import diary.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Výsledky hledání slov v zápisech.
 */
public class SearchResults extends JFrame {

    private static final int IMG_MAX_W = 360;
    private static final int IMG_MAX_H = 240;

    public SearchResults(String query) {
        setTitle("Hledání: \"" + query + "\"");
        setSize(620, 600);
        setLocationRelativeTo(null);
        setIconImage(Icons.calendarIcon(64));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Style.decorateFrame(this);
        setLayout(new BorderLayout());

        List<Entry> hits = FileStorage.search(query);

        // hlavička
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Style.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Výsledky pro \"" + query + "\"");
        title.setFont(Style.FONT_BIG);
        title.setForeground(Color.WHITE);
        JLabel cnt = new JLabel(hits.size() + " " + plural(hits.size()));
        cnt.setFont(Style.FONT_REG);
        cnt.setForeground(new Color(255, 255, 255, 220));
        header.add(title, BorderLayout.WEST);
        header.add(cnt,   BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // obsah
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Style.BG);
        container.setBorder(new EmptyBorder(18, 20, 18, 20));

        if (hits.isEmpty()) {
            JLabel empty = new JLabel("Nic se nenašlo.", SwingConstants.CENTER);
            empty.setFont(Style.FONT_MEDIUM);
            empty.setForeground(Style.TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(80, 0, 0, 0));
            container.add(empty);
        } else {
            for (Entry e : hits) {
                container.add(card(e));
                container.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Style.BG);
        add(scroll, BorderLayout.CENTER);

        // patička
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        footer.setBackground(Style.BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Style.BORDER));
        JButton close = Style.secondaryButton("Zavřít");
        close.addActionListener(e -> dispose());
        footer.add(close);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel card(Entry e) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Style.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel t = new JLabel(e.getTitle());
        t.setFont(Style.FONT_BIG);
        t.setForeground(Style.TEXT);
        JLabel d = new JLabel(DateUtils.formatDay(e.getDateTime().toLocalDate())
                + " • " + DateUtils.formatTime(e.getDateTime()));
        d.setFont(Style.FONT_SMALL);
        d.setForeground(Style.ACCENT);
        head.add(t, BorderLayout.WEST);
        head.add(d, BorderLayout.EAST);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(head);
        card.add(Box.createVerticalStrut(6));

        if (e.getContent() != null && !e.getContent().isEmpty()) {
            JTextArea ta = new JTextArea(e.getContent());
            ta.setFont(Style.FONT_REG);
            ta.setForeground(Style.TEXT);
            ta.setBackground(Style.SURFACE);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setBorder(null);
            ta.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(ta);
        }

        if (e.hasImage()) {
            try {
                BufferedImage img = ImageIO.read(new File(e.getImagePath()));
                if (img != null) {
                    Image scaled = EntryWindow.scaleToFit(img, IMG_MAX_W, IMG_MAX_H);
                    JLabel ic = new JLabel(new ImageIcon(scaled));
                    ic.setBorder(new EmptyBorder(10, 0, 0, 0));
                    ic.setAlignmentX(Component.LEFT_ALIGNMENT);
                    card.add(ic);
                }
            } catch (Exception ignored) {}
        }

        return card;
    }

    private String plural(int n) {
        if (n == 1) return "výsledek";
        if (n >= 2 && n <= 4) return "výsledky";
        return "výsledků";
    }
}
