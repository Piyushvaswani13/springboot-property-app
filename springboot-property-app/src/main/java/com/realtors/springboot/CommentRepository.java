package com.realtors.springboot;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPropertyIdAndParentCommentIdIsNull(Long propertyId, Sort sort);
    List<Comment> findByParentCommentId(Long parentCommentId, Sort sort);
    List<Comment> findByPropertyIdIsNotNull(Sort sort);
}



