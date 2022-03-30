package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.RoleRepository;

import java.util.HashSet;
import java.util.Optional;

@Singleton
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

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
    public Optional<Role> getById(Long id){

        return roleRepository.findById(id);
    }

    @Override
    public Iterable<Role> getAllRoles(){

        return roleRepository.findAll();
    }

    @Override
    public Role save(Role role){

        var lookup = roleRepository.findByRole(role.role());
        if (lookup.isPresent()){
            log.error("Attempt to create/save duplicate role: " + lookup);
            throw new IllegalArgumentException("Role with that name already exists.");
        }

        return roleRepository.save(new Role(role.role().toUpperCase(), role.title(), role.description()));
    }

    @Override
    public Role update(Role role){

        return roleRepository.update(new Role(role.id(), role.role().toUpperCase(), role.title(), role.description()));
    }

    @Override
    public void resolveRoles(HashSet<Role> roles, User user){

        var current = user.userRoles();
    }
}
