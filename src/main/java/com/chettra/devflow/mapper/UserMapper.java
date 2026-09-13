package com.chettra.devflow.mapper;

import com.chettra.devflow.dto.UserRequest;
import com.chettra.devflow.dto.UserResponse;
import com.chettra.devflow.entity.User;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper( componentModel = "spring")
public interface UserMapper {

    UserResponse mapToResponse(User user);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    User mapToEntity(UserRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    void updateEntity(UserRequest request, @MappingTarget User user);
}
