package io.security.repository;

import io.security.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {

    public Account findByUsername(String username);
}
