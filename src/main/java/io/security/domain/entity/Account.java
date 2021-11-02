package io.security.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue
    private Long Id;

    private String username;

    private String password;

    private String email;

    private String age;

    @ManyToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinTable(name = "account_roles", joinColumns = { @JoinColumn(name = "account_id") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id") })
    private Set<Role> userRoles = new HashSet<>();
}
