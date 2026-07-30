package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndPassword(String username, String password);
    User findByEmailAndPassword(String email, String password);
    User findUserById(UUID id);
    User findUserByIdAndIsActive(UUID id, boolean active);

    @Query(value = """
        SELECT u FROM User u
        WHERE u.displayName ILIKE concat('%', :keyword, '%') 
        or u.username ILIKE concat('%', :keyword, '%') 
        or u.email ILIKE concat('%', :keyword, '%') 
    """)
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);

    long countByIsActiveTrue();
}
