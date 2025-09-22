package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    // Spring Data JPA will automatically implement this method for us
    // It's useful for the security layer to find a user by their username
    Optional<Usuario> findByUsername(String username);

    // You can add more custom query methods here as needed

}
