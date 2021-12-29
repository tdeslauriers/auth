package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.UserRepository;

import java.util.Optional;

@Singleton
public class UserServiceImpl implements UserService{

    @Inject
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> lookupUserByUsername(String email) {

        return userRepository.findByUsername(email);
    }
}
