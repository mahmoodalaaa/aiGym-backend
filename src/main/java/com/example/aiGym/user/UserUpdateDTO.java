package com.example.aiGym.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String name;
    private Double height;
    private Double weight;
    private Integer dailyCalories;
    private Integer dailyProtein;
    private Integer dailyCarbs;
    private Integer dailyFat;
}
