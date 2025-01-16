package com.realtors.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/{propertyId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long propertyId) {
        List<CommentResponse> commentsWithReplies = commentService.getCommentsWithReplies(propertyId);
        return ResponseEntity.ok(commentsWithReplies);
    }

  @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addComment(@RequestBody CommentRequest commentRequest) {
        // Fetch the Property entity using the propertyId
        Property property = propertyRepository.findById(commentRequest.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        System.out.println("Received Property ID: " + commentRequest.getPropertyId());

        // Fetch the User entity (assuming you're getting it from a session or JWT token)
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create the Comment entity
        Comment comment = new Comment(
                null,  // commentId will be auto-generated // commentRequest.getCommentId()
                commentRequest.getPropertyId(),
                commentRequest.getUserId(),
                commentRequest.getParentCommentId(), // parentComment is optional
                commentRequest.getCommentText(),
                new Timestamp(System.currentTimeMillis()), // createdAt
                new Timestamp(System.currentTimeMillis())  // updatedAt
        );

        // Save the comment to the database
//        Comment savedComment = commentRepository.save(comment);
//
//        return ResponseEntity.ok(savedComment);
        // Save the comment using the service
        Comment savedComment = commentService.addComment(comment);

        // Create a custom response map
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment added successfully");
        response.put("commentId", savedComment.getCommentId());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long parentCommentId) {
        return ResponseEntity.ok(commentService.getReplies(parentCommentId));
    }

    @GetMapping("/with-replies/{propertyId}")
    public ResponseEntity<List<CommentResponse>> getCommentsWithReplies(@PathVariable Long propertyId) {
        // Call the service to get top-level comments with their direct replies
        List<CommentResponse> commentResponses = commentService.getCommentsWithReplies(propertyId);
        return ResponseEntity.ok(commentResponses);
    }

}

