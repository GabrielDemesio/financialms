package financial.controller;

import financial.domain.User;
import financial.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {
    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Não autenticado.");
        }
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElse(null);
        if (u == null) return ResponseEntity.status(404).body("Usuário não encontrado.");
        return ResponseEntity.ok(new Object(){
            public final Long id = u.getId();
            public final String name = u.getName();
            public final String email = u.getEmail();
            public final String cpf = u.getCpf();
        });
    }
}
