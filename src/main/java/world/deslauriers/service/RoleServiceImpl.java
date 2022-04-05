package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.repository.RoleRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.util.HashSet;
import java.util.Optional;

@Singleton
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Inject
    private final RoleRepository roleRepository;

    @Inject
    private final UserRoleRepository userRoleRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
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

        // add any UserRoles the old set does not contain
        roles.forEach(role -> {
            if (user.userRoles().stream().noneMatch(userRole -> userRole.role().id().equals(role.id()))){
                var exists = roleRepository.findById(role.id());
                if (exists.isEmpty()){
                    log.error("Attempt to add non-existent role to " + user.username());
                    throw new IllegalArgumentException("Role does not exist");
                }
                userRoleRepository.save(new UserRole(user, exists.get()));
                log.info("Added role " + exists.get().title() + " to " + user.username());
            }
        });

        // remove any UserRoles the new set does not contain.
        user.userRoles().forEach(userRole -> {
            if (roles.stream().noneMatch(role -> role.id().equals(userRole.role().id()))){
                var exists = userRoleRepository.findByUserAndRole(user, userRole.role());
                if (exists.isPresent()){
                    userRoleRepository.delete(exists.get());
                    log.info("Removed role " + userRole.role() + " from user " + user.username());
                }
            }
        });
    }
}
