package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.common.enums.VerificationType;
import vn.id.nonglam.kltn.kltn.models.auth.Verification;

import java.util.UUID;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, UUID> {
    Verification getByEmail(String email);
    Verification findByEmailAndType(String email, VerificationType type);
    Verification getByEmailAndCode(String email, String code);
    Verification getByEmailAndType(String email, VerificationType type);
}
