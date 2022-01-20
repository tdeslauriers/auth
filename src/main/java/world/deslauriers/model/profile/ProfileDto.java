package world.deslauriers.model.profile;

import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;

import java.util.HashSet;

public record ProfileDto(
        String username,
        String firstname,
        String lastname,
        HashSet<Address> addresses,
        HashSet<Phone> phones
) {}
