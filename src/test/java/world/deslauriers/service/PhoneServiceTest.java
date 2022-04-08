package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Phone;
import world.deslauriers.repository.PhoneRepository;
import world.deslauriers.repository.UserPhoneRepository;
import world.deslauriers.repository.UserRepository;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class PhoneServiceTest {

    @Inject private PhoneService phoneService;
    @Inject private PhoneRepository phoneRepository;
    @Inject private UserPhoneRepository userPhoneRepository;
    @Inject private UserRepository userRepository;

    // from test data
    private static final String VALID_USER = "admin@deslauriers.world";
    private static final String VALID_ASSOCIATED_PHONE = "1112223333";
    private static final String VALID_UNASSOCIATED_PHONE = "4445556666";
    private static final String VALID_NEW_PHONE = "77788899999";

    @Test
    void testPhoneServiceMethods() {

        var user = userRepository.findByUsername(VALID_USER).get();
        var current = user.userPhones();

        // cannot edit/add to invalid phone type
        var badType = new Phone("5556667777", "MONKEY");
        var updated = new HashSet<Phone>();
        current.forEach(userPhone -> updated.add(userPhone.phone()));
        updated.add(badType);
        var thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());
        });
        assertEquals("Invalid phone type: MONKEY", thrown.getMessage());

        // clean up
        updated.remove(badType);

        // cannot add duplicates or two of same number
        var duplicate = new Phone(VALID_ASSOCIATED_PHONE, "HOME");
        updated.add(duplicate);
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());
        });
        assertEquals("Cannot add duplicate phone numbers.", thrown.getMessage());

        // clean up
        updated.remove(duplicate);

        // cannot update numbers user does not own.
        // will be dropped by filtering
        var notOwned = phoneRepository.findByPhone(VALID_UNASSOCIATED_PHONE).get();
        // edit not owned
        var notOwnedEdit = new Phone(notOwned.id(), "9998882222", notOwned.type());
        updated.add(notOwnedEdit);
        phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());
        assertEquals(notOwned.phone(), phoneRepository.findByPhone(VALID_UNASSOCIATED_PHONE).get().phone());

        // clean up
        updated.remove(notOwnedEdit);

        // happy path > user owns edited record
        updated.clear();
        var legit = new Phone(
                user.userPhones()
                        .stream()
                        .filter(userPhone -> userPhone.phone().phone().equals(VALID_ASSOCIATED_PHONE))
                        .findFirst().get().id(),
                "2223334444",
                "HOME");
        updated.add(legit);
        phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());

        // happy path: user has less than three numbers.
        updated.add(new Phone("7778889999", "CELL"));
        updated.add(new Phone("3334445555", "WORK"));
        phoneService.resolvePhones(updated, user);
        assertEquals(3, userRepository.findByUsername(VALID_USER).get().userPhones().size());

        // must not be able to add more than 3 phones.
        updated.clear();
        userRepository.findByUsername(VALID_USER).get().userPhones().forEach(userPhone -> updated.add(userPhone.phone()));
        updated.add(new Phone("9998887777", "CELL"));
        assertTrue(updated.size() > 3);
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());
        });
        assertEquals("Only 3 phone numbers allowed.", thrown.getMessage());

        // must not be allowed to add more than one of each type.
        var remove = userRepository
                .findByUsername(VALID_USER).get()
                .userPhones()
                .stream()
                .filter(userPhone -> userPhone.phone().type().equals("WORK"))
                .findFirst().get();
        userPhoneRepository.delete(remove);
        assertEquals(2, userRepository.findByUsername(VALID_USER).get().userPhones().size());

        // must not enter duplicate types.
        updated.clear();
        updated.add(new Phone("6668889999", "CELL"));
        updated.add(new Phone("5553334444", "CELL"));
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());
        });
        assertEquals("Can only enter one of each type of phone.", thrown.getMessage());

        // deletion must occur when number not included in resolution payload
        updated.clear();
        userRepository.findByUsername(VALID_USER).get().userPhones().forEach(userPhone -> updated.add(userPhone.phone()));
        var deleted = userRepository.findByUsername(VALID_USER).get().userPhones()
                .stream()
                .filter(userPhone -> userPhone.phone().type().equals("CELL"))
                .findFirst().get().phone();
        updated.remove(deleted);
        phoneService.resolvePhones(updated, userRepository.findByUsername(VALID_USER).get());
        var removed = userRepository.findByUsername(VALID_USER).get().userPhones()
                .stream()
                .noneMatch(userPhone -> userPhone.phone().id().equals(deleted.id()));
        assertTrue(removed);
        userRepository.findByUsername(VALID_USER).get().userPhones().forEach(userPhone -> System.out.println(userPhone.phone()));
    }

}
