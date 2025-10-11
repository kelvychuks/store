package com.codewithkelvin.store.mappers;

import com.codewithkelvin.store.dtos.RegisterUserRequest;
import com.codewithkelvin.store.dtos.UpdateUserRequest;
import com.codewithkelvin.store.users.UserDto;
import com.codewithkelvin.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest request, @MappingTarget User user);
}
