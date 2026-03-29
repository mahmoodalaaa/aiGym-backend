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
public class ExerciseDBResponse {
    private boolean success;
    private int total;
    private int offset;
    private int limit;
    private List<ExerciseDTO> data;
}
