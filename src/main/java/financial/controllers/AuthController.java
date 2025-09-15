package financial.controllers;

import financial.DTO.auth.AuthResponseDTO;
import financial.DTO.auth.LoginDTO;
import financial.DTO.auth.RegisterDTO;
import financial.DTO.auth.UserProfileDTO;
import financial.entities.UserEntity;
import financial.services.AuthService;
import financial.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }
    
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }
    
    @GetMapping("/profile")
    public UserProfileDTO getProfile(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return userService.getUserProfile(user.getId());
    }
    
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Token válido");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
    }
}
