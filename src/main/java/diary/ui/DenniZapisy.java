package diary.ui;

import diary.model.Entry;
import diary.storage.FileStorage;
import diary.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Okno se všemi zápisy pro konkrétní den.
 * Otevírá se po kliknutí na den v kalendáři.
 *
 * - hezky vystylované karty
 * - obrázky zobrazené jako náhledy
 * - zápisy seřazené od nejstaršího k nejnovějšímu
 * - tlačítko "Přidej zápis" otevře EntryWindow pro tento den
 */
public class DenniZapisy extends JFrame {

    private static final int IMG_MAX_W = 480;
    private static final int IMG_MAX_H = 320;

    private final LocalDate day;
    private final Runnable onChanged;     // pro obnovu kalendáře

    public DenniZapisy(LocalDate day, Runnable onChanged) {
        this.day = day;
        this.onChanged = onChanged;

        setTitle("Zápisy – " + DateUtils.formatDay(day));
        setSize(640, 640);
        setLocationRelativeTo(null);
        setIconImage(Icons.calendarIcon(64));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Style.decorateFrame(this);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildList(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ───────────── HEADER ─────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Style.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel(DateUtils.formatDay(day));
        title.setFont(Style.FONT_BIG);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ───────────── seznam zápisů ─────────────
    private JScrollPane buildList() {
        List<Entry> entries = FileStorage.loadEntriesForDay(day);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Style.BG);
        container.setBorder(new EmptyBorder(18, 20, 18, 20));

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("Žádné zápisy pro tento den.", SwingConstants.CENTER);
            empty.setFont(Style.FONT_MEDIUM);
            empty.setForeground(Style.TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(80, 0, 0, 0));
            container.add(empty);
        } else {
            for (Entry e : entries) {
                container.add(entryCard(e));
                container.add(Box.createVerticalStrut(14));
            }
        }

        JScrollPane scroll = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Style.BG);
        return scroll;
    }

    private JPanel entryCard(Entry e) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Style.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // hlavička karty: titulek vlevo, čas vpravo
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JLabel titleLbl = new JLabel(e.getTitle());
        titleLbl.setFont(Style.FONT_BIG);
        titleLbl.setForeground(Style.TEXT);

        JLabel timeLbl = new JLabel(DateUtils.formatTime(e.getDateTime()));
        timeLbl.setFont(Style.FONT_BOLD);
        timeLbl.setForeground(Style.ACCENT);

        head.add(titleLbl, BorderLayout.WEST);
        head.add(timeLbl,  BorderLayout.EAST);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(head);
        card.add(Box.createVerticalStrut(8));

        // text
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

        // obrázek inline
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
            } catch (Exception ignored) {
                // pokud se obrázek nepodaří načíst, prostě ho přeskočíme
            }
        }

        // tlačítka úprava / smazání - malá, vpravo dole
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton edit = Style.secondaryButton("Upravit");
        edit.addActionListener(ev ->
                new EntryWindow(e, () -> {
                    if (onChanged != null) onChanged.run();
                    refresh();
                }).setVisible(true)
        );

        JButton del = Style.secondaryButton("Smazat");
        del.setForeground(Style.DANGER);
        del.addActionListener(ev -> {
            int res = JOptionPane.showConfirmDialog(this,
                    "Smazat zápis \"" + e.getTitle() + "\"?",
                    "Smazat zápis", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                FileStorage.deleteEntry(e);
                if (onChanged != null) onChanged.run();
                refresh();
            }
        });

        actions.add(edit);
        actions.add(del);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(actions);

        return card;
    }

    // ───────────── FOOTER ─────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        footer.setBackground(Style.BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Style.BORDER));

        JButton close = Style.secondaryButton("Zavřít");
        JButton add   = Style.accentButton("Přidej zápis");

        close.addActionListener(e -> dispose());
        add.addActionListener(e ->
                new EntryWindow(day, () -> {
                    if (onChanged != null) onChanged.run();
                    refresh();
                }).setVisible(true)
        );

        footer.add(close);
        footer.add(add);
        return footer;
    }

    /** Aktualizuje obsah okna po přidání/smazání zápisu. */
    private void refresh() {
        Container cp = getContentPane();
        cp.removeAll();
        cp.setLayout(new BorderLayout());
        cp.add(buildHeader(), BorderLayout.NORTH);
        cp.add(buildList(),   BorderLayout.CENTER);
        cp.add(buildFooter(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}
