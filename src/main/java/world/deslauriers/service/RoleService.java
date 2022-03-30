package world.deslauriers.service;

import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;

import java.util.HashSet;
import java.util.Optional;

public interface RoleService {


    Optional<Role> getRole(String role);

    Optional<Role> getById(Long id);

    Iterable<Role> getAllRoles();

    Role update(Role role);

    Role save(Role role);

    void resolveRoles(HashSet<Role> roles, User user);
}
