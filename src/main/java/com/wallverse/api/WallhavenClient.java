package com.wallverse.api;

import com.wallverse.model.Wallpaper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallhavenClient {
    private static final String API_KEY = resolveApiKey();
    private static final String BASE_URL = "https://wallhaven.cc/api/v1/search";
    private static final String USER_AGENT = "WallVerse/1.0 (JavaFX)";
    private final HttpClient client = HttpClient.newHttpClient();
    
    // Cache for wallpaper details to avoid repeated API calls
    private final Map<String, Wallpaper> cache = new HashMap<>();

    public List<Wallpaper> getTrending() throws IOException, InterruptedException {
        String url = BASE_URL + "?sorting=toplist&topRange=1M&purity=100";
        return fetch(url);
    }

    public List<Wallpaper> search(String keyword, String category) throws IOException, InterruptedException {
        // If keyword is null, make it an empty string
        if (keyword == null) {
            keyword = "";
        }
        
        String cleaned = keyword.trim();
        
        // If no keyword and category is "All", just show trending
        if (cleaned.equals("") && (category == null || category.equals("All"))) {
            return getTrending();
        }

        // Add category to the search if it's not "All"
        if (category != null && category.equals("All") == false) {
            cleaned = cleaned + " " + category;
        }

        String query = URLEncoder.encode(cleaned, "UTF-8");
        String url = BASE_URL + "?q=" + query + "&purity=100&sorting=relevance";
        return fetch(url);
    }

    private List<Wallpaper> fetch(String url) throws IOException, InterruptedException {
        if (API_KEY != null && !API_KEY.isEmpty()) {
            url += "&apikey=" + API_KEY;
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", USER_AGENT)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Wallhaven API error: " + response.statusCode());
        }
        return parseResponse(response.body());
    }

    private List<Wallpaper> parseResponse(String json) {
        List<Wallpaper> results = new ArrayList<>();
        JSONObject root = new JSONObject(json);
        
        // Check if "data" exists in the JSON response
        if (root.has("data") == false) {
            return results;
        }
        
        JSONArray data = root.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            Wallpaper w = parseSingleWallpaper(item);
            results.add(w);
        }
        return results;
    }

    /**
     * Fetches full metadata for a single wallpaper.
     */
    public Wallpaper fetchWallpaperDetailsById(String id) throws IOException, InterruptedException {
        // Return from cache if we already have it
        if (cache.containsKey(id)) {
            Wallpaper cached = cache.get(id);
            // If it has detailed tags, it's the full version
            if (cached.getDetailedTags() != null && !cached.getDetailedTags().isEmpty()) {
                return cached;
            }
        }

        String url = "https://wallhaven.cc/api/v1/w/" + id;
        if (API_KEY != null && !API_KEY.isEmpty()) {
            url += "?apikey=" + API_KEY;
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", USER_AGENT)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Wallhaven API error: " + response.statusCode());
        }

        JSONObject root = new JSONObject(response.body());
        JSONObject data = root.getJSONObject("data");
        
        Wallpaper w = parseSingleWallpaper(data);
        cache.put(id, w);
        return w;
    }

    private Wallpaper parseSingleWallpaper(JSONObject item) {
        String id = item.getString("id");
        String fullUrl = item.getString("path");
        String thumbUrl = item.getJSONObject("thumbs").getString("small");
        String category = item.getString("category");

        Wallpaper w = new Wallpaper(id, fullUrl, thumbUrl, category, "");
        
        // Basic fields that might be in search results or single view
        if (item.has("resolution")) w.setResolution(item.getString("resolution"));
        if (item.has("file_size")) w.setFileSize(item.getLong("file_size"));
        if (item.has("file_type")) w.setFileType(item.getString("file_type"));
        if (item.has("views")) w.setViews(item.getInt("views"));
        if (item.has("favorites")) w.setFavorites(item.getInt("favorites"));
        if (item.has("purity")) w.setPurity(item.getString("purity"));
        if (item.has("ratio")) w.setRatio(item.getString("ratio"));
        if (item.has("created_at")) w.setCreatedAt(item.getString("created_at"));
        
        if (item.has("uploader")) {
            w.setUploader(item.getJSONObject("uploader").getString("username"));
        }

        if (item.has("colors")) {
            JSONArray colorsArr = item.getJSONArray("colors");
            for (int i = 0; i < colorsArr.length(); i++) {
                w.getColors().add(colorsArr.getString(i));
            }
        }

        if (item.has("tags")) {
            JSONArray tagsArr = item.getJSONArray("tags");
            List<Wallpaper.Tag> detailedTags = new ArrayList<>();
            for (int i = 0; i < tagsArr.length(); i++) {
                JSONObject tagObj = tagsArr.getJSONObject(i);
                detailedTags.add(new Wallpaper.Tag(
                    tagObj.getInt("id"),
                    tagObj.getString("name"),
                    tagObj.getString("category")
                ));
            }
            w.setDetailedTags(detailedTags);
            
            // Also update the legacy string tags field
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < detailedTags.size(); i++) {
                sb.append(detailedTags.get(i).getName());
                if (i < detailedTags.size() - 1) sb.append(", ");
            }
            w.setTags(sb.toString());
        }
        
        return w;
    }

    private static String resolveApiKey() {
        // For beginners: Just returning an empty string. 
        // You can put your actual API key here if you have one!
        return "";
    }
}
