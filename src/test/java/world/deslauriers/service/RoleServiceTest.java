package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Role;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class RoleServiceTest {

    @Inject
    private RoleService roleService;

    private static final String VALID_ROLE = "GENERAL_ADMISSION";
    private static final String INVALID_ROLE = "FAKE_ROLE";

    @Test
    void testRoleServiceMethods(){

        roleService.save(new Role(VALID_ROLE));

        var role = roleService.getRole(VALID_ROLE);
        assertTrue(role.isPresent());
        assertNotNull(role.get().id());
        assertEquals(VALID_ROLE, role.get().role());

        role = roleService.getRole(INVALID_ROLE);
        assertTrue(role.isEmpty());
    }
}
