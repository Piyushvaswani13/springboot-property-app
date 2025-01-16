package com.realtors.springboot;


import java.sql.Timestamp;
import java.util.List;

public class CommentResponse {
    private Long commentId;
    private Long propertyId;
    private Long userId;
    private Long parentCommentId;
    private String commentText;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<CommentResponse> replies; // Only direct replies

    public CommentResponse(Long commentId, Long propertyId, Long userId, Long parentCommentId, String commentText, Timestamp createdAt, Timestamp updatedAt, List<CommentResponse> replies) {
        this.commentId = commentId;
        this.propertyId = propertyId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.replies = replies;
    }

    // Getters and setters

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CommentResponse> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentResponse> replies) {
        this.replies = replies;
    }
}

