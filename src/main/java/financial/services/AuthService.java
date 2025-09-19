package financial.services;

import financial.DTO.auth.AuthResponseDTO;
import financial.DTO.auth.LoginDTO;
import financial.DTO.auth.RegisterDTO;
import financial.entities.UserEntity;
import financial.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        UserEntity user = userService.register(registerDTO);
        String token = jwtUtil.generateToken(user, user.getId());
        
        return AuthResponseDTO.of(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                jwtUtil.extractExpirationAsLocalDateTime(token)
        );
    }
    
    public AuthResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.cpf(),
                        loginDTO.password()
                )
        );
        
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user, user.getId());
        
        return AuthResponseDTO.of(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                jwtUtil.extractExpirationAsLocalDateTime(token)
        );
    }
}
