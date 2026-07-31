package vn.id.nonglam.kltn.kltn.dto.response.admin;

import lombok.Builder;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record AdminOrderResponse(UUID id, HotelResponse hotel, CustomerResponse customer, OrderStatus orderStatus,
                                 List<OrderDetailResponse> orderDetails,
                                 String note, int totalCapacity, LocalDateTime checkinDate, LocalDateTime checkoutDate,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
    @Builder
    public record HotelResponse(UUID id, String name, String ownerUsername, String ownerEmail,
                                String thumbnail, boolean isActive) {}

    @Builder
    public record OrderDetailResponse(UUID id, String roomTypeName, List<RoomDetailResponse> roomDetails) {}

    @Builder
    public record RoomDetailResponse(UUID id, String roomCode,
                                     BigDecimal actualPrice, BigDecimal platformFee) {}

    @Builder
    public record CustomerResponse(UUID id, String email, String username, String avatarUrl, boolean isActive) {}

}
