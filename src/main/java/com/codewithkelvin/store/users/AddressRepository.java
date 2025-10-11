package com.codewithkelvin.store.repositories;

import com.codewithkelvin.store.users.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}