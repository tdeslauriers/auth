package world.deslauriers.service;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.*;
import world.deslauriers.model.dto.ProfileDto;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.registration.RegistrationResponseDto;
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
    private final RoleService roleService;
    private final PasswordEncoderService passwordEncoderService;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleService roleService, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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
}
