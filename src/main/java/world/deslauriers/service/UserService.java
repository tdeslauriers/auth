package world.deslauriers.service;

import world.deslauriers.model.database.User;

import java.util.Optional;

public interface UserService {

    Optional<User> lookupUserByEmail(String email);
}
