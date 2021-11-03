package io.security.repository;


import io.security.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);

    @Override
    void delete(Role role);

}