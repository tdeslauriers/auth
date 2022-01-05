package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Singleton
public class UserServiceImpl implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    private final UserRepository userRepository;

    @Inject
    private final PasswordEncoderService passwordEncoderService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Optional<User> lookupUserByUsername(String email) {

        return userRepository.findByUsername(email);
    }

    @Override
    public String registerUser(RegistrationDto registrationDto){

        var message = new StringBuilder();
        var user = new User(
                registrationDto.username(),
                passwordEncoderService.encode(registrationDto.password()),
                registrationDto.firstname(),
                registrationDto.lastname(),
                LocalDate.now(),
                true , // set enabled to false when create email verification
                false,
                false);
        user = userRepository.save(user);
        log.info("Registration successful; User ID: " + user.id().toString());
        message.append("Registration successful"); // placeholder for email verification service.

        return message.toString();
    }
}
