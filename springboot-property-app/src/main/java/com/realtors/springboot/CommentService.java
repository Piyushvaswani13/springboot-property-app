package com.realtors.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    // Fetch top-level comments for a property
    public List<Comment> getCommentsForProperty(Long propertyId) {
        return commentRepository.findByPropertyIdAndParentCommentIdIsNull(
                propertyId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // Fetch all comments for all properties
    public List<Comment> getAllComments() {
        return commentRepository.findByPropertyIdIsNotNull(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // Add a new comment
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Fetch replies for a specific comment
    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentId(
                parentCommentId, Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    // Fetch top-level comments with their direct replies
    public List<CommentResponse> getCommentsWithReplies(Long propertyId) {
        // Fetch top-level comments
        List<Comment> topLevelComments = commentRepository.findByPropertyIdAndParentCommentIdIsNull(
                propertyId, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : topLevelComments) {
            // Fetch replies for each top-level comment
            List<Comment> replies = commentRepository.findByParentCommentId(
                    comment.getCommentId(), Sort.by(Sort.Direction.ASC, "createdAt"));

            // Build the response for top-level comment
            commentResponses.add(new CommentResponse(
                    comment.getCommentId(),
                    comment.getPropertyId(),
                    comment.getUserId(),
                    comment.getParentCommentId(),
                    comment.getCommentText(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt(),
                    mapRepliesToResponses(replies) // Direct replies
            ));
        }
        return commentResponses;
    }

    // Map replies to CommentResponse format
    private List<CommentResponse> mapRepliesToResponses(List<Comment> replies) {
        List<CommentResponse> replyResponses = new ArrayList<>();
        for (Comment reply : replies) {
            replyResponses.add(new CommentResponse(
                    reply.getCommentId(),
                    reply.getPropertyId(),
                    reply.getUserId(),
                    reply.getParentCommentId(),
                    reply.getCommentText(),
                    reply.getCreatedAt(),
                    reply.getUpdatedAt(),
                    null // No further nesting
            ));
        }
        return replyResponses;
    }
}
