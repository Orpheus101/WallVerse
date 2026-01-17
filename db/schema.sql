-- Wallverse MySQL schema by Ayoub Hssine A.K.A Rais Ayoub Arsmouk hhhhh
CREATE DATABASE IF NOT EXISTS wallverse
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE wallverse;

CREATE TABLE IF NOT EXISTS users (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_username (username),
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS wallpapers (
  wallhaven_id VARCHAR(32) NOT NULL,
  url VARCHAR(1024) NOT NULL,
  thumb_url VARCHAR(1024) NOT NULL,
  category VARCHAR(50) DEFAULT NULL,
  tags TEXT DEFAULT NULL,
  image_path VARCHAR(1024) DEFAULT NULL,
  resolution VARCHAR(32) DEFAULT NULL,
  file_type VARCHAR(32) DEFAULT NULL,
  file_size BIGINT DEFAULT NULL,
  uploader VARCHAR(80) DEFAULT NULL,
  created_at DATETIME DEFAULT NULL,
  last_synced_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (wallhaven_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS likes (
  user_id INT NOT NULL,
  wallhaven_id VARCHAR(32) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, wallhaven_id),
  CONSTRAINT fk_likes_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_likes_wallpaper
    FOREIGN KEY (wallhaven_id) REFERENCES wallpapers(wallhaven_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS favorites (
  user_id INT NOT NULL,
  wallhaven_id VARCHAR(32) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, wallhaven_id),
  CONSTRAINT fk_favorites_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_favorites_wallpaper
    FOREIGN KEY (wallhaven_id) REFERENCES wallpapers(wallhaven_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
