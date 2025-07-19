package expense.tracker.controller;


import expense.tracker.dto.*;
import expense.tracker.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<RegistrationResponse> authenticate(@RequestBody @Valid UserDTO userDTO){
        RegistrationResponse registerResponse = authenticationService.registerUser(userDTO);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginDTO loginDTO){
        LoginResponse logicResponse = authenticationService.loginUser(loginDTO);
        return ResponseEntity.ok(logicResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody JwtRefreshToken refreshToken){
        LoginResponse refreshData = authenticationService.generateToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(refreshData);
    }

}
