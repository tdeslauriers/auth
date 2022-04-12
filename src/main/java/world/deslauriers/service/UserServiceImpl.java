package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.*;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Singleton
public class UserServiceImpl implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    private final UserRepository userRepository;

    @Inject
    private final UserRoleRepository userRoleRepository;

    @Inject
    private final RoleService roleService;

    @Inject
    private final AddressService addressService;

    @Inject
    private final PhoneService phoneService;

    @Inject
    private final PasswordEncoderService passwordEncoderService;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleService roleService, AddressService addressService, PhoneService phoneService, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.addressService = addressService;
        this.phoneService = phoneService;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Boolean userExists(String email){
        return userRepository.findUsername(email).isPresent();
    }

    @Override
    public Optional<User> lookupUserByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    @Override
    public Optional<User> lookupUserById(Long id){
        return userRepository.findById(id);
    }

    @Override
    public Iterable<ProfileDto> getAllUsers(){

        var users = userRepository.findAll();
        var profiles = new ArrayList<ProfileDto>((int) users.spliterator().getExactSizeIfKnown());
        users.forEach(user -> { profiles.add(buildProfile(user)); });

       return profiles;
    }

    @Override
    public String registerUser(RegistrationDto registrationDto){

        var message = new StringBuilder();
        var user = new User(
                registrationDto.username(),
                passwordEncoderService.encode(registrationDto.password()),
                registrationDto.firstname(),
                registrationDto.lastname(),
                LocalDate.now(),
                true , // set enabled to false when create email verification
                false,
                false);
        user = userRepository.save(user);
        log.info("User Registration successful; ID: " + user.id().toString() + " - Username: " + user.username());

        // add default role/scope
        userRoleRepository.save(new UserRole(user, roleService.getRole("GENERAL_ADMISSION").get()));
        log.info("User ID: " + user.id() + " granted scope: GENERAL_ADMISSION.");
        message.append("Registration successful"); // placeholder for email verification service.

        return message.toString();
    }

    // user
    @Override
    public Optional<ProfileDto> getProfile(String username){

        // user is present because came from jwt, login is user check
        var user = lookupUserByUsername(username);
        return user.map(this::buildProfile);
    }

    // admin
    @Override
    public Optional<ProfileDto> getProfileById(Long id){

        var user = userRepository.findById(id);
        return user.map(this::buildProfile);
    }

    @Override
    public Optional<ProfileDto> updateUser(User user, ProfileDto updatedProfile) {

        var sb = new StringBuilder();
        try {
            // logging is placeholder for updating user field history table
            // update username: not allowed, this will be done via email validation build

            // update firstname
            if (!updatedProfile.firstname().equals(user.firstname())){
                sb.append(user.firstname()).append(" --> ").append(updatedProfile.firstname()).append("\n");
            }

            // update lastname
            if (!updatedProfile.lastname().equals(user.lastname())){
                sb.append(user.lastname()).append(" --> ").append(updatedProfile.lastname()).append("\n");
            }

            // enabled?
            if (!updatedProfile.enabled().equals(user.enabled())){
                sb.append("Enabled: ").append(user.enabled()).append(" --> ").append(updatedProfile.enabled()).append("\n");
            }

            // account expired?
            if (!updatedProfile.accountExpired().equals(user.accountExpired())){
                sb.append("Account expired: ").append(user.accountExpired()).append(" --> ").append(updatedProfile.accountExpired()).append("\n");
            }

            // account locked?
            if (!updatedProfile.accountLocked().equals(user.accountLocked())){
                sb.append("Account locked: ").append(user.accountLocked()).append(" --> ").append(updatedProfile.accountLocked()).append("\n");
            }

            if (sb.length() > 0) {
                var updated = userRepository.update(new User(
                        user.id(),
                        updatedProfile.username(),
                        user.password(),
                        updatedProfile.firstname(),
                        updatedProfile.lastname(),
                        user.dateCreated(),
                        updatedProfile.enabled(),
                        updatedProfile.accountExpired(),
                        updatedProfile.accountLocked()));
                log.info("\nUpdated UserID " + updated.id() + ":\n" + sb);
            }

            if (updatedProfile.addresses() != null){
                addressService.resolveAddresses(updatedProfile.addresses(), user);
            }

            if (updatedProfile.phones() != null){
                phoneService.resolvePhones(updatedProfile.phones(), user);
            }

            if (updatedProfile.roles() != null){
                roleService.resolveRoles(updatedProfile.roles(), user);
            }

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e;
        }
        return getProfileById(user.id());
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
                roles,
                addresses,
                phones);
    }
}
