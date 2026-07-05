package com.example.aiGym.workout;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "set_logs")
@Getter
@Setter
@NoArgsConstructor
public class SetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_log_id", nullable = false)
    @JsonBackReference
    private ExerciseLog exerciseLog;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private Boolean isCompleted;
}
