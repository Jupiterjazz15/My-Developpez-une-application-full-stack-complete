package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dtos.SubscriptionRequest;
import com.openclassrooms.mddapi.models.Subscription;
import com.openclassrooms.mddapi.services.SubscriptionService;
import com.openclassrooms.mddapi.services.ThemeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final ThemeService themeService;

    public SubscriptionController(SubscriptionService subscriptionService, ThemeService themeService) {
        this.subscriptionService = subscriptionService;
        this.themeService = themeService;
    }

    // CREATE A SUBSCRIPTION
    @Operation(summary = "S'abonner à un thème")
    @PostMapping()
    public ResponseEntity<Map<String, String>> createSubscription(
            @Parameter(description = "Détails de l'abonnement") @RequestBody SubscriptionRequest subscriptionRequest) {

        // Vérifier si le thème existe
        if (subscriptionRequest.getThemeId() == null || themeService.getTheme(subscriptionRequest.getThemeId()) == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Theme not found"));
        }

        // Créer l'abonnement
        Subscription subscription = subscriptionService.subscribeToTheme(subscriptionRequest.getThemeId());

        if (subscription == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User is already subscribed to this theme"));
        }

        return ResponseEntity.ok(Map.of("message", "Successfully subscribed to the theme"));
    }

    // GET ALL SUBSCRIPTIONS
    @Operation(summary = "Récupérer tous les abonnements d'un utilisateur")
    @GetMapping()
    public ResponseEntity<Map<String, List<Subscription>>> getSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getUserSubscriptions();
        return ResponseEntity.ok(Map.of("subscriptions", subscriptions));
    }

    // DELETE A SUBSCRIPTIONS
    @DeleteMapping("/{themeId}")
    public ResponseEntity<Map<String, String>> unsubscribeFromTheme(
            @Parameter(description = "ID du thème à se désabonner") @PathVariable Long themeId) {

        boolean success = subscriptionService.unsubscribeFromTheme(themeId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Successfully unsubscribed from the theme"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Subscription not found or already unsubscribed"));
        }
    }

}
