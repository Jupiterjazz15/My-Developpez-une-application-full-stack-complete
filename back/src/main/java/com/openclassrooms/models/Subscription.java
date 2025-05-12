package com.openclassrooms.mddapi.models;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "theme_id" })
})
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 L’utilisateur abonné
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 🔗 Le thème auquel il est abonné
    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    public Subscription() {}

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
