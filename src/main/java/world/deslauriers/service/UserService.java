package world.deslauriers.service;

import world.deslauriers.model.database.User;
import world.deslauriers.model.registration.RegistrationDto;

import java.util.Optional;

public interface UserService {

    Optional<User> lookupUserByUsername(String email);

    String registerUser(RegistrationDto registrationDto);
}
