package br.com.microsservice.statelessauthapi.core.repository;

import br.com.microsservice.statelessauthapi.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username); // encapsula um objeto interno

}
