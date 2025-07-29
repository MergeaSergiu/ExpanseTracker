package expense.tracker.service;

import expense.tracker.dto.*;
import jakarta.validation.Valid;

import java.io.IOException;


public interface AuthenticationService {

 RegistrationResponse registerUser(@Valid UserDTO userDTO) throws IOException;

 LoginResponse loginUser(@Valid LoginDTO loginDTO);

 LoginResponse generateToken(JwtRefreshToken refreshToken);

 void requestPasswordReset(String email);

 boolean resetPassword(String email, String code, String newPassword);
}
