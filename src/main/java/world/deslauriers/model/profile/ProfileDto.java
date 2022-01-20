package world.deslauriers.model.profile;

import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.model.database.UserPhone;

import java.util.Set;

public record ProfileDto(
        String username,
        String firstname,
        String lastname,
        Set<UserAddress> addresses,
        Set<UserPhone> phones
) {}
