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
import java.time.LocalDateTime;
import javax.imageio.ImageIO;

/**
 * Okno pro nový nebo upravovaný zápis.
 *
 * - datum je v titulku okna (žádné "Nový zápis – ..." uvnitř)
 * - šipky < / > přepínají na předchozí / následující den (jen v módu "nový")
 * - obrázek se zobrazuje jako náhled (ne jako cesta k souboru)
 * - po Uložit se okno zavře a zavolá se onSaved callback (pro obnovu kalendáře)
 *
 * Pro editaci existujícího zápisu se použije konstruktor přijímající Entry.
 */
public class EntryWindow extends JFrame {

    private static final int PREVIEW_W = 240;
    private static final int PREVIEW_H = 180;

    private final LocalDate day;
    private final Runnable  onSaved;     // může být null
    private final Entry     editing;     // null = nový zápis

    private final JTextField titleField = Style.inputField();
    private final JTextArea  textArea   = Style.textArea();
    private final JLabel     preview    = new JLabel();
    private String           selectedImage;          // aktuálně vybraný obrázek

    /** Konstruktor pro nový zápis. */
    public EntryWindow(LocalDate day, Runnable onSaved) {
        this(day, null, onSaved);
    }

    /** Konstruktor pro editaci existujícího zápisu. */
    public EntryWindow(Entry editing, Runnable onSaved) {
        this(editing.getDateTime().toLocalDate(), editing, onSaved);
    }

    private EntryWindow(LocalDate day, Entry editing, Runnable onSaved) {
        this.day     = (day != null) ? day : LocalDate.now();
        this.editing = editing;
        this.onSaved = onSaved;

        boolean isEdit = editing != null;
        setTitle((isEdit ? "Úprava zápisu – " : "Zápis – ") + DateUtils.formatDay(this.day));
        setSize(820, 560);
        setLocationRelativeTo(null);
        setIconImage(Icons.calendarIcon(64));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Style.decorateFrame(this);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        if (isEdit) populateFromEntry();
        SwingUtilities.invokeLater(titleField::requestFocusInWindow);
    }

    private void populateFromEntry() {
        titleField.setText(editing.getTitle());
        textArea.setText(editing.getContent() == null ? "" : editing.getContent());
        selectedImage = editing.getImagePath();
        renderPreview();
    }

    // ───────────── HEADER s šipkami ─────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Style.PRIMARY);
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel dateLabel = new JLabel(DateUtils.formatDay(day), SwingConstants.CENTER);
        dateLabel.setFont(Style.FONT_BIG);
        dateLabel.setForeground(Color.WHITE);
        header.add(dateLabel, BorderLayout.CENTER);

