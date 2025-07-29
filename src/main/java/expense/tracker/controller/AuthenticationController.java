package expense.tracker.controller;


import expense.tracker.dto.*;
import expense.tracker.service.AuthenticationService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/user")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<RegistrationResponse> authenticate(@RequestBody @Valid UserDTO userDTO) throws IOException {
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

    @PostMapping("/resetPassword")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email){
        authenticationService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset requested");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String email,
                                                @RequestParam String code,
                                                @RequestParam String newPassword
                                                ){
        boolean success = authenticationService.resetPassword(email, code, newPassword);
        if(success){
            return ResponseEntity.ok("Password reset requested");
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid email or code");
        }
    }

}
