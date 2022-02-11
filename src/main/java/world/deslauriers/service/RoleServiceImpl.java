package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import world.deslauriers.model.database.Role;
import world.deslauriers.repository.RoleRepository;

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
    public Role save(Role role){

        // placeholder for input validation
        return roleRepository.save(role);
    }
}
