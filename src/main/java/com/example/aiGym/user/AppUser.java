package com.example.aiGym.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String auth0Id;

    @Column(nullable = false)
    private String email;

    private String name;
    private Double height;
    private Double weight;
    private Integer dailyCalories;
    private Integer dailyProtein;
    private Integer dailyCarbs;
    private Integer dailyFat;

    public AppUser(String auth0Id, String email) {
        this.auth0Id = auth0Id;
        this.email = email;
    }
}
