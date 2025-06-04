package com.openclassrooms.mddapi.dtos;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {

    @NotNull(message = "Theme ID is required")
    private Long themeId;

    // Getters and Setters
    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }
}
