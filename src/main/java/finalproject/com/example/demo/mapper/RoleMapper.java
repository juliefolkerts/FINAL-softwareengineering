package finalproject.com.example.demo.mapper;

import finalproject.com.example.demo.dto.role.RoleRequest;
import finalproject.com.example.demo.dto.role.RoleResponse;
import finalproject.com.example.demo.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toEntity(RoleRequest request);

    RoleResponse toResponse(Role entity);

    List<RoleResponse> toResponse(List<Role> entities);
}