        // šipky pro přepínání dnů má smysl jen u nového zápisu
        if (editing == null) {
            JButton prev = navButton("◀");
            JButton next = navButton("▶");
            header.add(prev, BorderLayout.WEST);
            header.add(next, BorderLayout.EAST);
            prev.addActionListener(e -> jumpToDay(day.minusDays(1)));
            next.addActionListener(e -> jumpToDay(day.plusDays(1)));
        }
        return header;
    }

    private JButton navButton(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font(Style.FONT_NAME, Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(Style.PRIMARY);
        b.setBorder(new EmptyBorder(6, 14, 6, 14));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void jumpToDay(LocalDate target) {
        dispose();
        new EntryWindow(target, onSaved).setVisible(true);
    }

    // ───────────── CENTER (text + náhled obrázku) ─────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setBackground(Style.BG);
        center.setBorder(new EmptyBorder(18, 20, 18, 20));

        // levá strana: titulek + text
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel("Titulek");
        titleLbl.setFont(Style.FONT_BOLD);
        titleLbl.setForeground(Style.TEXT_MUTED);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel textLbl = new JLabel("Text");
        textLbl.setFont(Style.FONT_BOLD);
        textLbl.setForeground(Style.TEXT_MUTED);
        textLbl.setBorder(new EmptyBorder(12, 0, 4, 0));
        textLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createLineBorder(Style.BORDER));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(titleLbl);
        left.add(Box.createVerticalStrut(4));
        left.add(titleField);
        left.add(textLbl);
        left.add(scroll);

        // pravá strana: náhled obrázku
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(PREVIEW_W + 20, 0));

        JLabel imgLbl = new JLabel("Obrázek");
        imgLbl.setFont(Style.FONT_BOLD);
        imgLbl.setForeground(Style.TEXT_MUTED);

        preview.setHorizontalAlignment(SwingConstants.CENTER);
        preview.setVerticalAlignment(SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(PREVIEW_W, PREVIEW_H));
        preview.setBackground(Style.SURFACE);
        preview.setOpaque(true);
        preview.setForeground(Style.TEXT_MUTED);
        preview.setFont(Style.FONT_SMALL);
        preview.setBorder(BorderFactory.createDashedBorder(Style.BORDER, 1.5f, 4f, 4f, true));
        renderPreview();

        JPanel imgBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        imgBtns.setOpaque(false);
        JButton pick   = Style.secondaryButton("Vybrat…");
        JButton remove = Style.secondaryButton("Odebrat");
        imgBtns.add(pick);
        imgBtns.add(remove);

        pick.addActionListener(e -> chooseImage());
        remove.addActionListener(e -> {
            selectedImage = null;
            renderPreview();
        });

        right.add(imgLbl,  BorderLayout.NORTH);
        right.add(preview, BorderLayout.CENTER);
        right.add(imgBtns, BorderLayout.SOUTH);

        center.add(left,  BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);
        return center;
    }

    private void chooseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Obrázky (PNG, JPG, GIF)", "png", "jpg", "jpeg", "gif", "bmp"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImage = fc.getSelectedFile().getAbsolutePath();
            renderPreview();
        }
    }

    private void renderPreview() {
        preview.setIcon(null);
        if (selectedImage == null) {
            preview.setText("(žádný obrázek)");
            return;
        }
        try {
            BufferedImage img = ImageIO.read(new File(selectedImage));
            if (img == null) {
                preview.setText("Nelze načíst obrázek");
                return;
            }
            preview.setText("");
            preview.setIcon(new ImageIcon(scaleToFit(img, PREVIEW_W - 8, PREVIEW_H - 8)));
        } catch (Exception ex) {
            preview.setText("Nelze načíst obrázek");
        }
    }

    /** Zmenší obrázek tak, aby se vešel do daných rozměrů a zachoval poměr stran. */
    static Image scaleToFit(BufferedImage src, int maxW, int maxH) {
        double scale = Math.min((double) maxW / src.getWidth(),
                                (double) maxH / src.getHeight());
        int w = (int) Math.round(src.getWidth() * scale);
        int h = (int) Math.round(src.getHeight() * scale);
        return src.getScaledInstance(Math.max(1, w), Math.max(1, h), Image.SCALE_SMOOTH);
    }

    // ───────────── FOOTER s tlačítky ─────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        footer.setBackground(Style.BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Style.BORDER));

        JButton cancel = Style.secondaryButton("Zavřít");
        JButton save   = Style.primaryButton(editing == null ? "Uložit zápis" : "Uložit změny");

        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> doSave());

        footer.add(cancel);
        footer.add(save);
        return footer;
    }

    private void doSave() {
        String t = titleField.getText() == null ? "" : titleField.getText().trim();
        if (t.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Zápis musí mít titulek.", "Chybí titulek",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Entry entry;
        if (editing != null) {
            // úprava: zachováme původní ID a čas, jen prohodíme texty a obrázek
            entry = new Entry(editing.getId(), t, textArea.getText(),
                              editing.getDateTime(), selectedImage);
        } else {
            // nový: dnes získá aktuální čas, jinak začátek vybraného dne
            LocalDateTime dt = day.equals(LocalDate.now())
                    ? LocalDateTime.now()
                    : day.atTime(LocalDateTime.now().toLocalTime());
            entry = new Entry(t, textArea.getText(), dt, selectedImage);
        }

        FileStorage.saveEntry(entry);

        if (onSaved != null) onSaved.run();
        dispose();
    }
}
