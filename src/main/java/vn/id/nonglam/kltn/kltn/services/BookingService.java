package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.common.constants.PlatformFee;
import vn.id.nonglam.kltn.kltn.common.constants.RedirectUrl;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderRequest;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderStatusCount;
import vn.id.nonglam.kltn.kltn.dto.response.order.HistoryOrderResponse;
import vn.id.nonglam.kltn.kltn.dto.response.order.OrderResponse;
import vn.id.nonglam.kltn.kltn.dto.response.order.UpdateOrderResponse;
import vn.id.nonglam.kltn.kltn.dto.response.order.UserOrderResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomDetail;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomType;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.models.order.OrderDetail;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.*;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomDetailRepository roomDetailRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final RedirectUrl redirectUrl;

    public List<OrderResponse.RoomDetailValidResponse> getRoomDetailsValid(UUID hotelId, LocalDateTime startDate, LocalDateTime endDate) {
        List<RoomType> roomTypes = roomTypeRepository.findByHotel_IdAndIsActiveTrue(hotelId);
        List<OrderResponse.RoomDetailValidResponse> result = new ArrayList<>();

        for (RoomType rt : roomTypes) {
            List<OrderResponse.RoomDetailSnapShotResponse> roomDetailSnapshot = rt.getRoomDetails().stream()
                    .filter(RoomDetail::isActive)
                    .map(rd ->
                            new OrderResponse.RoomDetailSnapShotResponse(
                                    rd.getId(),
                                    rd.getRoomCode(),
                                    orderDetailRepository.checkValidRoomDetail(rd.getId(), startDate, endDate))
                    ).toList();
            OrderResponse.RoomDetailValidResponse roomValid = new OrderResponse.RoomDetailValidResponse(
                    rt.getId(),
                    rt.getName(),
                    rt.getPrice(),
                    roomDetailSnapshot
            );
            result.add(roomValid);
        }
        return result;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        if (orderRequest.checkin().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date");

        if (orderRequest.checkin().isAfter(orderRequest.checkout()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Checkin date must before checkout date!");

        if (orderRequest.roomDetailsId() == null || orderRequest.roomDetailsId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must choose at least 1 room!");
        }

        List<RoomDetail> roomDetails = orderRequest.roomDetailsId().stream().map(id -> {
            boolean isValid = orderDetailRepository.checkValidRoomDetail(id, orderRequest.checkin(), orderRequest.checkout());
            boolean isExits = roomDetailRepository.existsByIdAndIsActiveTrue(id);

            if (!isValid || !isExits) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your chosen room is not valid!");
            }
            return roomDetailRepository.findByIdAndIsActiveTrue(id);
        }).toList();

        User user = userRepository.getReferenceById(userId);
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setNote(orderRequest.note());
        order.setCheckInDate(orderRequest.checkin());
        order.setCheckOutDate(orderRequest.checkout());
        order.setTotalCapacity(orderRequest.totalCapacity());

        Set<Hotel> listHotel = roomDetails.stream().map(
                roomDetail -> roomDetail.getRoomType().getHotel()
        ).collect(Collectors.toSet());

        if (listHotel.size() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can book only 1 hotel each booking");
        }
        order.setHotel(listHotel.iterator().next());

        order = orderRepository.save(order);

        User owner = order.getHotel().getOwner();
        double platformFeePercent = PlatformFee.getPlatformFee(owner.getUserType());
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (RoomDetail roomDetail : roomDetails) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setRoomDetail(roomDetail);

            // Actual price
            BigDecimal actualPrice = roomDetail.getRoomType().getPrice();
            totalPrice = totalPrice.add(actualPrice);
            orderDetail.setActualPrice(actualPrice);

            //Platform price
            BigDecimal platformFee = actualPrice.multiply(BigDecimal.valueOf(platformFeePercent));
            orderDetail.setPlatformFee(platformFee);

            orderDetailRepository.save(orderDetail);
        }

        mailService.sendNewBookingNoticeEmail(
                owner.getEmail(), // Email của chủ khách sạn
                owner.getDisplayName() == null ? owner.getUsername() : owner.getDisplayName(), // Tên chủ khách sạn
                user.getDisplayName() == null ? user.getUsername() : user.getDisplayName(), // Tên người đặt phòng
                order.getId().toString(),
                order.getCheckInDate(),
                order.getCheckOutDate(),
                totalPrice,
                redirectUrl.clientUrl + "/owner/orders/" + order.getId()
        );


        return new OrderResponse(true, order.getId(), totalPrice);
    }

    @Transactional
    public UpdateOrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        User user = findUser();
        if (user == null) return new UpdateOrderResponse(false, "User not found");

        UserRole role = user.getRole();
        if (role == null) return new UpdateOrderResponse(false, "User not found");

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return new UpdateOrderResponse(false, "Order not found");

        if (!verifyOwnership(user, role, order)) return new UpdateOrderResponse(false, "You are not owner of order");

        try {
            validateOrderStatusUpdate(role, order.getOrderStatus(), newStatus);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new UpdateOrderResponse(false, e.getMessage());
        }

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        if (newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.REJECTED) {
            String statusText = (newStatus == OrderStatus.CONFIRMED) ? "ĐÃ PHÊ DUYỆT" : "TỪ CHỐI";
            String statusColor = (newStatus == OrderStatus.CONFIRMED) ? "#16a34a" : "#dc2626";

            mailService.sendBookingStatusEmail(
                    order.getUser().getEmail(),
                    order.getUser().getUsername(),
                    order.getHotel().getName(),
                    order.getId().toString(),
                    statusText,
                    statusColor,
                    order.getNote(),
                    redirectUrl.clientUrl + "/user/orders/" + order.getId()
            );
        }

        return new UpdateOrderResponse(true, "Update success");
    }

    public List<UserOrderResponse> getOrderByUserId(UUID userId) {
        return orderRepository.findByUser_Id(userId)
                .stream()
                .map(order ->
                        new UserOrderResponse(
                                order.getId(),
                                order.getOrderStatus(),
                                order.calculateTotalAmount()
                        )
                )
                .toList();
    }

    @Transactional
    public void cancelExpiredPaymentOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        mailService.sendPaymentExpiredEmail(
                order.getUser().getEmail(),
                order.getUser().getUsername(),
                order.getHotel().getName(),
                order.getId().toString(),
                order.calculateTotalAmount(),
                redirectUrl.clientUrl + "/hotels/" + order.getHotel().getId()
        );
    }

    private boolean verifyOwnership(User user, UserRole role, Order order) {
        return switch (role) {
            case ADMIN -> true;
            case OWNER -> {
                UUID ownerIdOfThisOrder = order.getHotel().getOwner().getId();
                yield user.getId().equals(ownerIdOfThisOrder);
            }
            case USER -> user.getId().equals(order.getUser().getId());
        };
    }

    private void validateOrderStatusUpdate(UserRole role, OrderStatus currentStatus, OrderStatus newStatus) throws IllegalArgumentException, IllegalStateException {
        switch (role) {
            case ADMIN -> {
            }
            case USER -> {
                if (newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Customers can only cancel bookings.");
                }
                if (currentStatus == OrderStatus.CHECKED_IN || currentStatus == OrderStatus.COMPLETED) {
                    throw new IllegalStateException("Can't cancel bookings after checked in or completed");
                }
            }
            case OWNER -> {
                if (newStatus == OrderStatus.PENDING || newStatus == OrderStatus.PAID) {
                    throw new IllegalArgumentException("Owner can't update status to PENDING and PAID");
                }
                List<OrderStatus> allowedStatus = switch (currentStatus) {
                    case PENDING -> List.of(OrderStatus.CONFIRMED, OrderStatus.REJECTED);
                    case PAID -> List.of(OrderStatus.CHECKED_IN, OrderStatus.COMPLETED);
                    default -> List.of();
                };
                if (!allowedStatus.contains(newStatus)) {
                    throw new IllegalStateException("Status disallowed");
                }
            }
        }
    }

    private User findUser() {
        UUID userId = SecurityUtil.currentUserId().orElse(null);
        if (userId == null) return null;
        return userRepository.findUserById(userId);
    }

    public Page<HistoryOrderResponse> getHistoryOrders(OrderStatus status, Pageable pageable) {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Page<Order> orders = orderRepository.findByUser_IdAndOrderStatus(userId, status, pageable);
        return orders.map(this::mapToHistoryOrderResponse);
    }

    private HistoryOrderResponse mapToHistoryOrderResponse(Order order) {
        HistoryOrderResponse.HotelResponse hotelResponse = null;
        if (order.getHotel() != null) {
            hotelResponse = new HistoryOrderResponse.HotelResponse(
                    order.getHotel().getId(),
                    order.getHotel().getName(),
                    order.getHotel().getThumbnail()
            );
        } else {
            hotelResponse = new HistoryOrderResponse.HotelResponse(null, "Khách sạn đã ngừng hoạt động", null);
        }

        List<HistoryOrderResponse.RoomTypeSnapShotResponse> roomTypeResponses = order.getOrderDetails().stream()
                .collect(Collectors.groupingBy(detail -> {
                    if (detail.getRoomDetail() == null || detail.getRoomDetail().getRoomType() == null) {
                        return "UNKNOWN_ROOM_TYPE";
                    }
                    return detail.getRoomDetail().getRoomType().getId().toString();
                }))
                .entrySet().stream()
                .map(entry -> {
                    String typeKey = entry.getKey();
                    var orderDetailsInRoomType = entry.getValue();
                    String roomTypeName = "Phòng đã không còn tồn tại";
                    UUID roomTypeId = null;

                    if (!"UNKNOWN_ROOM_TYPE".equals(typeKey)) {
                        var firstDetail = orderDetailsInRoomType.get(0).getRoomDetail().getRoomType();
                        roomTypeId = firstDetail.getId();
                        roomTypeName = firstDetail.getName();
                    }

                    // Map list OrderDetail sang list RoomDetailSnapShotResponse
                    List<HistoryOrderResponse.RoomDetailSnapShotResponse> roomDetails = orderDetailsInRoomType.stream()
                            .map(detail -> {
                                UUID detailId = detail.getRoomDetail() != null ? detail.getRoomDetail().getId() : null;
                                String roomCode = detail.getRoomDetail() != null ? detail.getRoomDetail().getRoomCode() : "N/A";

                                return new HistoryOrderResponse.RoomDetailSnapShotResponse(
                                        detailId,
                                        roomCode,
                                        detail.getActualPrice()
                                );
                            })
                            .toList();

                    // Build RoomTypeSnapShotResponse
                    return new HistoryOrderResponse.RoomTypeSnapShotResponse(
                            roomTypeId,
                            roomTypeName,
                            roomDetails
                    );
                })
                .toList();

        return new HistoryOrderResponse(
                order.getId(),
                hotelResponse,
                order.getNote(),
                order.getCheckInDate(),
                order.getCheckOutDate(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                roomTypeResponses
        );
    }

    @Transactional
    public List<OrderStatusCount> countOrders() {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));
        return orderRepository.countOrderStatusByUserId(userId);
    }
}
