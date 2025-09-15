package financial.DTO.auth;

import java.time.LocalDateTime;

public record UserProfileDTO(
    Long id,
    String name,
    String email,
    String phone,
    LocalDateTime createdAt
) {}
