package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class UserServiceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleService roleService;

    @Inject
    private UserRoleRepository userRoleRepository;

    @Inject
    private PasswordEncoderService passwordEncoderService;

    @Inject
    private UserService userService;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "H~Z\\ysbY[fOg|4^86:BQ";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";
    private static final String VALID_ROLE_1 = "GALLERY_READ";
    private static final String VALID_ROLE_2 = "GALLERY_EDIT";

    @Test
    void testUserServiceMethods(){

        var user = userRepository.save(new User(
                VALID_EMAIL, passwordEncoderService.encode(VALID_CLEAR_PASSWORD), VALID_FIRST, VALID_LAST, LocalDate.now(), true, false, false));

        var ur1 = userRoleRepository.save(new UserRole(user, roleService.save(new Role(VALID_ROLE_1))));
        var ur2 = userRoleRepository.save(new UserRole(user, roleService.save(new Role(VALID_ROLE_2))));

        user = userService.lookupUserByUsername(user.username()).get();
        assertNotNull(user);
        assertEquals(VALID_EMAIL, user.username());
        assertEquals(2, user.userRoles().size());


        // admin profile update tests:
        // method will throw if field validation fails.
        // id not in db
        userService.updateUser(new ProfileDto(
                4444L,
                user.username(),
                user.firstname(),
                user.lastname(),
                user.dateCreated(),
                user.enabled(),
                user.accountExpired(),
                user.accountLocked(),
                null,
                null));

        // field changes
        userService.updateUser(new ProfileDto(
                user.id(),
                user.username(),
                VALID_FIRST,
                "007",
                user.dateCreated(),
                false,
                user.accountExpired(),
                user.accountLocked(),
                null,
                null));

        var updated = userService.lookupUserByUsername(user.username()).get();
        assertNotNull(updated.id());
        assertEquals(user.id(), updated.id());
        assertEquals(user.username(), updated.username());
        assertEquals(user.firstname(), updated.firstname());
        assertEquals("007", updated.lastname());
        assertFalse(updated.enabled());
        assertFalse(updated.accountExpired());
        assertFalse(updated.accountLocked());
        System.out.println(updated);

        var all = userRepository.findAllUsers();
        assertNotNull(all);
        assertTrue(all.iterator().hasNext());
        assertNotNull(all.iterator().next().id());
        assertEquals(all.iterator().next().firstname(), VALID_FIRST);

    }

}
