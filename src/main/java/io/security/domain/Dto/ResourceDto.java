package io.security.domain.Dto;

import io.security.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto{

    private String id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private Set<Role> roleSet;

}