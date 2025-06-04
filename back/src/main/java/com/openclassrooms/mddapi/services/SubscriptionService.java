package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.models.Subscription;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.repositories.SubscriptionRepository;
import com.openclassrooms.mddapi.repositories.ThemeRepository;
import com.openclassrooms.mddapi.dtos.UserDto;
import com.openclassrooms.mddapi.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ThemeService themeService; // Injection de ThemeService

    // CREATE (S'abonner à un thème)
    public Subscription subscribeToTheme(Long themeId) {
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

        // Récupérer le thème auquel l'utilisateur souhaite s'abonner
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid theme ID"));

        // Vérifier si l'utilisateur est déjà abonné à ce thème
        Subscription existingSubscription = subscriptionRepository.findByUserIdAndThemeId(user.getId(), theme.getId());
        if (existingSubscription != null) {
            return null; // Retourner null si l'utilisateur est déjà abonné
        }

        // Créer un nouvel abonnement
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTheme(theme);

        // Sauvegarder l'abonnement dans la base de données
        return subscriptionRepository.save(subscription);
    }

    public boolean unsubscribeFromTheme(Long themeId) {
        // Vérifier l'authentification de l'utilisateur
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false; // Retourner false si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        // Récupérer l'utilisateur à partir du service
        UserDto userDto = userService.getUserByEmail(email);

        // Convertir UserDto en User
        User user = convertToUser(userDto);

        // Récupérer le thème correspondant au themeId
        Theme theme = themeService.getTheme(themeId);
        if (theme == null) {
            return false; // Le thème n'existe pas
        }

        // Chercher l'abonnement correspondant à l'utilisateur et au thème
        Subscription subscription = subscriptionRepository.findByUserIdAndThemeId(user.getId(), themeId);
        if (subscription != null) {
            subscriptionRepository.delete(subscription); // Supprimer l'abonnement
            return true; // L'utilisateur a été désabonné avec succès
        }

        return false; // L'utilisateur n'était pas abonné à ce thème
    }

    // ALL (Récupérer tous les abonnements d'un utilisateur)
    public List<Subscription> getUserSubscriptions() {
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

        // Récupérer les abonnements de l'utilisateur
        return subscriptionRepository.findByUserId(user.getId());
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
}
