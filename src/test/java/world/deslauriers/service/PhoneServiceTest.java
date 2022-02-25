package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.UserPhone;
import world.deslauriers.repository.PhoneRepository;
import world.deslauriers.repository.UserPhoneRepository;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.service.constants.PhoneType;

import java.util.*;

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
    void testPhoneServiceMethods(){

        var user = userRepository.findByUsername(VALID_USER).get();
        var userPhones = userPhoneRepository.findByUser(user);
        var phones = new HashSet<Phone>(List.of(userPhones.iterator().next().phone()));
        System.out.println(phones);
        // do nothing if no changes
        phoneService.resolvePhones(phones, user);
        userPhones = userPhoneRepository.findByUser(user);
        assertEquals(1, userPhones.size());
        assertTrue(userPhones.stream().anyMatch(userPhone -> {
            assert userPhone.phone() != null;
            return Objects.equals(userPhone.phone().phone(), VALID_ASSOCIATED_PHONE);
        }));
        assertTrue(userPhones.stream().anyMatch(userPhone -> {
            assert userPhone.phone() != null;
            return Objects.equals(userPhone.phone().type(), "CELL");
        }));

        // edit number(s) if does exist
        var current = phones.iterator().next(); // get current
        phones.clear();
        phones.add(new Phone(current.id(), "1112224444", current.type()));
        phoneService.resolvePhones(phones, user);
        userPhones = userPhoneRepository.findByUser(user);
        assertEquals(1, userPhones.size());
        assertTrue(userPhones.stream().anyMatch(userPhone -> {
            assert userPhone.phone() != null;
            return Objects.equals(userPhone.phone().phone(), "1112224444");
        }));
        assertTrue(userPhones.stream().anyMatch(userPhone -> {
            assert userPhone.phone() != null;
            return Objects.equals(userPhone.phone().type(), "CELL");
        }));

        // cannot add duplicate phone number
        phones.add(new Phone("1112224444", "WORK"));
        assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(phones, user);
        });

        // add new if different from existing
        // look up before add new phone to db
        phones.clear();
        userPhones = userPhoneRepository.findByUser(user);
        phones.add(userPhones.stream().filter(userPhone -> userPhone.phone().phone().equals("1112224444")).findFirst().get().phone());
        phones.add(new Phone(VALID_UNASSOCIATED_PHONE, PhoneType.WORK.toString()));
        phoneService.resolvePhones(phones, user);
        userPhones = userPhoneRepository.findByUser(user);
        assertEquals(2, userPhones.size());
        assertTrue(userPhones.stream().anyMatch(userPhone -> {
            assert userPhone.phone() != null;
            return Objects.equals(userPhone.phone().phone(), "1112224444");
        }));
        assertTrue(userPhones.stream().anyMatch(userPhone -> {
            assert userPhone.phone() != null;
            return Objects.equals(userPhone.phone().phone(), VALID_UNASSOCIATED_PHONE);
        }));

        // only one of each type: cell, work, home
        phones.add(new Phone(VALID_NEW_PHONE, "CELL"));
        var thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(phones, user);
        });
        assertEquals("Can only enter one of each type of phone.", thrown.getMessage());

        // add up to three numbers

        phones.clear();
        phones.add(new Phone(VALID_NEW_PHONE, "HOME"));
        phoneService.resolvePhones(phones, user);
        userPhones = userPhoneRepository.findByUser(user);
        assertEquals(3, userPhones.size());
        phones.clear();
        phones.add(new Phone("1231231234", "HOME"));
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(phones, user);
        });
        assertEquals("Only 3 phone numbers allowed.", thrown.getMessage());

        // phone types must be valid
        userPhoneRepository.findByUser(user).forEach(userPhone -> userPhoneRepository.delete(userPhone));
        assertEquals(0, userPhoneRepository.findByUser(user).size());
        phones.add(new Phone(VALID_NEW_PHONE, "FOUR"));
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            phoneService.resolvePhones(phones, user);
        });
        assertEquals("Invalid phone type: FOUR", thrown.getMessage());

        // add if no phone exists
        userPhoneRepository.findByUser(user).forEach(userPhone -> userPhoneRepository.delete(userPhone));
        assertEquals(0, userPhoneRepository.findByUser(user).size());
        phones.clear();
        phones.add(new Phone(VALID_NEW_PHONE, "HOME"));
        phoneService.resolvePhones(phones, user);
        assertEquals(1, userPhoneRepository.findByUser(user).size());
    }

    @Test
    void testRoughDraft(){

        var user = userRepository.findByUsername(VALID_USER).get();
        var current = userPhoneRepository.findByUser(user);
        var currentCount = current.spliterator().getExactSizeIfKnown();
        assertEquals(1, currentCount);

        var associate = userPhoneRepository.save(
                new UserPhone(user, phoneRepository.findByPhone(VALID_UNASSOCIATED_PHONE).get()));
        current = userPhoneRepository.findByUser(userRepository.findByUsername(VALID_USER).get());
        assertEquals(2, current.spliterator().getExactSizeIfKnown());

        var types = new HashSet<String>();
        current.forEach(userPhone -> types.add(userPhone.phone().type()));
        assertEquals(2, types.size());

        associate = userPhoneRepository.save(
                new UserPhone(user, phoneRepository.save(new Phone("7778889999", "FOOD"))));
        current = userPhoneRepository.findByUser(userRepository.findByUsername(VALID_USER).get());
        assertEquals(3, current.spliterator().getExactSizeIfKnown());

        current.forEach(userPhone -> types.add(userPhone.phone().type()));
        assertEquals(3, types.size());

        assertEquals("WORK".toUpperCase(), PhoneType.WORK.toString());

    }
}
