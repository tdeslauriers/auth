package world.deslauriers.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Role;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class RoleDaoTest {

    @Inject
    private RoleRepository roleRepository;

    private static final String VALID_ROLE_1 = "GALLERY_READ";
    private static final String VALID_ROLE_2 = "GALLERY_EDIT";
    private static final String VALID_TITLE = "Gallery Read";
    private static final String VALID_DESCRIPTION = "Navigate albums and view gallery content.";

    @Test
    void testRoleCrud(){

        var role = roleRepository.save(new Role(VALID_ROLE_1, VALID_TITLE, VALID_DESCRIPTION));
        assertNotNull(role.id());

        var updated = roleRepository.update(new Role(role.id(), VALID_ROLE_2, VALID_TITLE, VALID_DESCRIPTION));
        assertEquals(role.id(), updated.id());
        assertEquals(VALID_ROLE_2, updated.role());

    }
}
