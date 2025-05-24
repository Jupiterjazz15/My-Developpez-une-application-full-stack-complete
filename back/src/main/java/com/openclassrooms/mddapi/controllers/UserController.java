package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dtos.UserDto;
import com.openclassrooms.mddapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class UserController {

    // Injection par constructeur
    private final UserService userService; // final pour garantir l'immutabilité

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Récupérer un utilisateur par son ID")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "ID de l'utilisateur à récupérer") @PathVariable Long id) {
        UserDto user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
