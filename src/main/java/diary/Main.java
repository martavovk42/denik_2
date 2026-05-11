package diary;

import diary.storage.FileStorage;
import diary.ui.CalendarWindow;
import diary.ui.NameWindow;
import diary.ui.WelcomeWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // české vykreslení čar - kvůli emoji a diakritice ve fontech
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(Main::start);
    }

    private static void start() {
        String name = FileStorage.loadUserName();

        if (name == null || name.isEmpty()) {
            // první spuštění - zeptáme se na jméno
            new NameWindow(saved -> new WelcomeWindow(saved, Main::openCalendar));
        } else {
            // další spuštění - krátká uvítací obrazovka, pak kalendář
            new WelcomeWindow(name, Main::openCalendar);
        }
    }

    private static void openCalendar() {
        new CalendarWindow().setVisible(true);
    }
}
