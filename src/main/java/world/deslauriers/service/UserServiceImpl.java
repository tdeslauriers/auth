package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.profile.UserDto;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDate;
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
    public Iterable<UserDto> getAllUsers(){

        return userRepository.findAllUsers();
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
    public void updateUser(ProfileDto updatedProfile) {

        var sb = new StringBuilder();
        var user = userRepository.findById(updatedProfile.id());

        if (user.isEmpty()){
            log.error("Attempt to edit invalid User Id: " + updatedProfile.id() + " - does not exist.");
            throw new IllegalArgumentException("Invalid user Id.");
        }

        try {
            // logging is placeholder for updating user field history table
            // update username
            if (!updatedProfile.username().equals(user.get().username())){
                sb.append(user.get().username()).append(" --> ").append(updatedProfile.username()).append("\n");
            }

            // update firstname
            if (!updatedProfile.firstname().equals(user.get().firstname())){
                sb.append(user.get().firstname()).append(" --> ").append(updatedProfile.firstname()).append("\n");
            }

            // update lastname
            if (!updatedProfile.lastname().equals(user.get().lastname())){
                sb.append(user.get().lastname()).append(" --> ").append(updatedProfile.lastname()).append("\n");
            }

            // enabled?
            if (!updatedProfile.enabled().equals(user.get().enabled())){
                sb.append("Enabled: ").append(user.get().enabled()).append(" --> ").append(updatedProfile.enabled()).append("\n");
            }

            // account expired?
            if (!updatedProfile.accountExpired().equals(user.get().accountExpired())){
                sb.append("Account expired: ").append(user.get().accountExpired()).append(" --> ").append(updatedProfile.accountExpired()).append("\n");
            }

            // account locked?
            if (!updatedProfile.accountLocked().equals(user.get().accountLocked())){
                sb.append("Account locked: ").append(user.get().accountLocked()).append(" --> ").append(updatedProfile.accountLocked()).append("\n");
            }

            if (sb.length() > 0) {
                var updated = userRepository.update(new User(
                        user.get().id(),
                        updatedProfile.username(),
                        user.get().password(),
                        updatedProfile.firstname(),
                        updatedProfile.lastname(),
                        user.get().dateCreated(),
                        updatedProfile.enabled(),
                        updatedProfile.accountExpired(),
                        updatedProfile.accountLocked()));
                log.info("\nUpdated UserID " + updated.id() + ":\n" + sb);
            }

            if (updatedProfile.addresses() != null){
                addressService.resolveAddresses(updatedProfile.addresses(), user.get());
            }

            if (updatedProfile.phones() != null){
                phoneService.resolvePhones(updatedProfile.phones(), user.get());
            }

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private ProfileDto buildProfile(User user){

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
                addresses,
                phones);
    }
}
