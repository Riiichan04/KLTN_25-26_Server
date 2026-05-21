package vn.id.nonglam.kltn.kltn.dto.request.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderRequest(List<UUID> roomDetailsId, String note, LocalDateTime checkin, LocalDateTime checkout) {}
