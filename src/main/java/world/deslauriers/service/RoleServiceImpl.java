package world.deslauriers.service;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.model.dto.RemoveUserRoleCmd;
import world.deslauriers.model.dto.RoleDto;
import world.deslauriers.repository.RoleRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.util.HashSet;

@Singleton
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;

    public RoleServiceImpl(RoleRepository roleRepository, UserRoleRepository userRoleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.userService = userService;
    }

    @Override
    public Mono<Role> getRole(String role){
        return roleRepository.findByRole(role);
    }

    @Override
    public Mono<Role> getById(Long id){
        return roleRepository.findById(id);
    }

    @Override
    public Flux<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    @Override
    public Mono<Role> save(RoleDto cmd){
        return roleRepository.save(new Role(cmd.role().toUpperCase(), cmd.title(), cmd.description()));
    }

    @Override
    public Mono<Role> update(RoleDto cmd){
        return roleRepository.update(new Role(cmd.id(), cmd.role().toUpperCase(), cmd.title(), cmd.description()));
    }

    @Override
    public void resolveRoles(HashSet<Role> roles, User user){

        roles.forEach(role -> {
            if (user.userRoles().stream().noneMatch(userRole -> userRole.role().id().equals(role.id()))){
                roleRepository.findById(role.id())
                        .flatMap(r -> userRoleRepository.save(new UserRole(user, r)))
                        .switchIfEmpty(Mono.defer(() -> {
                            log.error("Attempt to add role that does not exist.");
                            return Mono.empty();
                        }))
                        .subscribe(userRole -> log.info("Created xref id: {}, user: {} <--> role: {}",
                                userRole.id(), userRole.user().username(), userRole.role().title()));
            }
        });
    }

    @Override
    public Mono<Void> deleteRole(long id) {

        return userRoleRepository.findByRoleId(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Attempting to delete role id the does not exist.");
                    return Mono.empty();
                }))
                .flatMap(userRole -> {
                    log.info("Removing xref: {} (role {}: {} from user: {}: {}).",
                            userRole.id(),
                            userRole.role().id(),
                            userRole.role().role(),
                            userRole.user().id(),
                            userRole.user().username());
                    return userRoleRepository.delete(userRole);
                })
                .then(Mono.defer(() -> {
                    log.info("Deleting role id: {}", id);
                    return roleRepository.deleteById(id);
                }))
                .then();
    }

    @Override
    public Mono<Void> removeUserRole(RemoveUserRoleCmd cmd) {

        return userService.getUserById(cmd.userId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Attempt to remove userRole xref from user id that does not exist.");
                    return Mono.empty();
                }))
                .zipWith(roleRepository.findById(cmd.roleId())
                        .switchIfEmpty(Mono.defer(() -> {
                            log.error("Attempt to remove userRole xref from role id that does not exist.");
                            return Mono.empty();
                        })))
                .flatMap(objects -> userRoleRepository.findByUserAndRole(objects.getT1(), objects.getT2()))
                .flatMap(userRole -> {
                    log.info("Deleting xref from user: {} and role: {}", userRole.user().username(), userRole.role().role());
                    return userRoleRepository.delete(userRole);
                })
                .then();
    }
}
