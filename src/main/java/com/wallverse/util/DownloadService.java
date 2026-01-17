package com.wallverse.util;

import com.wallverse.model.Wallpaper;
import javafx.concurrent.Task;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class DownloadService {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public static Task<Path> downloadWallpaper(Wallpaper w) {
        return new Task<>() {
            @Override
            protected Path call() throws Exception {
                updateMessage("Preparing download...");
                updateProgress(0, 100);

                String downloads = System.getProperty("user.home") + "/Downloads";
                Path targetDir = Paths.get(downloads);
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                String url = w.getUrl(); // Use path/url field which should be the direct link
                String ext = w.getFileType() != null ? w.getFileType() : "jpg";
                String fileName = "wallhaven-" + w.getId() + "." + ext;
                Path targetPath = targetDir.resolve(fileName);

                HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "*/*")
                    .GET()
                    .build();

                updateMessage("Connecting to Wallhaven...");
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                if (response.statusCode() != 200) {
                    throw new Exception("HTTP Error: " + response.statusCode());
                }

                long totalBytes = Long.parseLong(response.headers().firstValue("Content-Length").orElse("-1"));
                
                try (InputStream is = response.body()) {
                    if (totalBytes == -1) {
                        // Unknown size, just copy
                        updateMessage("Downloading (unknown size)...");
                        Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        // Track progress
                        updateMessage("Downloading...");
                        byte[] buffer = new byte[8192];
                        long downloaded = 0;
                        int read;
                        
                        // We write to a temporary file first
                        Path tempFile = Files.createTempFile("wallverse-", ".tmp");
                        try (java.io.OutputStream os = Files.newOutputStream(tempFile)) {
                            while ((read = is.read(buffer)) != -1) {
                                os.write(buffer, 0, read);
                                downloaded += read;
                                updateProgress(downloaded, totalBytes);
                            }
                        }
                        Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                updateMessage("Download complete!");
                updateProgress(100, 100);
                return targetPath;
            }
        };
    }
}
