package com.wallverse.model;

import java.time.LocalDateTime;
import java.util.Objects;


public class user {
    private int id;
    private String username;
    private String email;
    private String password;
    private LocalDateTime created_at;
}

public User() {
    this.created_at = LocalDateTime.now();
}


public User ( int id, String username, String email, String password, LocalDateTime created_at) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
   this.created_at = LocalDateTime.now();
}

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getUsername() {
    return username;
} 

public void setUsername (String username) {
    this.username = username;
}

public String getEmail() {
    return email;
}

public void setEmail( String email) {
    this.email = email;
}

public String getPassword(){
    return password;
}

public void setPassword(String password){
    this.password = password;
}

public LocalDateTime getCreatedAt() {
    return created_at;
}

public void setCreatedAt(LocalDateTime created_at) {
    this.created_at = created_at;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id == user.id && Objects.equals(username, user.username);
}

@Override
public int hashCode() {
    return Objects.hash(id, username);
}

@Override
public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", created_at=" + created_at +
            '}';
}