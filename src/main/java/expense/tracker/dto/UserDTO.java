package expense.tracker.dto;

import jakarta.validation.constraints.*;

public record UserDTO (

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 6)
        String password
){ }


