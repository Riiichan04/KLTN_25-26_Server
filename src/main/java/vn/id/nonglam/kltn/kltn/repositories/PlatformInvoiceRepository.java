package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.id.nonglam.kltn.kltn.common.enums.InvoiceStatus;
import vn.id.nonglam.kltn.kltn.models.payment.PlatformInvoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PlatformInvoiceRepository extends JpaRepository<PlatformInvoice, UUID> {
    List<PlatformInvoice> findByStatusAndDueDateBefore(InvoiceStatus attr0, LocalDateTime dueDateBefore);
}
