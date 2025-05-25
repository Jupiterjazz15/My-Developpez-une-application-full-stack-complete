package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dtos.ArticleRequest;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.repositories.ArticleRepository;
import com.openclassrooms.mddapi.repositories.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.openclassrooms.mddapi.dtos.UserDto;
import com.openclassrooms.mddapi.models.User;


import java.time.LocalDate;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserService userService;

    // CREATE
    public Article saveArticle(ArticleRequest articleRequest) {
        // Vérifier l'authentification de l'utilisateur
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        // Récupérer l'utilisateur à partir du service
        UserDto userDto = userService.getUserByEmail(email);

        // Convertir UserDto en User
        User user = convertToUser(userDto);

        // Récupérer le thème correspondant au thème_id dans la requête
        Theme theme = themeRepository.findById(articleRequest.getThemeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid theme ID"));

        // Convertir l'ArticleRequest en Article
        Article article = convertToArticle(articleRequest);

        // Assigner les informations supplémentaires : auteur (user) et thème
        article.setAuthor(user);  // Assigner l'auteur
        article.setTheme(theme);  // Assigner le thème
        article.setCreatedAt(LocalDate.now());
        article.setUpdatedAt(LocalDate.now());

        // Sauvegarder l'article dans la base de données
        return articleRepository.save(article);
    }

    // ALL
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    // GET BY ID
    public Article getArticle(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    // UPDATE
    public Article updateArticle(Long id, ArticleRequest articleRequest) {
        Article articleToUpdate = getArticle(id);
        if (articleToUpdate == null) {
            return null;
        }

        // Vérifier si l'utilisateur est authentifié et s'il est bien l'auteur de l'article
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        UserDto user = userService.getUserByEmail(email);

        // Vérifier si l'utilisateur est autorisé à mettre à jour l'article
        if (!articleToUpdate.getAuthor().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("User is not authorized"); // Lancer une exception d'accès non autorisé
        }

        articleToUpdate.setTitle(articleRequest.getTitle());
        articleToUpdate.setContent(articleRequest.getContent());
        articleToUpdate.setUpdatedAt(LocalDate.now());

        // Mise à jour du thème si nécessaire
        Theme theme = themeRepository.findById(articleRequest.getThemeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid theme ID"));

        articleToUpdate.setTheme(theme);

        return articleRepository.save(articleToUpdate);
    }

    // DELETE
    public void deleteArticle(Long id) {
        Article articleToDelete = getArticle(id);
        if (articleToDelete != null) {
            articleRepository.delete(articleToDelete);
        }
    }

    // Méthode pour convertir un UserDto en User
    public User convertToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        // Assurez-vous d'ajouter d'autres champs nécessaires
        return user;
    }

    // CONVERTIR ARTICLEREQUEST EN ARTICLE
    public Article convertToArticle(ArticleRequest articleRequest) {
        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setCreatedAt(LocalDate.now());
        article.setUpdatedAt(LocalDate.now());
        return article;
    }
}
