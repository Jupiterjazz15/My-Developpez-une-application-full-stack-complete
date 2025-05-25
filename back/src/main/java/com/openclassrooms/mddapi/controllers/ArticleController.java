package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dtos.ArticleRequest;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.services.ArticleService;
import com.openclassrooms.mddapi.services.ThemeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ThemeService themeService;

    public ArticleController(ArticleService articleService, ThemeService themeService) {
        this.articleService = articleService;
        this.themeService = themeService;
    }

    // CREATE AN ARTICLE
    @Operation(summary = "Créer un nouvel article")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createArticle(
            @Parameter(description = "Titre de l'article") @RequestBody @Valid ArticleRequest articleRequest) {

        // Vérifier si le thème existe
        if (articleRequest.getThemeId() == null || themeService.getTheme(articleRequest.getThemeId()) == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Theme not found"));
        }

        // Convertir l'ArticleRequest en un objet Article et enregistrer
        articleService.saveArticle(articleRequest);  // Sauvegarder l'article dans la base de données

        return ResponseEntity.ok(Map.of("message", "Article created successfully"));
    }

    // ALL ARTICLES
    @Operation(summary = "Récupérer la liste de tous les articles")
    @GetMapping()
    public Map<String, List<Article>> getArticles() {
        List<Article> articles = articleService.getArticles();
        return Map.of("articles", articles);
    }

    // GET AN ARTICLE
    @Operation(summary = "Récupérer un article par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(
            @Parameter(description = "ID de l'article à récupérer") @PathVariable Long id) {
        Article article = articleService.getArticle(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(article);
    }

    // UPDATE AN ARTICLE
    @Operation(summary = "Mettre à jour un article existant")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateArticle(
            @Parameter(description = "ID de l'article à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Détails de l'article à mettre à jour") @Valid @RequestBody ArticleRequest articleRequest) {

        try {
            Article updatedArticle = articleService.updateArticle(id, articleRequest);
            // Gérer le cas où l'article est introuvable
            if (updatedArticle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Article not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Article updated!"));

        } catch (AccessDeniedException ex) {
            // Gérer le cas où l'utilisateur n'est pas autorisé
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    // DELETE AN ARTICLE
    @Operation(summary = "Supprimer un article")
    @DeleteMapping("/{id}")  // Correction ici
    public ResponseEntity<Map<String, String>> deleteArticle(
            @Parameter(description = "ID de l'article à supprimer") @PathVariable Long id) {

        articleService.deleteArticle(id);
        return ResponseEntity.ok(Map.of("message", "Article deleted"));
    }
}
