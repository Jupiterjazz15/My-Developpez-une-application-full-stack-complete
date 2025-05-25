package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.repositories.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.openclassrooms.mddapi.dtos.UserDto;
import com.openclassrooms.mddapi.dtos.ThemeRequest;
import java.time.LocalDate;
import java.util.List;

@Service
public class ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserService userService;

    // CREATE
    public Theme saveTheme(ThemeRequest themeRequest) {
        // Vérifier l'authentification de l'utilisateur
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        // Récupérer l'utilisateur à partir du service
        UserDto user = userService.getUserByEmail(email);

        // Convertir le ThemeRequest en Theme
        Theme theme = convertToTheme(themeRequest);

        // Assignation de l'ID de l'utilisateur comme propriétaire
        theme.setOwner_id(user.getId());
        theme.setCreatedAt(LocalDate.now());
        theme.setUpdatedAt(LocalDate.now());

        return themeRepository.save(theme);
    }

    // ALL
    public List<Theme> getThemes() {
        return themeRepository.findAll();
    }

    // GET BY ID
    public Theme getTheme(Long id) {
        return themeRepository.findById(id).orElse(null);
    }

    // GET BY TITLE
    public Theme getThemeByTitle(String title) {
        return themeRepository.getThemeByTitle(title);  // Utilisation de la méthode du repository
    }

    // UPDATE
    public Theme updateTheme(Long id, ThemeRequest themeRequest) {
        Theme themeToUpdate = getTheme(id);
        if (themeToUpdate == null) {
            return null;
        }

        // Vérifier si l'utilisateur est authentifié et s'il est bien le propriétaire du thème
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        UserDto user = userService.getUserByEmail(email);

        // Vérifier si l'utilisateur est autorisé à mettre à jour le thème
        if (!themeToUpdate.getOwner_id().equals(user.getId())) {
            throw new AccessDeniedException("User is not authorized"); // Lancer une exception d'accès non autorisé
        }

        themeToUpdate.setTitle(themeRequest.getTitle());
        themeToUpdate.setDescription(themeRequest.getDescription());
        themeToUpdate.setUpdatedAt(LocalDate.now());

        return themeRepository.save(themeToUpdate);
    }

    // DELETE
    public void deleteTheme(Long id) {
        Theme themeToDelete = getTheme(id);
        if (themeToDelete != null) {
            themeRepository.delete(themeToDelete);
        }
    }

    // Méthode pour convertir un ThemeRequest en Theme
    public Theme convertToTheme(ThemeRequest themeRequest) {
        Theme theme = new Theme();
        theme.setTitle(themeRequest.getTitle());
        theme.setDescription(themeRequest.getDescription());
        theme.setCreatedAt(LocalDate.now());
        theme.setUpdatedAt(LocalDate.now());
        // Assigner un owner_id si nécessaire
        // theme.setOwner_id(someOwnerId);
        return theme;
    }
}
