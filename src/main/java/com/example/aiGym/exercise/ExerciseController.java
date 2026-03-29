package com.example.aiGym.exercise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseApiService exerciseApiService;

    public ExerciseController(ExerciseApiService exerciseApiService) {
        this.exerciseApiService = exerciseApiService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExerciseDTO>> searchExercises(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ExerciseDTO> exercises = exerciseApiService.searchExercises(keyword, offset, limit);
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/muscle")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByMuscle(
            @RequestParam String muscle,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ExerciseDTO> exercises = exerciseApiService.getExercisesByMuscle(muscle, offset, limit);
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/bodypart")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByBodypart(
            @RequestParam String bodypart,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ExerciseDTO> exercises = exerciseApiService.getExercisesByBodypart(bodypart, offset, limit);
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/muscles")
    public ResponseEntity<List<String>> getAllMuscles() {
        try {
            List<String> muscles = exerciseApiService.getAllMuscles();
            return ResponseEntity.ok(muscles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/bodyparts")
    public ResponseEntity<List<String>> getAllBodyparts() {
        try {
            List<String> bodyparts = exerciseApiService.getAllBodyparts();
            return ResponseEntity.ok(bodyparts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
