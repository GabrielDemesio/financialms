package financial.controller;

import financial.domain.User;
import financial.dto.AuthResponse;
import financial.dto.LoginRequest;
import financial.dto.RegisterRequest;
import financial.repository.UserRepository;
import financial.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body) {
        if (userRepository.existsByEmail(body.email)) {
            return ResponseEntity.badRequest().body("Email já está em uso.");
        }
        if (userRepository.existsByCpf(body.cpf)) {
            return ResponseEntity.badRequest().body("CPF já está em uso.");
        }
        User u = new User();
        u.setName(body.name);
        u.setEmail(body.email);
        u.setCpf(body.cpf);
        u.setPasswordHash(passwordEncoder.encode(body.password));
        u = userRepository.save(u);

        String token = jwtService.generateToken(u.getId(), u.getEmail());
        AuthResponse resp = new AuthResponse();
        resp.userId = u.getId();
        resp.name = u.getName();
        resp.email = u.getEmail();
        resp.cpf = u.getCpf();
        resp.token = token;
        return ResponseEntity.created(URI.create("/me")).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body) {
        User u = userRepository.findByEmail(body.email).orElse(null);
        if (u == null) {
            return ResponseEntity.status(401).body("Credenciais inválidas.");
        }
        PasswordEncoder enc = passwordEncoder;
        if (!enc.matches(body.password, u.getPasswordHash())) {
            return ResponseEntity.status(401).body("Credenciais inválidas.");
        }
        String token = jwtService.generateToken(u.getId(), u.getEmail());
        AuthResponse resp = new AuthResponse();
        resp.userId = u.getId();
        resp.name = u.getName();
        resp.email = u.getEmail();
        resp.cpf = u.getCpf();
        resp.token = token;
        return ResponseEntity.ok(resp);
    }
}
