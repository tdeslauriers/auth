package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Singleton
public class UserServiceImpl implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    private final UserRepository userRepository;

    @Inject
    private final PasswordEncoderService passwordEncoderService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Optional<User> lookupUserByUsername(String email) {

        return userRepository.findByUsername(email);
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
        log.info("Registration successful; User ID: " + user.id().toString());
        message.append("Registration successful"); // placeholder for email verification service.

        return message.toString();
    }

    @Override
    public Optional<ProfileDto> getProfile(String username){

        // user is present because came from jwt, login is user check
        var user = lookupUserByUsername(username);
        if (user.isPresent()){

            HashSet<Address> addresses = new HashSet<>();
            if (user.get().userAddresses() != null){

                user.get().userAddresses().forEach(userAddress -> {
                    var address = new Address(
                            userAddress.address().id(),
                            userAddress.address().address(),
                            userAddress.address().city(),
                            userAddress.address().state(),
                            userAddress.address().zip()
                    );
                    addresses.add(address);
                });
            }

            HashSet<Phone> phones = new HashSet<>();
            if (user.get().userPhones() != null){

                user.get().userPhones().forEach(userPhone -> {
                    var phone = new Phone(userPhone.phone().id(), userPhone.phone().phone());
                    phones.add(phone);
                });
            }

            return Optional.of(new ProfileDto(
                    user.get().username(),
                    user.get().firstname(),
                    user.get().lastname(),
                    addresses,
                    phones
            ));
        } else {
            return null;
        }
    }
}
