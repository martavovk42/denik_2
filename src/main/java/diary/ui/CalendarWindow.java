package diary.ui;

import diary.model.Entry;
import diary.storage.FileStorage;
import diary.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.List;

import static diary.ui.Theme.theme;

/**
 * Hlavní okno aplikace - měsíční kalendář se zápisy.
 *
 * - vlevo nahoře vyhledávání slov v zápisech
 * - klikem na měsíc/rok lze přeskočit na jiný měsíc
 * - tlačítko "Přidej zápis" otevře zápis pro dnešek
 * - klikem na den se otevře okno se všemi zápisy pro daný den
 * - dny s zápisy jsou obarvené, dny s fotkami mají ikonku
 */
public class CalendarWindow extends JFrame {

    private LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

    public CalendarWindow() {
        setTitle("Můj deník");
        setSize(820, 640);
        setLocationRelativeTo(null);
        setIconImage(Icons.calendarIcon(64));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Theme.decorateFrame(this);
        setLayout(new BorderLayout());
        render();
    }

    // render
    private void render() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        add(buildTop(),  BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // horní panel
    private JPanel buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(theme.PRIMARY());
        top.setBorder(new EmptyBorder(12, 16, 12, 16));

        // LEVÁ STRANA: vyhledávání a menu
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchPanel.setOpaque(false);


        JMenuBar menuBar = theme.menuBar();
        JMenu nastaveni = theme.menu("☰");
        JMenuItem themeitem = theme.menuItem("Téma");
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        menuBar.add(nastaveni);
        nastaveni.add(themeitem);

        JTextField searchField = new JTextField(14);
        searchField.setFont(Theme.FONT_REG);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.PRIMARY_DARK(), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = theme.accentButton("Hledat");
        searchBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Runnable doSearch = () -> {
            String q = searchField.getText() == null ? "" : searchField.getText().trim();
            if (!q.isEmpty()) {
                new SearchResults(q).setVisible(true);
            }
        };
        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());

        searchPanel.add(menuBar);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        top.add(searchPanel, BorderLayout.WEST);

        //STŘED: měsíc/rok s šipkami
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        monthPanel.setOpaque(false);

