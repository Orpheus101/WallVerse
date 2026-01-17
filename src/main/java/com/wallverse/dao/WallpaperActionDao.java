package com.wallverse.dao;

import com.wallverse.model.Wallpaper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class WallpaperActionDao extends BaseDao {

    /**
     * Upserts a wallpaper record. Updates metadata if it's currently missing.
     */
    private void ensureWallpaper(Wallpaper wallpaper) throws SQLException {
        String sql = "INSERT INTO wallpapers (wallhaven_id, url, thumb_url, category, tags, image_path, resolution, file_type, file_size, uploader, created_at, last_synced_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "url = VALUES(url), " +
                     "thumb_url = VALUES(thumb_url), " +
                     "category = VALUES(category), " +
                     "tags = VALUES(tags), " +
                     "image_path = COALESCE(wallpapers.image_path, VALUES(image_path)), " +
                     "resolution = COALESCE(wallpapers.resolution, VALUES(resolution)), " +
                     "file_type = COALESCE(wallpapers.file_type, VALUES(file_type)), " +
                     "file_size = COALESCE(wallpapers.file_size, VALUES(file_size)), " +
                     "uploader = COALESCE(wallpapers.uploader, VALUES(uploader)), " +
                     "created_at = COALESCE(wallpapers.created_at, VALUES(created_at)), " +
                     "last_synced_at = NOW()";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, wallpaper.getId());
            ps.setString(2, wallpaper.getUrl());
            ps.setString(3, wallpaper.getThumbUrl());
            ps.setString(4, wallpaper.getCategory());
            ps.setString(5, wallpaper.getTags());
            ps.setString(6, wallpaper.getUrl()); // image_path
            ps.setString(7, wallpaper.getResolution());
            ps.setString(8, wallpaper.getFileType());
            ps.setLong(9, wallpaper.getFileSize());
            ps.setString(10, wallpaper.getUploader());
            ps.setString(11, wallpaper.getCreatedAt());

            ps.executeUpdate();
        }
    }

    public boolean addLike(int userId, Wallpaper wallpaper) {
        String sql = "INSERT IGNORE INTO likes (user_id, wallhaven_id) VALUES (?, ?)";
        try {
            ensureWallpaper(wallpaper);
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ps.setString(2, wallpaper.getId());
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean removeLike(int userId, String wallhavenId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND wallhaven_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, wallhavenId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean addFavorite(int userId, Wallpaper wallpaper) {
        String sql = "INSERT IGNORE INTO favorites (user_id, wallhaven_id) VALUES (?, ?)";
        try {
            ensureWallpaper(wallpaper);
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ps.setString(2, wallpaper.getId());
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean removeFavorite(int userId, String wallhavenId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND wallhaven_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, wallhavenId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            return false;
        }
    }

    public List<Wallpaper> getLikedWallpapers(int userId) {
        String sql = "SELECT w.* FROM likes l JOIN wallpapers w ON w.wallhaven_id = l.wallhaven_id " +
                     "WHERE l.user_id = ? ORDER BY l.created_at DESC";
        return fetchWallpapers(sql, userId);
    }

    public List<Wallpaper> getFavoriteWallpapers(int userId) {
        String sql = "SELECT w.* FROM favorites f JOIN wallpapers w ON w.wallhaven_id = f.wallhaven_id " +
                     "WHERE f.user_id = ? ORDER BY f.created_at DESC";
        return fetchWallpapers(sql, userId);
    }

    private List<Wallpaper> fetchWallpapers(String sql, int userId) {
        List<Wallpaper> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Wallpaper w = new Wallpaper();
                    w.setId(rs.getString("wallhaven_id"));
                    w.setUrl(rs.getString("image_path") != null ? rs.getString("image_path") : rs.getString("url"));
                    w.setThumbUrl(rs.getString("thumb_url"));
                    w.setCategory(rs.getString("category"));
                    w.setTags(rs.getString("tags"));
                    w.setResolution(rs.getString("resolution"));
                    w.setFileType(rs.getString("file_type"));
                    w.setFileSize(rs.getLong("file_size"));
                    w.setUploader(rs.getString("uploader"));
                    w.setCreatedAt(rs.getString("created_at"));
                    list.add(w);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean isLiked(int userId, String wallhavenId) {
        String sql = "SELECT 1 FROM likes WHERE user_id = ? AND wallhaven_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, wallhavenId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean isFavorited(int userId, String wallhavenId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND wallhaven_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, wallhavenId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            return false;
        }
    }
}
