package com.example.aiGym.workout;

import com.example.aiGym.user.AppUser;
import com.example.aiGym.user.AppUserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkoutController {

    private final AppUserRepository userRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WeightLogRepository weightLogRepository;

    @PostMapping("/workouts")
    public ResponseEntity<WorkoutSession> saveWorkoutSession(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody WorkoutSession session) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        String auth0Id = jwt.getSubject();
        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        session.setUser(user);

        // Bind bidirectional relationships so JPA maps foreign keys correctly
        if (session.getExercises() != null) {
            for (ExerciseLog exercise : session.getExercises()) {
                exercise.setWorkoutSession(session);
                if (exercise.getSets() != null) {
                    for (SetLog set : exercise.getSets()) {
                        set.setExerciseLog(exercise);
                    }
                }
            }
        }

        WorkoutSession saved = workoutSessionRepository.save(session);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/workouts")
    public ResponseEntity<List<WorkoutSession>> getWorkoutHistory(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        String auth0Id = jwt.getSubject();
        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<WorkoutSession> history = workoutSessionRepository.findByUserOrderByDateDesc(user);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/user/weight")
    public ResponseEntity<WeightLog> logWeight(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody WeightRequest request) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        String auth0Id = jwt.getSubject();
        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime date = LocalDateTime.now();
        if (request.getDate() != null) {
            try {
                date = LocalDateTime.parse(request.getDate());
            } catch (Exception e) {
                // fallback to now
            }
        }

        // Update current user profile weight
        user.setWeight(request.getWeight());
        userRepository.save(user);

        // Create and save WeightLog
        WeightLog log = new WeightLog(user, date, request.getWeight());
        WeightLog saved = weightLogRepository.save(log);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/weight")
    public ResponseEntity<List<WeightLog>> getWeightHistory(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        String auth0Id = jwt.getSubject();
        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<WeightLog> history = weightLogRepository.findByUserOrderByDateDesc(user);
        return ResponseEntity.ok(history);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WeightRequest {
        private Double weight;
        private String date;
    }
}
