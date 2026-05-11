package diary.ui;

import diary.storage.FileStorage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * První spuštění - zeptáme se na jméno uživatele.
 * Stylizováno stejně jako ostatní okna.
 */
public class NameWindow extends JFrame {

    public NameWindow(Consumer<String> onDone) {
        setTitle("Vítej v deníku");
        setSize(440, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Icons.calendarIcon(64));
        Style.decorateFrame(this);
        setLayout(new BorderLayout());

        // 🔝 hlavička
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Style.PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel title = new JLabel("Ahoj!");
        title.setFont(Style.FONT_HUGE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // 🔘 střed - text a vstupní pole
        JPanel center = new JPanel();
        center.setBackground(Style.BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel intro = new JLabel("Jaké oslovení si přeješ?");
        intro.setFont(Style.FONT_MEDIUM);
        intro.setForeground(Style.TEXT);
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Tvé jméno se zobrazí při příštím přihlášení.");
        hint.setFont(Style.FONT_SMALL);
        hint.setForeground(Style.TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = Style.inputField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(intro);
        center.add(Box.createVerticalStrut(6));
        center.add(hint);
        center.add(Box.createVerticalStrut(14));
        center.add(field);

        add(center, BorderLayout.CENTER);

        // 🔽 spodní panel s tlačítkem
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(Style.BG);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Style.BORDER));

        JButton ok = Style.primaryButton("Pokračovat");
        bottom.add(ok);
        add(bottom, BorderLayout.SOUTH);

        Runnable submit = () -> {
            String name = field.getText() == null ? "" : field.getText().trim();
            if (name.isEmpty()) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Style.DANGER, 1, true),
                        new EmptyBorder(6, 10, 6, 10)
                ));
                return;
            }
            FileStorage.saveUserName(name);
            dispose();
            onDone.accept(name);
        };

        ok.addActionListener(e -> submit.run());
        field.addActionListener(e -> submit.run());

        SwingUtilities.invokeLater(field::requestFocusInWindow);
        setVisible(true);
    }
}
