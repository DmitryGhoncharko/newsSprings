package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByCategoryId(Long id);
}
