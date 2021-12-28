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

    public RoleDaoTest(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Test
    void testRoleCrud(){

        var role = roleRepository.save(new Role("gallery_view"));
        assertNotNull(role.id());

        var updated = roleRepository.update(new Role(role.id(), "gallery_edit"));
        assertEquals(role.id(), updated.id());
        assertEquals("gallery_edit", updated.role());
        assertNull(updated.userRoles());

    }
}
