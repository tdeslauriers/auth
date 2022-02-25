package world.deslauriers.service;

import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.User;

import java.util.HashSet;

public interface PhoneService {
    void resolvePhones(HashSet<Phone> phones, User user);
}