        JButton prev = navArrow("◀");
        JButton next = navArrow("▶");
        JLabel  label = new JLabel(
                DateUtils.mesic(currentMonth.getMonthValue()) + " " + currentMonth.getYear(),
                SwingConstants.CENTER);
        label.setFont(Theme.FONT_BIG);
        label.setForeground(Color.WHITE);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setBorder(new EmptyBorder(4, 14, 4, 14));
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { pickMonthYear(); }
        });

        prev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); render(); });
        next.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); render(); });

        monthPanel.add(prev);
        monthPanel.add(label);
        monthPanel.add(next);
        top.add(monthPanel, BorderLayout.CENTER);

        // PRAVÁ STRANA: Přidej zápis
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);

        JButton add = theme.accentButton("Přidej zápis");
        add.setBorder(new EmptyBorder(6, 14, 6, 14));
        add.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add.addActionListener(e ->
                new EntryWindow(LocalDate.now(), this::render).setVisible(true));
        right.add(add);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private JButton navArrow(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font(Theme.FONT_NAME, Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(theme.PRIMARY());
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(4, 10, 4, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    //  výběr měsíce/roku
    private void pickMonthYear() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) months[i] = DateUtils.mesic(i + 1);
        JComboBox<String> monthCb = new JComboBox<>(months);
        monthCb.setSelectedIndex(currentMonth.getMonthValue() - 1);
        monthCb.setFont(Theme.FONT_REG);

        int yearNow = Year.now().getValue();
        List<Integer> years = new ArrayList<>();
        for (int y = yearNow - 10; y <= yearNow + 10; y++) years.add(y);
        JComboBox<Integer> yearCb = new JComboBox<>(years.toArray(new Integer[0]));
        yearCb.setSelectedItem(currentMonth.getYear());
        yearCb.setFont(Theme.FONT_REG);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBackground(theme.BG());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Měsíc:"));
        panel.add(monthCb);
        panel.add(new JLabel("Rok:"));
        panel.add(yearCb);

        int res = JOptionPane.showConfirmDialog(this, panel, "Vyber měsíc a rok",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int m = monthCb.getSelectedIndex() + 1;
            int y = (Integer) yearCb.getSelectedItem();
            currentMonth = LocalDate.of(y, m, 1);
            render();
        }
    }

    // mřížka dnů
    private JPanel buildGrid() {
        Map<LocalDate, List<Entry>> byDay = entriesGroupedByDay();

        JPanel grid = new JPanel(new GridLayout(0, 7, 8, 8));
        grid.setBorder(new EmptyBorder(14, 14, 14, 14));
        grid.setBackground(theme.BG());

        String[] days = {"Po", "Út", "St", "Čt", "Pá", "So", "Ne"};
        for (String d : days) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(Theme.FONT_BOLD);
            l.setForeground(theme.TEXT_MUTED());
            grid.add(l);
        }

        LocalDate first = currentMonth.withDayOfMonth(1);
        int offset = first.getDayOfWeek().getValue();
        for (int i = 1; i < offset; i++) grid.add(emptyCell());

        int total = currentMonth.lengthOfMonth();
        for (int i = 1; i <= total; i++) {
            LocalDate date = currentMonth.withDayOfMonth(i);
            List<Entry> dayEntries = byDay.getOrDefault(date, Collections.emptyList());
            grid.add(buildDayCell(date, dayEntries));
        }
        // doplnění do plné mřížky pro hezký vzhled
        int used = offset - 1 + total;
        int pad  = (7 - used % 7) % 7;
        for (int i = 0; i < pad; i++) grid.add(emptyCell());

        return grid;
    }

    private JPanel emptyCell() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }

    private Map<LocalDate, List<Entry>> entriesGroupedByDay() {
        Map<LocalDate, List<Entry>> map = new HashMap<>();
        for (Entry e : FileStorage.loadEntries()) {
            map.computeIfAbsent(e.getDateTime().toLocalDate(),
                                k -> new ArrayList<>()).add(e);
        }
        return map;
    }

    private DayCell buildDayCell(LocalDate date, List<Entry> entries) {
        int count = entries.size();
        boolean hasPhotos = entries.stream().anyMatch(Entry::hasImage);
        boolean isToday = date.equals(LocalDate.now());

        DayCell cell = new DayCell(date.getDayOfMonth(), count, hasPhotos, isToday);
        cell.addActionListener(e ->
                new DenniZapisy(date, this::render).setVisible(true));
        return cell;
    }

    // vlastní tlačítko dne
    private static class DayCell extends JButton {
        private final boolean hasPhotos;

        DayCell(int day, int count, boolean hasPhotos, boolean isToday) {
            super(String.valueOf(day));
            this.hasPhotos = hasPhotos;

            Color bg = null;
            Color fg = theme.TEXT();
            if (count == 0) {
                bg = theme.SURFACE();
            } else {
                bg = theme.COUNT_COLORS(count, bg);
                fg = theme.ACCESIBILITY(count, fg);
            }

            setBackground(bg);
            setForeground(fg);
            setFont(new Font(Theme.FONT_NAME, isToday ? Font.BOLD : Font.PLAIN, 16));
            setFocusPainted(false);
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.TOP);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            isToday ? theme.ACCENT() : theme.BORDER(),
                            isToday ? 2 : 1, true),
                    new EmptyBorder(6, 8, 4, 8)
            ));
            setPreferredSize(new Dimension(80, 60));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            final Color baseBg = bg;
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    setBackground(blend(baseBg, theme.ACCENT_SOFT(), 0.35f));
                }
                @Override public void mouseExited(MouseEvent e) {
                    setBackground(baseBg);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hasPhotos) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int s = 14;
                int x = getWidth() - s - 6;
                int y = getHeight() - s - 6;
                g2.drawImage(Icons.cameraIcon(s), x, y, null);
                g2.dispose();
            }
        }

        private static Color blend(Color a, Color b, float t) {
            int r = (int) (a.getRed()   * (1 - t) + b.getRed()   * t);
            int gg = (int) (a.getGreen() * (1 - t) + b.getGreen() * t);
            int bb = (int) (a.getBlue()  * (1 - t) + b.getBlue()  * t);
            return new Color(Math.max(0, Math.min(255, r)),
                             Math.max(0, Math.min(255, gg)),
                             Math.max(0, Math.min(255, bb)));
        }
    }
}
