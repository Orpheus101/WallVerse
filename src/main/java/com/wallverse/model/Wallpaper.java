package com.wallverse.model;

import java.util.ArrayList;
import java.util.List;

public class Wallpaper {
    private String id;
    private String url;
    private String thumbUrl;
    private String category;
    private String tags; // legacy simple tags string

    // Detailed metadata fields
    private String resolution = "Unknown";
    private long fileSize = 0;
    private String fileType = "jpg";
    private int views = 0;
    private int favorites = 0;
    private String uploader = "Unknown";
    private String purity = "sfw";
    private String ratio = "16:9";
    private String createdAt = "";
    private List<String> colors = new ArrayList<>();
    private List<Tag> detailedTags = new ArrayList<>();

    public Wallpaper() {
    }

    public Wallpaper(String id, String url, String thumbUrl, String category, String tags) {
        this.id = id;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.category = category;
        this.tags = tags;
    }

    // Helper to get a Pinterest-style title
    public String getDisplayTitle() {
        if (detailedTags != null && !detailedTags.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int limit = Math.min(detailedTags.size(), 3);
            for (int i = 0; i < limit; i++) {
                sb.append(detailedTags.get(i).getName());
                if (i < limit - 1) sb.append(", ");
            }
            if (resolution != null && !resolution.equals("Unknown")) {
                sb.append(" • ").append(resolution);
            }
            return sb.toString();
        }
        return category + " Wallpaper • " + resolution;
    }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getThumbUrl() { return thumbUrl; }
    public void setThumbUrl(String thumbUrl) { this.thumbUrl = thumbUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getFavorites() { return favorites; }
    public void setFavorites(int favorites) { this.favorites = favorites; }

    public String getUploader() { return uploader; }
    public void setUploader(String uploader) { this.uploader = uploader; }

    public String getPurity() { return purity; }
    public void setPurity(String purity) { this.purity = purity; }

    public String getRatio() { return ratio; }
    public void setRatio(String ratio) { this.ratio = ratio; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public List<Tag> getDetailedTags() { return detailedTags; }
    public void setDetailedTags(List<Tag> detailedTags) { this.detailedTags = detailedTags; }

    @Override
    public String toString() {
        return "Wallpaper{id='" + id + "', title='" + getDisplayTitle() + "'}";
    }

    // Inner Tag class for better mapping
    public static class Tag {
        private int id;
        private String name;
        private String category;

        public Tag(int id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
    }
}

