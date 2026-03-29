package com.example.aiGym.exercise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private String exerciseId;
    private String name;
    private String targetMuscles;
    private String bodyParts;
    private String equipments;
    private List<String> secondaryMuscles;
    private String gifUrl;
    private List<String> instructions;
}
