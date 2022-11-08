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

    private static final String VALID_ROLE_1 = "ALLOWANCE_EARNER";
    private static final String VALID_ROLE_2 = "ALLOWANCE_REMITTER";
    private static final String VALID_TITLE = "Allowance user";
    private static final String VALID_DESCRIPTION = "View and Marks tasks as complete.";

    @Test
    void testRoleCrud(){

        var role = roleRepository.save(new Role(VALID_ROLE_1, VALID_TITLE, VALID_DESCRIPTION));
        assertNotNull(role.id());

        var updated = roleRepository.update(new Role(role.id(), VALID_ROLE_2, VALID_TITLE, VALID_DESCRIPTION));
        assertEquals(role.id(), updated.id());
        assertEquals(VALID_ROLE_2, updated.role());

    }
}
