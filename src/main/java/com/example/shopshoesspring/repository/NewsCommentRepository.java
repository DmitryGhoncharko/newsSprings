package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.Newscomment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsCommentRepository extends JpaRepository<Newscomment,Long> {
    List<Newscomment> findByNewsId(Long id);
}
