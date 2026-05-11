package diary.storage;

import diary.model.Entry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Ukládání zápisů na disk.
 *
 * Každý zápis je soubor s názvem  entry_<časváhodot>.txt  uvnitř složky
 * data/YYYY-MM-DD/.  Volitelný obrázek se ukládá pod stejným základem
 * s příponou .png. Díky tomu může být v jednom dni libovolný počet zápisů
 * a žádné se nepřepisují podle stejného titulku.
 *
 * Formát txt souboru (UTF-8):
 *   řádek 1:  titulek
 *   řádek 2:  čas (ISO LocalDateTime)
 *   řádek 3+: text zápisu (i víceřádkový)
 */
public class FileStorage {

    private static final String BASE      = "data";
    private static final String USER_FILE = BASE + "/user.txt";

    // ───────────── cesty ─────────────
    private static Path getDir(LocalDate date) {
        return Paths.get(BASE, date.toString());
    }

    private static String makeId() {
        return "entry_" + System.currentTimeMillis();
    }

    // ───────────── ukládání ─────────────
    public static void saveEntry(Entry entry) {
        try {
            Path dir = getDir(entry.getDateTime().toLocalDate());
            Files.createDirectories(dir);

            if (entry.getId() == null) {
                entry.setId(makeId());
            }
            String base = entry.getId();

            Path txt = dir.resolve(base + ".txt");
            try (BufferedWriter w = Files.newBufferedWriter(txt, StandardCharsets.UTF_8)) {
                w.write(safeTitle(entry.getTitle()));
                w.newLine();
                w.write(entry.getDateTime().toString());
                w.newLine();
                w.write(entry.getContent() == null ? "" : entry.getContent());
            }

            // OBRÁZEK
            Path target = dir.resolve(base + ".png");
            if (entry.getImagePath() != null) {
                File img = new File(entry.getImagePath());
                if (img.exists()) {
                    // pokud uživatel vybral už existující obrázek ve stejné cestě, nic neděláme
                    if (!img.toPath().toAbsolutePath().equals(target.toAbsolutePath())) {
                        Files.copy(img.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } else {
                // při úpravě byl obrázek odebrán - smažeme starý
                Files.deleteIfExists(target);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String safeTitle(String t) {
        return t == null ? "" : t.replace("\n", " ").replace("\r", " ");
    }

    // ───────────── načítání ─────────────
    public static List<Entry> loadEntries() {
        List<Entry> list = new ArrayList<>();
        Path base = Paths.get(BASE);
        if (!Files.exists(base)) return list;

        try (Stream<Path> walk = Files.walk(base)) {
            walk.filter(p -> p.toString().endsWith(".txt"))
                .filter(p -> !p.getFileName().toString().equals("user.txt"))
                .forEach(p -> {
                    Entry e = readEntry(p);
                    if (e != null) list.add(e);
                });
        } catch (IOException e) {
            e.printStackTrace();
        }

        list.sort(Comparator.comparing(Entry::getDateTime));
        return list;
    }

    private static Entry readEntry(Path p) {
        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            if (lines.size() < 2) return null;

            String title = lines.get(0);
            String timeStr = lines.get(1);
            String content = (lines.size() > 2)
                    ? String.join("\n", lines.subList(2, lines.size()))
                    : "";

            LocalDateTime dt;
            try {
                dt = LocalDateTime.parse(timeStr);
            } catch (Exception ex) {
                dt = LocalDateTime.now();
            }

            String fileName = p.getFileName().toString();
            String id = fileName.substring(0, fileName.length() - 4); // odeber .txt

            String imgPath = p.toString().substring(0, p.toString().length() - 4) + ".png";
            File img = new File(imgPath);

            return new Entry(id, title, content, dt, img.exists() ? imgPath : null);
        } catch (IOException e) {
            return null;
        }
    }

    /** Zápisy pro konkrétní den - oseřazené podle času vzestupně. */
    public static List<Entry> loadEntriesForDay(LocalDate day) {
        List<Entry> all = loadEntries();
        List<Entry> out = new ArrayList<>();
        for (Entry e : all) {
            if (e.getDateTime().toLocalDate().equals(day)) {
                out.add(e);
            }
        }
        return out;
    }

    /** Hledá slovo v titulku i v obsahu (case-insensitive). */
    public static List<Entry> search(String query) {
        if (query == null) return List.of();
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();

        List<Entry> out = new ArrayList<>();
        for (Entry e : loadEntries()) {
            String inTitle   = e.getTitle()   == null ? "" : e.getTitle().toLowerCase();
            String inContent = e.getContent() == null ? "" : e.getContent().toLowerCase();
            if (inTitle.contains(q) || inContent.contains(q)) {
                out.add(e);
            }
        }
        return out;
    }

    // ───────────── mazání ─────────────
    public static void deleteEntry(Entry entry) {
        if (entry == null || entry.getId() == null) return;
        Path dir = getDir(entry.getDateTime().toLocalDate());
        try {
            Files.deleteIfExists(dir.resolve(entry.getId() + ".txt"));
            Files.deleteIfExists(dir.resolve(entry.getId() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ───────────── jméno uživatele ─────────────
    public static String loadUserName() {
        try {
            Path path = Paths.get(USER_FILE);
            if (!Files.exists(path)) return null;
            String s = Files.readString(path, StandardCharsets.UTF_8).trim();
            return s.isEmpty() ? null : s;
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveUserName(String name) {
        try {
            Files.createDirectories(Paths.get(BASE));
            Files.writeString(Paths.get(USER_FILE), name == null ? "" : name.trim(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
