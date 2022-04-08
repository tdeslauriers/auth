package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserPhone;
import world.deslauriers.repository.PhoneRepository;
import world.deslauriers.repository.UserPhoneRepository;
import world.deslauriers.service.constants.PhoneType;

import java.util.HashSet;
import java.util.stream.Collectors;

@Singleton
public class PhoneServiceImpl implements PhoneService {

    private static final Logger log = LoggerFactory.getLogger(PhoneServiceImpl.class);

    @Inject
    private final PhoneRepository phoneRepository;

    @Inject
    private final UserPhoneRepository userPhoneRepository;

    public PhoneServiceImpl(PhoneRepository phoneRepository, UserPhoneRepository userPhoneRepository) {
        this.phoneRepository = phoneRepository;
        this.userPhoneRepository = userPhoneRepository;
    }

    @Override
    public void resolvePhones(HashSet<Phone> phones, User user) {

        // prep
        var add = phones
                .stream()
                .filter(phone -> phone.id() == null)
                .collect(Collectors.toCollection(HashSet::new));

        var edit = phones
                .stream()
                .filter(phone -> phone.id() != null)
                .filter(phone -> user.userPhones().stream().anyMatch(userPhone -> userPhone.phone().id().equals(phone.id())))
                .collect(Collectors.toCollection(HashSet::new));

        var remove = user.userPhones()
                .stream()
                .filter(userPhone -> phones.stream().filter(phone -> phone.id() != null).noneMatch(phone -> phone.id().equals(userPhone.phone().id())))
                .collect(Collectors.toCollection(HashSet::new));

        // only 3 allowed
        if (add.size() > 3 ||
            edit.size() > 3 ||
            add.size() + edit.size() > 3) {
            log.error("Attempt to add more than 3 numbers to record");
            throw new IllegalArgumentException("Only 3 phone numbers allowed.");
        }

        // cannot enter duplicate numbers
        var numbers = new HashSet<String>();
        add.forEach(phone -> numbers.add(phone.phone()));
        edit.forEach(phone -> numbers.add(phone.phone()));
        if(add.size() + edit.size() > numbers.size()) {
            log.error("Attempt to enter duplicate phone numbers");
            throw new IllegalArgumentException("Cannot add duplicate phone numbers.");
        }

        var types = new HashSet<String>();
        add.forEach(phone -> types.add(phone.type()));
        edit.forEach(phone -> types.add(phone.type()));

        // unique phone types
        if (add.size() + edit.size() > types.size()) {
            log.error("Attempt to enter duplicate phone type");
            throw new IllegalArgumentException("Can only enter one of each type of phone.");
        }

        // correct type check
        types.forEach(type -> {
            var valid = false;
            for (PhoneType pt: PhoneType.values()) if (type.toUpperCase().equals(pt.toString())) valid = true;
            if (!valid){
                log.error("Attempt to enter invalid type: " + type);
                throw new IllegalArgumentException("Invalid phone type: " + type);
            }
        });

        // update database
        var resolved = new HashSet<Phone>();

        // add
        add.forEach(phone -> {
            var associated = userPhoneRepository.save(new UserPhone(user, phoneRepository.save(phone)));
            log.info("Associated new phone " + associated.phone() + " to user " + associated.user().username());
            resolved.add(associated.phone());
        });

        // edit
        edit.stream().filter(phone -> phone.id() != null).forEach(phone -> {
            var previous = user.userPhones()
                    .stream()
                    .filter(userPhone -> userPhone.phone().id().equals(phone.id()))
                    .findFirst().get().phone();

            if (!previous.phone().equals(phone.phone()) ||
                    !previous.type().equals(phone.type())){

                var updated = phoneRepository.update(phone);
                log.info("Updating " + user.username() + "'s phone " + previous + " to " + updated);
                resolved.add(updated);
            }
        });

        // remove
        remove.forEach(userPhone -> {
            userPhoneRepository.delete(userPhone);
            log.info("Deleted association between user " + user.username() + " and phone " + userPhone.phone());
            phoneRepository.delete(userPhone.phone());
            log.info("Deleted " + userPhone.phone());
        });
    }

}