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
    private static final String VALID_ROLE_TITLE = "General Admission";
    private static final String VALID_ROLE_DESCRIPTION = "Site user.";
    private static final String INVALID_ROLE = "FAKE_ROLE";

    @Test
    void testRoleServiceMethods(){

        var role = roleService.save(new Role(VALID_ROLE, VALID_ROLE_TITLE, VALID_ROLE_DESCRIPTION));
        assertNotNull(role.id());
        assertEquals(VALID_ROLE, role.role());
        assertEquals(VALID_ROLE_TITLE, role.title());
        assertEquals(VALID_ROLE_DESCRIPTION, role.description());


    }
}
