package vn.id.nonglam.kltn.kltn.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeOrderStatusRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminOrderResponse;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.models.order.OrderDetail;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderRepository orderRepository;

    public Page<AdminOrderResponse> getOrders(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findAll(keyword, startDate, endDate, pageable)
                .map(this::mapperToAdminOrderResponse);
    }

    private AdminOrderResponse mapperToAdminOrderResponse(Order o) {
        // 1. Grouping orderDetails theo RoomType
        List<AdminOrderResponse.OrderDetailResponse> orderDetailResponses = Optional.ofNullable(o.getOrderDetails())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.groupingBy(detail -> {
                    if (detail.getRoomDetail() == null || detail.getRoomDetail().getRoomType() == null) {
                        return "UNKNOWN_ROOM_TYPE";
                    }
                    return detail.getRoomDetail().getRoomType().getId().toString();
                }))
                .entrySet().stream()
                .map(entry -> {
                    String typeKey = entry.getKey();
                    List<OrderDetail> detailsInGroup = entry.getValue();

                    UUID orderDetailId = detailsInGroup.get(0).getId();
                    String roomTypeName = "Phòng không xác định";

                    if (!"UNKNOWN_ROOM_TYPE".equals(typeKey)) {
                        roomTypeName = detailsInGroup.get(0).getRoomDetail().getRoomType().getName();
                    }

                    // Map danh sách danh sách các phòng chi tiết (roomCode, actualPrice, platformFee)
                    List<AdminOrderResponse.RoomDetailResponse> roomDetails = detailsInGroup.stream()
                            .map(detail -> {
                                UUID roomDetailId = detail.getRoomDetail() != null ? detail.getRoomDetail().getId() : null;
                                String roomCode = detail.getRoomDetail() != null ? detail.getRoomDetail().getRoomCode() : "N/A";

                                return AdminOrderResponse.RoomDetailResponse.builder()
                                        .id(roomDetailId)
                                        .roomCode(roomCode)
                                        .actualPrice(detail.getActualPrice())
                                        .platformFee(detail.getPlatformFee())
                                        .build();
                            })
                            .toList();

                    return AdminOrderResponse.OrderDetailResponse.builder()
                            .id(orderDetailId)
                            .roomTypeName(roomTypeName)
                            .roomDetails(roomDetails)
                            .build();
                })
                .toList();

        // 2. Build AdminOrderResponse
        return AdminOrderResponse.builder()
                .id(o.getId())
                .hotel(Optional.ofNullable(o.getHotel())
                        .map(hotel -> {
                            User owner = hotel.getOwner();
                            return AdminOrderResponse.HotelResponse.builder()
                                    .id(hotel.getId())
                                    .name(hotel.getName())
                                    .thumbnail(hotel.getThumbnail())
                                    .isActive(hotel.isActive())
                                    .ownerEmail(owner != null ? owner.getEmail() : null)
                                    .ownerUsername(owner != null ? owner.getUsername() : null)
                                    .build();
                        })
                        .orElse(null))
                .customer(Optional.ofNullable(o.getUser())
                        .map(u -> AdminOrderResponse.CustomerResponse.builder()
                                .id(u.getId())
                                .email(u.getEmail())
                                .username(u.getUsername())
                                .avatarUrl(u.getAvatarUrl())
                                .isActive(u.isActive())
                                .build())
                        .orElse(null))
                .orderStatus(o.getOrderStatus())
                .orderDetails(orderDetailResponses)
                .checkinDate(o.getCheckInDate())
                .checkoutDate(o.getCheckOutDate())
                .note(o.getNote())
                .totalCapacity(o.getTotalCapacity())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    @Transactional
    public Boolean changeStatus(UUID id, ChangeOrderStatusRequest req) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No order found with id: " + id));
        order.setOrderStatus(req.orderStatus());
        orderRepository.save(order);
        return true;
    }
}
