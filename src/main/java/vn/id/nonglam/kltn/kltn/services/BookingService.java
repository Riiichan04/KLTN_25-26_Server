package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.common.constants.PlatformFee;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.PaymentStatus;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderRequest;
import vn.id.nonglam.kltn.kltn.dto.response.order.OrderResponse;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomDetailRepository roomDetailRepository;
    private final UserRepository userRepository;

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

        List<RoomDetail> roomDetails = orderRequest.roomDetailsId().stream().map(id -> {
            boolean isValid = orderDetailRepository.checkValidRoomDetail(id, orderRequest.checkin(), orderRequest.checkout());
            boolean isExits = roomDetailRepository.existsByIdAndActiveTrue(id);

            if (!isValid || !isExits) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your chosen room is not valid!");
            }
            return roomDetailRepository.findByIdAndActiveTrue(id);
        }).toList();

        User user = userRepository.getReferenceById(userId);
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setNote(orderRequest.note());
//        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCheckInDate(orderRequest.checkin());
        order.setCheckOutDate(orderRequest.checkout());
        order.setTotalCapacity(order.getTotalCapacity());
        order = orderRepository.save(order);

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
            double platformFeePercent = PlatformFee.getPlatformFee(user.getUserType());
            BigDecimal platformFee = actualPrice.multiply(BigDecimal.valueOf(platformFeePercent));
            orderDetail.setPlatformFee(platformFee);

            orderDetailRepository.save(orderDetail);
        }
        return new OrderResponse(true, order.getId(), totalPrice);
    }

    @Transactional
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        User user = findUser();
        if (user == null) return;

        UserRole role = user.getRole();
        if (role == null) return;

        if (!verifyOwnership(user, role, order)) return;

        validateOrderStatusUpdate(role, order.getOrderStatus(), newStatus);

        order.setOrderStatus(newStatus);
        orderRepository.save(order);
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

    private void validateOrderStatusUpdate(UserRole role, OrderStatus currentStatus, OrderStatus newStatus) {
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
}
