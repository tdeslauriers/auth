package world.deslauriers.service;

import world.deslauriers.model.database.User;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.profile.UserDto;
import world.deslauriers.model.registration.RegistrationDto;

import java.util.Optional;

public interface UserService {

    Boolean userExists(String email);
    String registerUser(RegistrationDto registrationDto);

    Optional<User> lookupUserByUsername(String email);
    Optional<User> lookupUserById(Long id);
    Iterable<ProfileDto> getAllUsers();

    Optional<ProfileDto> getProfile(String username);
    Optional<ProfileDto> getProfileById(Long id);

    void updateUser(User user, ProfileDto updatedProfile);
}
