package com.codewithkelvin.store.repositories;

import com.codewithkelvin.store.users.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository <Profile, Long> {
}