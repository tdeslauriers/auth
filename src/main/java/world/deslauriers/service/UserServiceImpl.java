package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Singleton
public class UserServiceImpl implements UserService{

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

        return message.toString();
    }
}
