package world.deslauriers.service;

import world.deslauriers.exceptions.UserRegistrationException;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.database.User;

import java.util.Optional;

public interface UserService {

    Optional<User> lookupUserByUsername(String email);

    String registerUser(RegistrationDto registrationDto);
}
