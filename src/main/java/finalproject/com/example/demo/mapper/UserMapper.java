package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.user.AdminCreateUserRequest;
import finalproject.com.example.demo.dto.user.UserResponse;
import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blocked", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(AdminCreateUserRequest request);

    default List<String> mapRoles(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getRole)
                .collect(Collectors.toList());
    }
}






//package finalproject.com.example.demo.mapper;
//
//import finalproject.com.example.demo.dto.user.AdminCreateUserRequest;
//import finalproject.com.example.demo.dto.user.UserResponse;
//import finalproject.com.example.demo.entity.Role;
//import finalproject.com.example.demo.entity.User;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
//
//    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
//    UserResponse toResponse(User user);
//
//    List<UserResponse> toResponse(List<User> users);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "blocked", constant = "false")
//    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "roles", ignore = true)
//    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "orders", ignore = true)
//    @Mapping(target = "reviews", ignore = true)
//    User toEntity(AdminCreateUserRequest request);
//
//    default List<String> mapRoles(List<Role> roles) {
//        if (roles == null) {
//            return null;
//        }
//        return roles.stream().map(Role::getRole).collect(Collectors.toList());
//    }
//}
