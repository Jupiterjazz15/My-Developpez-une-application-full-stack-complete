package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dtos.LoginRequest;
import com.openclassrooms.mddapi.dtos.RegisterRequest;
import com.openclassrooms.mddapi.dtos.UserDto;
import com.openclassrooms.mddapi.services.JWTService;
import com.openclassrooms.mddapi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("api/auth")
public class AuthController {

    private JWTService jwtService;

    private UserService userService;

    public AuthController(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> getToken(@Valid @RequestBody LoginRequest loginRequest) {
        // Recherche l'utilisateur soit par email, soit par nom d'utilisateur
        User user = userService.findByEmailOrName(loginRequest.getEmail());  // Note que findByEmailOrName existe déjà dans ton repository

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User does not exist"));
        }

        // Si l'utilisateur existe, générer un token
        String token = jwtService.generateToken(user.getEmail()); // Utiliser l'email pour générer le token, même si c'est par nom d'utilisateur

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Vérifier si l'utilisateur existe déjà
        if (userService.saveUser(registerRequest) == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }

        String token = jwtService.generateToken(registerRequest.getEmail());

        // Retourner le token JWT dans la réponse
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build(); // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaim("email");
        // Récupérer l'utilisateur à partir du service
        UserDto user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404).build(); // Retourner une erreur 404 si l'utilisateur n'est pas trouvé
        }

        return ResponseEntity.ok(user);
    }

}
