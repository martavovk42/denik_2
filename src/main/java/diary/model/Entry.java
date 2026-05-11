package diary.model;

import java.time.LocalDateTime;

public class Entry {

    private String id;            // jednoznačné jméno souboru bez přípony
    private String title;
    private String content;
    private LocalDateTime dateTime;
    private String imagePath;

    public Entry(String title, String content, LocalDateTime dateTime, String imagePath) {
        this(null, title, content, dateTime, imagePath);
    }

    public Entry(String id, String title, String content, LocalDateTime dateTime, String imagePath) {
        this.id = id;
        this.title = title;
        this.content = content;

        // ochrana proti null
        this.dateTime = (dateTime == null) ? LocalDateTime.now() : dateTime;
        this.imagePath = imagePath;
    }

    public String getId() { return id; }
    public void   setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getImagePath() { return imagePath; }

    public boolean hasImage() {
        return imagePath != null && !imagePath.isEmpty();
    }
}
