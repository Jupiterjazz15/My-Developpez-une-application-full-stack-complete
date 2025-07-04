package com.openclassrooms.mddapi.dtos;

import jakarta.validation.constraints.NotBlank;  // Annotation pour spécifier que le champ ne doit pas être vide

// DTO pour transporter les données envoyées par l'utilisateur lors d'une requête de connexion
public class LoginRequest {

    // Déclaration des attributs
    @NotBlank
    private String usernameOrEmail; // Modification du champ pour accepter email ou nom d'utilisateur

    @NotBlank
    private String password;

    // Getters et Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
