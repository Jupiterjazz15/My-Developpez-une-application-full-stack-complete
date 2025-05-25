package com.openclassrooms.mddapi.dtos;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    // Déclaration de l'attribut content pour le contenu du commentaire
    @NotBlank(message = "Content cannot be empty")  // Validation pour s'assurer que le commentaire n'est pas vide
    private String content;

    // Getter et Setter pour content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
