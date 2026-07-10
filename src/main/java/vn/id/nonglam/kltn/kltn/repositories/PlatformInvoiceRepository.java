package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.id.nonglam.kltn.kltn.models.payment.PlatformInvoice;

import java.util.UUID;

public interface PlatformInvoiceRepository extends JpaRepository<PlatformInvoice, UUID> {
}
