package financial.utils;

import financial.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
            return user.getId();
        }
        throw new IllegalStateException("Usuário não autenticado");
    }
    
    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
            return user;
        }
        throw new IllegalStateException("Usuário não autenticado");
    }
    
    public static Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
            return user.getId();
        }
        throw new IllegalStateException("Usuário não autenticado");
    }
    
    public static UserEntity getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
            return user;
        }
        throw new IllegalStateException("Usuário não autenticado");
    }
}
