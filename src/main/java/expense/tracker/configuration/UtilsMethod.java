package expense.tracker.configuration;


import expense.tracker.entity.User;
import expense.tracker.repository.UserRepository;
import expense.tracker.service.impl.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UtilsMethod {

    public UtilsMethod(JwtService jwtService, UserRepository userRepository){
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    private final JwtService jwtService;

    private final UserRepository userRepository;

    public User extractUsernameFromAuthorizationHeader(String authorization){
        String jwt = authorization.substring(7);
        String username = jwtService.extractUsername(jwt);
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }
}
