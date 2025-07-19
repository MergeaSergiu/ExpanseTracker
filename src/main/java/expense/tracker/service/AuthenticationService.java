package expense.tracker.service;

import expense.tracker.dto.*;
import jakarta.validation.Valid;


public interface AuthenticationService {

 RegistrationResponse registerUser(@Valid UserDTO userDTO);

 LoginResponse loginUser(@Valid LoginDTO loginDTO);

 LoginResponse generateToken(JwtRefreshToken refreshToken);

}
