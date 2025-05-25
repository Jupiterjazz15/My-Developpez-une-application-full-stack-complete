package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dtos.ThemeRequest;
import com.openclassrooms.mddapi.models.Theme;
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
@RequestMapping("api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    // CREATE A THEME
    @Operation(summary = "Créer un nouveau thème")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createTheme(
            @Parameter(description = "Titre du thème") @RequestBody @Valid ThemeRequest themeRequest) {

            // Vérifier si un thème avec le même titre existe déjà
            Theme existingTheme = themeService.getThemeByTitle(themeRequest.getTitle());
            if (existingTheme != null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Theme already exists"));
            }

            // Convertir le ThemeRequest en un objet Theme et enregistrer
            Theme theme = themeService.convertToTheme(themeRequest);

            // Sauvegarder le thème dans la base de données
            themeService.saveTheme(themeRequest);  // Correction ici : pas besoin de re-convertir car saveTheme fait la conversion

            return ResponseEntity.ok(Map.of("message", "Theme created successfully"));
    }

    // ALL THEMES
    @Operation(summary = "Récupérer la liste de tous les thèmes")
    @GetMapping()
    public Map<String, List<Theme>> getThemes() {
        List<Theme> themes = themeService.getThemes();
        return Map.of("themes", themes);
    }

    // GET A THEME
    @Operation(summary = "Récupérer un thème par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<Theme> getTheme(
            @Parameter(description = "ID du thème à récupérer") @PathVariable Long id) {
            Theme theme = themeService.getTheme(id);
            if (theme == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(theme);
    }

    // UPDATE A THEME
    @Operation(summary = "Mettre à jour un thème existant")
    @PutMapping("/{id}")  // Correction ici
    public ResponseEntity<Map<String, String>> updateTheme(
            @Parameter(description = "ID du thème à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Détails du thème à mettre à jour") @Valid @RequestBody ThemeRequest themeRequest) {

        try {
            Theme updatedTheme = themeService.updateTheme(id, themeRequest);
            // Gérer le cas où le thème est introuvable
            if (updatedTheme == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Theme not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Theme updated!"));

        } catch (AccessDeniedException ex) {
            // Gérer le cas où l'utilisateur n'est pas autorisé
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    // DELETE A THEME
    @Operation(summary = "Supprimer un thème")
    @DeleteMapping("/{id}")  // Correction ici
    public ResponseEntity<Map<String, String>> deleteTheme(
            @Parameter(description = "ID du thème à supprimer") @PathVariable Long id) {

        themeService.deleteTheme(id);
        return ResponseEntity.ok(Map.of("message", "Theme deleted"));
    }
}
