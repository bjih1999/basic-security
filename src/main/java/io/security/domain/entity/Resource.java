package io.security.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue
    private Long id;

    private String resourceName;

    private String httpMethod;

    private int orderNum;

    private String resourceType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_resources", joinColumns = {
            @JoinColumn(name = "resource_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id")
    })
    private Set<Role> roleSet = new HashSet<>();
}
