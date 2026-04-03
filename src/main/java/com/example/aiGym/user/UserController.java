package com.example.aiGym.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AppUserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<AppUser> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String auth0Id = jwt.getSubject();

        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    AppUser newUser = new AppUser(jwt.getSubject(),
                            jwt.getClaimAsString("email") != null ? jwt.getClaimAsString("email")
                                    : "temp-email@domain.com");
                    return userRepository.save(newUser);
                });

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<AppUser> updateCurrentUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserUpdateDTO updateDTO) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String auth0Id = jwt.getSubject();
        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateDTO.getEmail() != null) user.setEmail(updateDTO.getEmail());
        if (updateDTO.getName() != null) user.setName(updateDTO.getName());
        if (updateDTO.getHeight() != null) user.setHeight(updateDTO.getHeight());
        if (updateDTO.getWeight() != null) user.setWeight(updateDTO.getWeight());
        if (updateDTO.getDailyCalories() != null) user.setDailyCalories(updateDTO.getDailyCalories());
        if (updateDTO.getDailyProtein() != null) user.setDailyProtein(updateDTO.getDailyProtein());
        if (updateDTO.getDailyCarbs() != null) user.setDailyCarbs(updateDTO.getDailyCarbs());
        if (updateDTO.getDailyFat() != null) user.setDailyFat(updateDTO.getDailyFat());

        return ResponseEntity.ok(userRepository.save(user));
    }
}
