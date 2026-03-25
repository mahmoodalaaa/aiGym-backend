package com.example.aiGym.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private AppUserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<AppUser> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String auth0Id = jwt.getSubject();

        // Auth0 custom claims or standard OIDC claims can hold the email
        // Make sure to add the "email" scope in the frontend request
        String email = jwt.getClaimAsString("email");
        if (email == null) {
            // Attempt an alternative claim namespace if configured with rules/actions,
            // e.g., "https://aiGym/email"
            // For now fallback to a placeholder if email is missing from the token
            email = "no-email-provided";
        }

        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    AppUser newUser = new AppUser(jwt.getSubject(),
                            jwt.getClaimAsString("email") != null ? jwt.getClaimAsString("email")
                                    : "temp-email@domain.com");
                    return userRepository.save(newUser);
                });

        return ResponseEntity.ok(user);
    }
}
