package financial.services;

import financial.DTO.auth.RegisterDTO;
import financial.DTO.auth.UserProfileDTO;
import financial.entities.UserEntity;
import financial.exceptions.NotFoundException;
import financial.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return userRepository.findByCpfAndActiveTrue(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + cpf));
    }
    
    public UserEntity register(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.email())) {
            throw new IllegalArgumentException("Email já está em uso");
        }
        
        UserEntity user = new UserEntity();
        user.setCpf(registerDTO.cpf());
        user.setName(registerDTO.name());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setPhone(registerDTO.phone());
        user.setActive(true);
        
        return userRepository.save(user);
    }
    
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public UserEntity findByCpf(String cpf) {
        return userRepository.findByCpfAndActiveTrue(cpf)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
    
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .filter(UserEntity::isActive)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
    
    public UserProfileDTO getUserProfile(Long userId) {
        UserEntity user = findById(userId);
        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt()
        );
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndActiveTrue(email);
    }
}
