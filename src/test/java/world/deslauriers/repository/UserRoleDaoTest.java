package world.deslauriers.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.service.RoleService;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class UserRoleDaoTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleService roleService;

    @Inject
    private UserRoleRepository userRoleRepository;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "3rd_Worst_password_ever!";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";
    private static final String VALID_ROLE_1 = "ALLOWANCE_EARNER";
    private static final String VALID_ROLE_2 = "ALLOWANCE_REMITTER";
    private static final String VALID_ROLE_TITLE = "Allowance user";
    private static final String VALID_ROLE_DESCRIPTION = "View and Marks tasks as complete.";



    @Test
    void testUserRoleCrud(){

        // db records must exist prior to association in xref
        var role = roleService.save(new Role(VALID_ROLE_1, VALID_ROLE_TITLE, VALID_ROLE_DESCRIPTION));
        var user = userRepository.save(new User(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST, LocalDate.now(), true, false, false));

        var ur = userRoleRepository.save(new UserRole(user, role));
        assertNotNull(ur.id());
        assertEquals(user, ur.user());
        assertEquals(role, ur.role());

        // test join
        user = userRepository.findByUsername(user.username()).get();
        assertNotNull(user);
        assertNotNull(user.userRoles());
        assertEquals(ur.id(), user.userRoles().stream().findFirst().get().id());
        assertNotNull(user.userRoles().stream().findFirst().get().role());
        assertEquals(VALID_ROLE_1, user.userRoles().stream().findFirst().get().role().role());

        // test add xref join via user record update
        var ur2 = new UserRole(user, roleService.save(new Role(VALID_ROLE_2, VALID_ROLE_TITLE, VALID_ROLE_DESCRIPTION)));
        ur2 = userRoleRepository.save(ur2);

        // id present, but user and roles null???
        var userrole = userRoleRepository.findByUserAndRole(user, role);
        System.out.println(userrole);



    }
}
