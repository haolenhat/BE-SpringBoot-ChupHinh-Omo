package com.example.photo_imgbb.repository;

import com.example.photo_imgbb.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
