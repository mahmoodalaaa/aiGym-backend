package com.example.aiGym.workout;

import com.example.aiGym.user.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "weight_logs")
@Getter
@Setter
@NoArgsConstructor
public class WeightLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Double weight;

    public WeightLog(AppUser user, LocalDateTime date, Double weight) {
        this.user = user;
        this.date = date;
        this.weight = weight;
    }
}
