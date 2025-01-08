package com.estudo.microservice.userservice.mapper;

import com.estudo.microservice.userservice.entity.User;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
import models.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE,
        nullValueCheckStrategy = ALWAYS
)
public interface UserMapper {

    UserResponse fromEntity(User user);

    @Mapping(target = "id", ignore = true)
    User fromRequest(CreateUserRequest createUserRequest);

    @Mapping(target = "id", ignore = true)
    User update(UpdateUserRequest updateUserRequest, @MappingTarget User entity);
}
