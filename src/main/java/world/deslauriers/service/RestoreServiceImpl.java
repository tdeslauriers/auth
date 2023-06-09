package world.deslauriers.service;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.model.dto.BackupRole;
import world.deslauriers.model.dto.BackupUser;
import world.deslauriers.model.database.User;
import world.deslauriers.model.dto.BackupUserrole;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class RestoreServiceImpl implements RestoreService {

    private static final Logger log = LoggerFactory.getLogger(RestoreServiceImpl.class);

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;


    public RestoreServiceImpl(UserService userService, RoleService roleService, UserRoleRepository userRoleRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Mono<User> restoreUser(BackupUser backupUser) {

        // TODO: add decryption/type-conversion logic after initial data restore.
        // TODO: add check to see if record exists, or is more current than backup.
        // temp

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        var created = LocalDateTime.parse(backupUser.dateCreated(), formatter);
        var bday = (backupUser.birthday() != null) ? LocalDateTime.parse(backupUser.birthday(), formatter): null;

        var user = new User(
                backupUser.id(),
                backupUser.username(),
                backupUser.password(),
                backupUser.firstname(),
                backupUser.lastname(),
                created.toLocalDate(),
                backupUser.enabled(),
                backupUser.accountExpired(),
                backupUser.accountLocked(),
                bday.toLocalDate(),
                backupUser.uuid()
        );
        if (backupUser.firstname().equals("Chiller")){
            return Mono.empty();
        } else {
            return userService.restore(user);
        }
    }

    @Override
    public Mono<Role> restoreRole(BackupRole backupRole) {

        // TODO: add decryption/type-conversion logic after initial data restore.
        // TODO: add check to see if record exists, or is more current than backup.
        // temp

        var role = new Role(
                backupRole.id(),
                backupRole.role(),
                backupRole.title(),
                backupRole.description()
        );
        return roleService.restore(role);
    }

    @Override
    public Mono<UserRole> restoreUserrole(BackupUserrole backupUserrole) {

        // TODO: add decryption/type-conversion logic after initial data restore.
        // TODO: add check to see if record exists, or is more current than backup.
        // temp

        return userService
                .getUserById(backupUserrole.userId())
                .zipWith(roleService.getById(backupUserrole.roleId()))
                .flatMap(objects -> userRoleRepository.save(new UserRole(backupUserrole.id(), objects.getT1(), objects.getT2())));
    }
}
