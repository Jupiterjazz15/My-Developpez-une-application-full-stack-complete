package com.openclassrooms.mddapi.repositories;

import com.openclassrooms.mddapi.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Trouver tous les abonnements d'un utilisateur
    List<Subscription> findByUserId(Long userId);

    // Trouver un abonnement spécifique d'un utilisateur pour un thème spécifique
    Subscription findByUserIdAndThemeId(Long userId, Long themeId);

    // Trouver tous les abonnements pour un thème
    List<Subscription> findByThemeId(Long themeId);

    // Compter le nombre d'abonnés à un thème spécifique
    Long countByThemeId(Long themeId);
}
