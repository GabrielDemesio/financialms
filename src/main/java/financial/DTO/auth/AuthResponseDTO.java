package financial.DTO.auth;

import java.time.LocalDateTime;

public record AuthResponseDTO(
    String token,
    String type,
    Long userId,
    String name,
    String email,
    LocalDateTime expiresAt
) {
    public static AuthResponseDTO of(String token, Long userId, String name, String email, LocalDateTime expiresAt) {
        return new AuthResponseDTO(token, "Bearer", userId, name, email, expiresAt);
    }
}
