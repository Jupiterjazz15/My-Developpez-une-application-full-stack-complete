package com.openclassrooms.mddapi.repositories;

import org.springframework.stereotype.Repository; // annotation qui indique que cette interface est un composant Spring pour gérer l'accès aux données.
import org.springframework.data.jpa.repository.JpaRepository; // fournit l'interface JpaRepository incluant les mthds pr interagir avec la DB (CRUD, etc.).
import com.openclassrooms.mddapi.models.User; // import du model User

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // MTHD DE JPAREPOSITORY POUR EVITER LES DOUBLONS
    boolean existsByEmail(String email);

    // MTHD DE JPAREPOSITORY
    User findByEmail(String email);

    // Ajout d'une méthode pour rechercher un utilisateur par email ou nom d'utilisateur
    User findByEmailOrName(String email, String name); // Permet de rechercher par email ou nom
}