package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.repository.RoleRepository;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
public class UserServiceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private UserRoleRepository userRoleRepository;

    @Inject
    private PasswordEncoderService passwordEncoderService;

    @Inject
    private UserService userService;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "Worst_password_ever!";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";
    private static final String VALID_ROLE_1 = "GALLERY_READ";
    private static final String VALID_ROLE_2 = "GALLERY_EDIT";

    @Test
    void testUserServiceMethods(){

        var user = userRepository.save(new User(
                VALID_EMAIL, passwordEncoderService.encode(VALID_CLEAR_PASSWORD), VALID_FIRST, VALID_LAST, LocalDate.now(), true, false, false));

        var ur1 = userRoleRepository.save(new UserRole(user, roleRepository.save(new Role(VALID_ROLE_1))));
        var ur2 = userRoleRepository.save(new UserRole(user, roleRepository.save(new Role(VALID_ROLE_2))));

        user = userService.lookupUserByUsername(user.username()).get();
        assertNotNull(user);
        assertEquals(VALID_EMAIL, user.username());
        assertEquals(2, user.userRoles().size());

        var all = userRepository.findAllUsers();
        all.forEach(System.out::println);
    }

}
