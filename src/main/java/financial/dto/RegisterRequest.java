package financial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    public String name;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    @Size(min = 6, max = 100)
    public String password;

    @NotBlank
    @Size(min = 11, max = 14)
    public String cpf;
}
