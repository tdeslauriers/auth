package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.RoleRepository;

import java.util.HashSet;
import java.util.Optional;

@Singleton
public class RoleServiceImpl implements RoleService {

    @Inject
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> getRole(String role){

        return roleRepository.findByRole(role);
    }

    @Override
    public Iterable<Role> getAllRoles(){

        return roleRepository.findAll();
    }

    @Override
    public Role save(Role role){

        return roleRepository.save(role);
    }

    @Override
    public Role update(Role role){

        return roleRepository.update(role);
    }

    @Override
    public void resolveRoles(HashSet<Role> roles, User user){

        var current = user.userRoles();


    }
}
