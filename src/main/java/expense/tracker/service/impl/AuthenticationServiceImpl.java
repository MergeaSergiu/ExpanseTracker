package expense.tracker.service.impl;

import expense.tracker.dto.*;
import expense.tracker.entity.Role;
import expense.tracker.entity.User;
import expense.tracker.repository.RoleRepository;
import expense.tracker.repository.UserRepository;
import expense.tracker.service.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegistrationResponse registerUser(UserDTO userDTO) {
        User foundUser = userRepository.findByUsername(userDTO.email()).orElse(null);
        if (foundUser != null) throw new EntityExistsException("User already exists");
        Role role = roleRepository.findByName("USER");
        if(role == null) throw new EntityExistsException("Role not exists");
        User user = new User(userDTO.email(), userDTO.firstName(), userDTO.lastName(), passwordEncoder.encode(userDTO.password()), role);
        userRepository.save(user);
        return new RegistrationResponse("Account created successfully");
    }

    @Override
    public LoginResponse loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.email()).orElse(null);
        if (user == null) throw new EntityExistsException("User does not exist");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.email(),
                        loginDTO.password()
                )
        );

        return new LoginResponse(
                jwtService.generateToken(user.getUsername(), user.getRole().getName()),
                jwtService.generateRefreshToken(user.getUsername(), user.getRole().getName()),
                user.getRole().getName()
        );
    }

    @Override
    public LoginResponse generateToken(JwtRefreshToken refreshToken) {
        if(!jwtService.validateToken(refreshToken.getRefresh_JWT())) throw new EntityExistsException("Refresh Token expired");
        String username = jwtService.extractUsername(refreshToken.getRefresh_JWT());
        if(username == null) throw new EntityNotFoundException("Refresh token does not exist");
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        String access_token = jwtService.generateToken(user.getUsername(), user.getRole().getName());
        String role = jwtService.extractRole(refreshToken.getRefresh_JWT());
        return new LoginResponse(
                access_token,
                refreshToken.getRefresh_JWT(),
                role);
    }
}
