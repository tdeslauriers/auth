package world.deslauriers.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.*;
import world.deslauriers.model.dto.ProfileDto;
import world.deslauriers.model.dto.RemoveUserRoleCmd;
import world.deslauriers.model.dto.ResetPasswordCmd;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.registration.RegistrationResponseDto;
import world.deslauriers.repository.PasswordHistoryRepository;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

@Singleton
public class UserServiceImpl implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final RoleService roleService;
    private final PasswordEncoderService passwordEncoderService;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordHistoryRepository passwordHistoryRepository, RoleService roleService, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.roleService = roleService;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Mono<User> getUserByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    @Override
    public Mono<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    @Override
    public Flux<User> backupAll(){
        return userRepository.findAll();
    }

    @Override
    public Flux<ProfileDto> getAllUsers(){
        return userRepository.findAll().map(this::buildProfile);
    }

    @Override
    public Mono<RegistrationResponseDto> registerUser(RegistrationDto registrationDto){

        return userRepository.findByUsername(registrationDto.username())
                .flatMap(existsUser -> Mono.just(( new RegistrationResponseDto(400, "Bad Request", "Username unavailable", "/register"))))
                .switchIfEmpty(Mono.defer(() -> {

                    if (!registrationDto.password().equals(registrationDto.confirmPassword())){
                        return Mono.just(new RegistrationResponseDto(400, "Bad Request", "Passwords do not match.", "/register"));
                    }

                    return userRepository.save(new User(
                                    registrationDto.username(),
                                    passwordEncoderService.encode(registrationDto.password()),
                                    registrationDto.firstname(),
                                    registrationDto.lastname(),
                                    LocalDate.now(),
                                    true , // set enabled to false when create email verification
                                    false,
                                    false,
                                    UUID.randomUUID().toString()))
                            .flatMap(user -> {
                                log.info("User registered: {}", user.username());
                                return roleService.getRole("GENERAL_ADMISSION")
                                        .flatMap(role -> userRoleRepository.save(new UserRole(user, role)));
                            })
                            .flatMap(userRole -> {
                                log.info("Assigned user: {} baseline scope: {}", userRole.user().username(), userRole.role().role());
                                return Mono.just(new RegistrationResponseDto(201, null, "Registration successful: " + userRole.user().username(), "/register"));
                            });
        }));
    }

    // user
    @Override
    public Mono<ProfileDto> getProfile(String username){

        // user is present because came from jwt, login is user check
        var user = getUserByUsername(username);
        return user.map(this::buildProfile);
    }

    // admin
    @Override
    public Mono<ProfileDto> getProfileByUuid(String uuid){
        return userRepository.findByUuid(uuid).map(this::buildProfile);
    }

    @Override
    public Mono<User> updateUser(User user, ProfileDto updatedProfile) {

            // logging is placeholder for updating user field history table
            // update firstname
            if (!updatedProfile.firstname().equals(user.firstname())){
                log.info("Updating {}'s firstname: {} --> {}", user.username(), user.firstname(), updatedProfile.firstname());
            }

            // update lastname
            if (!updatedProfile.lastname().equals(user.lastname())){
                log.info("Updating {}'s lastname: {} --> {}", user.username(), user.lastname(), updatedProfile.lastname());
            }

            // enabled?
            if (!updatedProfile.enabled().equals(user.enabled())){
                log.info("Updating {}'s enabled status: {} --> {}", user.username(), user.enabled(), updatedProfile.enabled());
            }

            // account expired?
            if (!updatedProfile.accountExpired().equals(user.accountExpired())){
                log.info("Updating {}'s expired status: {} --> {}", user.username(), user.accountExpired(), updatedProfile.accountExpired());
            }

            // account locked?
            if (!updatedProfile.accountLocked().equals(user.accountLocked())){
                log.info("Updating {}'s locked status: {} --> {}", user.username(), user.accountLocked(), updatedProfile.accountLocked());
            }

            if (updatedProfile.roles() != null){
                roleService.resolveRoles(updatedProfile.roles(), user);
            }

            return userRepository.update(new User(
                                            user.id(),
                                            updatedProfile.username(),
                                            user.password(),
                                            updatedProfile.firstname(),
                                            updatedProfile.lastname(),
                                            user.dateCreated(),
                                            updatedProfile.enabled(),
                                            updatedProfile.accountExpired(),
                                            updatedProfile.accountLocked(),
                                            updatedProfile.birthday(),
                                            user.uuid()));


//            if (updatedProfile.addresses() != null){
//                addressService.resolveAddresses(updatedProfile.addresses(), user);
//            }
//
//            if (updatedProfile.phones() != null){
//                phoneService.resolvePhones(updatedProfile.phones(), user);
//            }
//

    }

    @Override
    public Mono<PasswordHistory> resetPassword(User user, ResetPasswordCmd cmd) {

        if(!cmd.updated().equals(cmd.confirm())){
            log.error("Password reset attempted where passwords do not match for user {}", user.username());
            return Mono.error(new HttpStatusException(HttpStatus.BAD_REQUEST, "New Passwords do not match"));
        }
        // null checked by @Valid in request body
        // check if password matches
        if (!passwordEncoderService.matches(cmd.current(), user.password())){
            log.error("Password reset attempted with incorrect password for user: {}", user.username());
            return Mono.error(new AuthenticationException(new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH)));
        }
        // make sure not the same as current
        if (cmd.current().equals(cmd.updated())){
            log.error("Password reset attempted where updated password matched a password from history for user {}", user.username());
            return Mono.error(new HttpStatusException(HttpStatus.BAD_REQUEST, "New password cannot match any of the previous 12 iterations."));
        }

        // complexity checked by dto
        return passwordHistoryRepository.findByUser(user)
                        .collectList()
                        .map(passwordHistories -> {
                            return passwordHistories
                                    .stream()
                                    .anyMatch(passwordHistory -> passwordEncoderService.matches(cmd.updated(), passwordHistory.password()));
                        })
                        .flatMap(exists -> {
                            if (exists) {
                                log.warn("Password reset for user {} attempted to use previous password.", user.username());
                                return Mono.error(new HttpStatusException(HttpStatus.BAD_REQUEST, "New password cannot match any of the previous 12 iterations."));
                            }
                            return userRepository.update(new User(
                                    user.id(),
                                    user.username(),
                                    passwordEncoderService.encode(cmd.updated()),
                                    user.firstname(),
                                    user.lastname(),
                                    user.dateCreated(),
                                    user.enabled(),
                                    user.accountExpired(),
                                    user.accountLocked(),
                                    user.birthday(),
                                    user.uuid()));
                        })
                        .flatMap(u -> {
                            log.info("Reset user {} {}'s ({}) password.", u.firstname(), u.lastname(), u.username());
                            return passwordHistoryRepository.save(new PasswordHistory(user.password(), LocalDate.now(), u));
                        });
    }

    private ProfileDto buildProfile(User user){

        var roles = new HashSet<Role>();
        user.userRoles().forEach(userRole -> roles.add(userRole.role()));

        var addresses = new HashSet<Address>();
        user.userAddresses().forEach(userAddress -> addresses.add(userAddress.address()));

        var phones = new HashSet<Phone>();
        user.userPhones().forEach(userPhone -> phones.add(userPhone.phone()));

        return new ProfileDto(
                user.id(),
                user.username(),
                user.firstname(),
                user.lastname(),
                user.dateCreated(),
                user.enabled(),
                user.accountExpired(),
                user.accountLocked(),
                user.birthday(),
                user.uuid(),
                roles,
                addresses,
                phones);
    }

    @Override
    public Mono<Void> removeUserRole(RemoveUserRoleCmd cmd) {

        return userRoleRepository.findByUserIdAndRoleId(cmd.userId(), cmd.roleId())
                .flatMap(userRole -> {
                    log.info("Deleting xref id: {} -- user: {} <--> role: {}", userRole.id(), userRole.user().username(), userRole.role().role());
                    return userRoleRepository.delete(userRole);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Attempted to delete xref that does not exist.");
                    return Mono.empty();
                }))
                .then();
    }
}

