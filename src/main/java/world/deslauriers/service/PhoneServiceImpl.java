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
        var toResolve = new HashSet<Phone>();
        phones.stream().filter(phone -> phone.id() == null).forEach(phone -> toResolve.add(phone));
        phones.stream().filter(phone -> phone.id() != null).forEach(phone -> {
            if (user.userPhones().stream().noneMatch(userPhone -> userPhone.phone().id().equals(phone.id()))){
                log.error("Attempt to edit phone record user does not own: " + phone);
                throw new IllegalArgumentException("Cannot edit phone record user does not own.");
            }
            toResolve.add(phone);
        });
        user.userPhones().forEach(userPhone -> {
            if (toResolve
                    .stream()
                    .filter(phone -> phone.id() != null)
                    .noneMatch(phone -> phone.id().equals(userPhone.id()))){

                toResolve.add(userPhone.phone());
            }
        });

        // only 3 allowed
        if (toResolve.size() > 3) {
            log.error("Attempt to add more than 3 numbers to record");
            throw new IllegalArgumentException("Only 3 phone numbers allowed.");
        }

        // cannot enter duplicate numbers
        var numbers = new HashSet<String>();
        toResolve.forEach(phone -> numbers.add(phone.phone()));
        if(numbers.size() < toResolve.size()) {
            log.error("Attempt to enter duplicate phone numbers");
            throw new IllegalArgumentException("Cannot add duplicate phone numbers.");
        }

        var types = new HashSet<String>();
        toResolve.forEach(phone -> types.add(phone.type()));
        user.userPhones().forEach(userPhone -> types.add(userPhone.phone().type()));

        // correct type check
        types.forEach(type -> {
            var valid = false;
            for (PhoneType pt: PhoneType.values()) if (type.toUpperCase().equals(pt.toString())) valid = true;
            if (!valid){
                log.error("Attempt to enter invalid type: " + type);
                throw new IllegalArgumentException("Invalid phone type: " + type);
            }
        });

        // unique phone types
        if (types.size() < toResolve.size()) {
            log.error("Attempt to enter duplicate phone type");
            throw new IllegalArgumentException("Can only enter one of each type of phone.");
        }

        // edit
        toResolve.stream().filter(phone -> phone.id() != null).forEach(phone -> {
            var previous = user.userPhones()
                    .stream()
                    .filter(userPhone -> userPhone.phone().id().equals(phone.id()))
                    .findFirst().get().phone();
            if (!previous.equals(phone)){
                var updated = phoneRepository.update(phone);
                log.info("Updating " + user.username() + "'s phone " + previous + " to " + updated);
            }
        });

        // add
        var total = user.userPhones().size();
        var toAdd = toResolve.stream().filter(phone -> phone.id() == null).toList();
        for (Phone p : toAdd) {
            if (total >= 3){
                log.error("Attempt to add more than three phones");
                throw new IllegalArgumentException("Cannot add more than three phones.");
            }
            var added = phoneRepository.save(p);
            var associated = userPhoneRepository.save(new UserPhone(user, added));
            log.info("Associated new phone " + associated.phone() + " to user " + associated.user().username());
            total++;
        }
    }

}