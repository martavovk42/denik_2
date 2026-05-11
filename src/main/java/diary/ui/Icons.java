package diary.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;

/**
 * Programově vykreslené ikony - není třeba žádný PNG soubor.
 */
public class Icons {

    /** Hlavní ikona aplikace - stylizovaný list kalendáře. */
    public static Image calendarIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int pad = Math.max(2, size / 16);
        int x = pad;
        int y = pad + size / 10;
        int w = size - 2 * pad;
        int h = size - 2 * pad - size / 10;

        // bílé tělo
        g.setColor(Style.SURFACE);
        g.fillRoundRect(x, y, w, h, size / 6, size / 6);

        // horní zelený pruh
        g.setColor(Style.PRIMARY);
        g.fillRoundRect(x, y, w, h / 3, size / 6, size / 6);
        g.fillRect(x, y + h / 6, w, h / 6);

        // dvě spirálky nahoře
        g.setColor(Style.PRIMARY_DARK);
        int ringW = Math.max(2, size / 14);
        int ringH = Math.max(4, size / 6);
        g.fillRoundRect(x + w / 4 - ringW / 2, pad / 2, ringW, ringH, ringW, ringW);
        g.fillRoundRect(x + 3 * w / 4 - ringW / 2, pad / 2, ringW, ringH, ringW, ringW);

        // dnešní datum velkými písmeny uvnitř ikony
        String num = String.valueOf(LocalDate.now().getDayOfMonth());
        int fontSize = (int) (h * 0.55);
        g.setFont(new Font(Style.FONT_NAME, Font.BOLD, fontSize));
        g.setColor(Style.ACCENT);
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(num)) / 2;
        int ty = y + h / 3 + (h * 2 / 3 + fm.getAscent()) / 2 - fm.getDescent() / 2;
        g.drawString(num, tx, ty);

        // jemný rámeček
        g.setColor(Style.BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w - 1, h - 1, size / 6, size / 6);

        g.dispose();
        return img;
    }

    /** Malá ikona fotoaparátu - používá se k označení dnů s obrázky. */
    public static Image cameraIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = Math.max(1, size / 10);
        // tělo
        g.setColor(Style.ACCENT);
        g.fillRoundRect(pad, pad + size / 6, size - 2 * pad, size - 2 * pad - size / 6,
                Math.max(2, size / 6), Math.max(2, size / 6));
        // hledáček
        int finderW = size / 3;
        g.fillRect((size - finderW) / 2, pad, finderW, size / 4);
        // objektiv
        g.setColor(Style.SURFACE);
        int lens = size / 3;
        g.fillOval((size - lens) / 2, (size - lens) / 2 + size / 12, lens, lens);
        g.setColor(Style.ACCENT);
        int innerLens = lens - Math.max(2, size / 8);
        g.fillOval((size - innerLens) / 2, (size - innerLens) / 2 + size / 12,
                innerLens, innerLens);

        g.dispose();
        return img;
    }
}
