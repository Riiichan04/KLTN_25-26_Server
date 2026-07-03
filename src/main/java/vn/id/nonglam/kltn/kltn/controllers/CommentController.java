package vn.id.nonglam.kltn.kltn.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.nonglam.kltn.kltn.dto.request.comments.InsertCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.request.comments.UpdateCommentRequest;
import vn.id.nonglam.kltn.kltn.dto.response.common.ServiceResponse;
import vn.id.nonglam.kltn.kltn.services.CommentService;

import java.util.UUID;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ServiceResponse> insertComment(@RequestBody InsertCommentRequest body) {
        ServiceResponse response = this.commentService.insertComments(body);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ServiceResponse> updateComment(@PathVariable UUID id, @RequestBody UpdateCommentRequest body) {
        return ResponseEntity.ok(commentService.updateComment(id, body));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ServiceResponse> deleteComment(@RequestBody UUID commentId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
