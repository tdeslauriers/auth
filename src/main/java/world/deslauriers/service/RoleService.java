package world.deslauriers.service;

import world.deslauriers.model.database.Role;

import java.util.Optional;

public interface RoleService {


    Optional<Role> getRole(String role);
}
