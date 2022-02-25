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

        // only 3 allowed
        if (phones.size() > 3 ) {
            log.error("Attempt to add more than 3 numbers");
            throw new IllegalArgumentException("Only 3 phone numbers allowed.");
        }

        // unique phone types
        var types = new HashSet<String>();
        phones.forEach(phone -> types.add(phone.type()));
        if (types.size() < phones.size()) {
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

        var current = userPhoneRepository.findByUser(user);
        var sb = new StringBuilder();
        phones.forEach(phone -> {

            // user has phone(s)
            if (current.iterator().hasNext()) {

                // add additional phone(s)
                if (phone.id() == null) {

                    if (current.size() + 1 > 3) {
                        log.error("Attempt to add more than 3 numbers");
                        throw new IllegalArgumentException("Only 3 phone numbers allowed.");
                    }
                    // no duplicate numbers/types
                    if (isDuplicatePhone(phone, current)) {
                        log.error("Attempt to add duplicate number ( " + phone.phone() + " ) to " + user.username());
                        throw new IllegalArgumentException("Duplicate number.");
                    }
                    if (isDuplicateType(phone, current)) {
                        log.error("Attempt to add duplicate phone type ( " + phone.type() + " ) to " + user.username());
                        throw new IllegalArgumentException("Duplicate type.");
                    }

                    current.add(associatePhone(phone, user));
                    sb.append(" Adding ").append(phone.type().toLowerCase()).append(" phone - ").append(phone.phone());
                }

                // edit existing.
                current.forEach(userPhone -> {
                    var editCount = 0;

                    if (phone.id() != null && phone.id().equals(userPhone.phone().id())){

                        if (!phone.phone().equals(userPhone.phone().phone())){
                            // no duplicate numbers/types
                            if (isDuplicatePhone(phone, current)) {
                                log.error("Attempt to add duplicate number ( " + phone.phone() + " ) to " + user.username());
                                throw new IllegalArgumentException("Duplicate number.");
                            }
                            sb.append( "Updating ").append(phone.type().toLowerCase()).append(" phone: ").append(userPhone.phone().phone()).append(" --> ").append(phone.phone());
                            editCount++;
                        }
                        if (!phone.type().equals(userPhone.phone().type())){

                            if (isDuplicateType(phone, current)) {
                                log.error("Attempt to add duplicate phone type ( " + phone.type() + " ) to " + user.username());
                                throw new IllegalArgumentException("Duplicate type.");
                            }
                            sb.append( "Updating ").append(phone.phone()).append(" type: ").append(userPhone.phone().type().toLowerCase()).append(" --> ").append(phone.type());
                            editCount++;
                        }
                        if (editCount > 0){
                            phoneRepository.update(phone);
                        }
                    }
                });
            }

            // user has no associated phones
            if (!current.iterator().hasNext()) {

                current.add(associatePhone(phone, user));
                sb.append(" Adding ").append(phone.type().toLowerCase()).append(" phone - ").append(phone.phone()).append(" to ").append(user.username()).append("'s profile.");
            }
        });
        if (sb.length() > 0) log.info("Updating " + user.username() + "'s phone(s): " + sb);
    }

    private UserPhone associatePhone(Phone phone, User user) {

        var existing = phoneRepository.findByPhone(phone.phone());
        return existing
                .map(ph -> userPhoneRepository.save(new UserPhone(user, ph)))
                .orElseGet(() -> userPhoneRepository.save(new UserPhone(user, phoneRepository.save(phone))));
    }

    private boolean isDuplicatePhone(Phone phone, Iterable<UserPhone> userPhones) {

        for (UserPhone userPhone : userPhones) {
            assert userPhone.phone() != null;
            if (userPhone.phone().phone().equals(phone.phone())) return true;
        }
        return false;
    }

    private boolean isDuplicateType(Phone phone, Iterable<UserPhone> userPhones){

        for (UserPhone userPhone : userPhones) {
            assert userPhone.phone() != null;
            if (userPhone.phone().type().equals(phone.type())) return true;
        }
        return false;
    }
}