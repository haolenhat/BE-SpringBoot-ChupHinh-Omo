package com.example.photo_imgbb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;          // link ảnh imgbb
    private String publicId;     // lưu delete_url (để xóa ảnh sau này)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Photo() {}

    public Photo(Long id, String url, String publicId, LocalDateTime createdAt) {
        this.id = id;
        this.url = url;
        this.publicId = publicId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
