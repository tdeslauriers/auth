package world.deslauriers.service;

import world.deslauriers.model.database.User;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.profile.UserDto;
import world.deslauriers.model.registration.RegistrationDto;

import java.util.Optional;

public interface UserService {

    Boolean userExists(String email);

    Optional<User> lookupUserByUsername(String email);

    Iterable<UserDto> getAllUsers();

    String registerUser(RegistrationDto registrationDto);

    Optional<ProfileDto> getProfile(String username);
}
