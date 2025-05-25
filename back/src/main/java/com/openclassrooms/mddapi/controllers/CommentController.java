package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dtos.CommentRequest;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // CREATE A COMMENT
    @Operation(summary = "Ajouter un commentaire à un article")
    @PostMapping("/article/{articleId}")
    public ResponseEntity<Map<String, String>> addComment(
            @Parameter(description = "ID de l'article auquel ajouter un commentaire") @PathVariable Long articleId,
            @Parameter(description = "Contenu du commentaire") @RequestBody @Valid CommentRequest commentRequest) {

        Comment newComment = commentService.addCommentToArticle(articleId, commentRequest);

        if (newComment == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to add comment"));
        }

        return ResponseEntity.ok(Map.of("message", "Comment added successfully"));
    }

    // ALL COMMENTS FOR AN ARTICLE
    @Operation(summary = "Récupérer tous les commentaires d'un article")
    @GetMapping("/article/{articleId}")
    public ResponseEntity<Map<String, List<Comment>>> getCommentsForArticle(
            @Parameter(description = "ID de l'article pour lequel récupérer les commentaires") @PathVariable Long articleId) {

        List<Comment> comments = commentService.getCommentsForArticle(articleId);

        return ResponseEntity.ok(Map.of("comments", comments));
    }

    // GET A COMMENT
    @Operation(summary = "Récupérer un commentaire par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(
            @Parameter(description = "ID du commentaire à récupérer") @PathVariable Long id) {

        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(comment);
    }
}
