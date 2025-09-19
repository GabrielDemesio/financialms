package financial.repositories;

import financial.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    Optional<UserEntity> findByCpf(String cpf);

    Optional<UserEntity> findByCpfAndActiveTrue(String cpf);

    boolean existsByEmail(String email);

    boolean existsByEmailAndActiveTrue(String email);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndActiveTrue(String cpf);
}
