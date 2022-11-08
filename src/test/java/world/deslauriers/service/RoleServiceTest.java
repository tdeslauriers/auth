package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Role;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class RoleServiceTest {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;


    private static final String VALID_USER = "admin@deslauriers.world";
    private static final String VALID_ROLE = "GENERAL_ADMISSION";
    private static final String VALID_ROLE_TITLE = "General Admission";
    private static final String VALID_ROLE_DESCRIPTION = "Site user.";
    private static final String INVALID_ROLE = "FAKE_ROLE";

    @Test
    void testRoleServiceMethods(){

        var user = userService.lookupUserByUsername(VALID_USER).get();
        var roles = new HashSet<Role>();
        roleService.getAllRoles().forEach(roles::add);
        roleService.resolveRoles(roles, user);

        // must determine which to add from updated set
        assertEquals(6, userService
                .lookupUserByUsername(VALID_USER)
                .get()
                .userRoles()
                .spliterator()
                .getExactSizeIfKnown());
        System.out.println(userService.lookupUserByUsername(VALID_USER).get());
        // must prevent adding same association over and over
        // HashSet type should prevent this anyway.
        // re-adds what is already there.
        roleService.resolveRoles(roles, userService.lookupUserByUsername(VALID_USER).get());
        assertEquals(6, userService
                .lookupUserByUsername(VALID_USER)
                .get()
                .userRoles()
                .spliterator()
                .getExactSizeIfKnown());

        // must lookup added roles to make sure real
        roles.add(new Role(666L, INVALID_ROLE, "Invaid", "Invalid"));
        var thown = assertThrows(IllegalArgumentException.class, () -> {
            roleService.resolveRoles(roles, userService.lookupUserByUsername(VALID_USER).get());
        });
        assertEquals("Role does not exist", thown.getMessage());

        // must determine which user-role associations to remove from updated set
        roles.clear();
        roles.add(roleService.getRole(VALID_ROLE).get());
        roleService.resolveRoles(roles, userService.lookupUserByUsername(VALID_USER).get());
        assertEquals(1, userService
                .lookupUserByUsername(VALID_USER)
                .get()
                .userRoles()
                .spliterator()
                .getExactSizeIfKnown());

        // running it again should try to delete something that isnt there.
        // Nothing should happen.
        roleService.resolveRoles(roles, userService.lookupUserByUsername(VALID_USER).get());
        assertEquals(1, userService
                .lookupUserByUsername(VALID_USER)
                .get()
                .userRoles()
                .spliterator()
                .getExactSizeIfKnown());
    }
}
