package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.models.payment.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
