package expense.tracker.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import expense.tracker.configuration.RabbitMQConfig;
import expense.tracker.dto.*;
import expense.tracker.entity.Role;
import expense.tracker.entity.User;
import expense.tracker.repository.RoleRepository;
import expense.tracker.repository.UserRepository;
import expense.tracker.service.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;

    public AuthenticationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RegistrationResponse registerUser(UserDTO userDTO) throws IOException {
        User foundUser = userRepository.findByUsername(userDTO.email()).orElse(null);
        if (foundUser != null) throw new EntityExistsException("User already exists");
        Role role = roleRepository.findByName("USER");
        if(role == null) throw new EntityExistsException("Role not exists");
        User user = new User(userDTO.email(), userDTO.firstName(), userDTO.lastName(), passwordEncoder.encode(userDTO.password()), role);
        userRepository.save(user);

        EmailRequest emailRequest = new EmailRequest(user.getUsername() );

        senObjectAsString(emailRequest);

        return new RegistrationResponse("Account creation successful");

    }

    private void senObjectAsString(EmailRequest emailRequest) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonEmailRequest = objectMapper.writeValueAsString(emailRequest);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    jsonEmailRequest
            );

        } catch ( JsonProcessingException e) {
            throw new IOException("Could not send a confirmation email");
        }
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

    @Override
    public void requestPasswordReset(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        String key = "pwd-reset:" + email;

        redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);
        ResetPassEmailMessage resetPassEmailMessage = new ResetPassEmailMessage(
                email,
                code
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.RESET_PASS_QUEUE, resetPassEmailMessage);
    }

    @Override
    public boolean resetPassword(String email, String code, String newPassword) {
        String key = "pwd-reset:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null || !storedCode.equals(code)) {
            return false;
        }

        User user = userRepository.findByUsername(email).orElse(null);
        if (user == null) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(key);
        return true;
    }
}
