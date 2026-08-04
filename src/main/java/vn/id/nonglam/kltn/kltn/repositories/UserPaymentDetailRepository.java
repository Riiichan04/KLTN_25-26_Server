package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.id.nonglam.kltn.kltn.models.user.UserPaymentDetail;

public interface UserPaymentDetailRepository extends JpaRepository<UserPaymentDetail, Long> {
}
