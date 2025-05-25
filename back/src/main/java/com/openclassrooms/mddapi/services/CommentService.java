package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dtos.CommentRequest;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.repositories.CommentRepository;
import com.openclassrooms.mddapi.repositories.ArticleRepository;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.dtos.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    // CREATE: Ajouter un commentaire à un article
    public Comment addCommentToArticle(Long articleId, CommentRequest commentRequest) {
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

        // Récupérer l'article auquel le commentaire sera ajouté
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new IllegalArgumentException("Invalid article ID"));

        // Créer un nouvel objet Comment
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setCreatedAt(LocalDate.now());
        comment.setAuthor(convertToUser(userDto));  // Assigner l'auteur du commentaire
        comment.setArticle(article);  // Associer l'article

        return commentRepository.save(comment);
    }

    // GET: Récupérer un commentaire par son ID
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    // ALL: Récupérer tous les commentaires pour un article
    public List<Comment> getCommentsForArticle(Long articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    // Méthode pour convertir un UserDto en User
    private User convertToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        // Assurez-vous d'ajouter d'autres champs nécessaires
        return user;
    }
}
