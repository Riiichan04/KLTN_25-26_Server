package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.PaymentStatus;
import vn.id.nonglam.kltn.kltn.dto.request.order.OrderRequest;
import vn.id.nonglam.kltn.kltn.dto.response.order.OrderResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomDetail;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomType;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.models.order.OrderDetail;
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
        List<RoomType> roomTypes = roomTypeRepository.findByHotel_IdAndActiveTrue(hotelId);
        List<OrderResponse.RoomDetailValidResponse> result = new ArrayList<>();

        for (RoomType rt : roomTypes) {
            List<OrderResponse.RoomDetailSnapShotResponse> roomDetailSnapshot = rt.getRoomDetails().stream()
                    .filter(rd -> rd.isActive()).map(rd -> new OrderResponse.RoomDetailSnapShotResponse(rd.getId(), rd.getRoomCode(),
                            orderDetailRepository.checkValidRoomDetail(rd.getId(), startDate, endDate))).toList();
            OrderResponse.RoomDetailValidResponse roomValid = new OrderResponse.RoomDetailValidResponse(
                    rt.getId(), rt.getName(), rt.getDepositedPercent(), rt.getPrice(), roomDetailSnapshot);
            result.add(roomValid);
        }
        return result;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        if(orderRequest.checkin().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date");

        if(orderRequest.checkin().isAfter(orderRequest.checkout()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Checkin date must before checkout date!");

        for(UUID id: orderRequest.roomDetailsId()) {
            if(orderDetailRepository.checkValidRoomDetail(id, orderRequest.checkin(), orderRequest.checkout())
            && roomDetailRepository.existsByIdAndActiveTrue(id)) continue;

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your chosen room is not valid!");
        }

        Order order = new Order();
        order.setUser(userRepository.getReferenceById(userId));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setNote(orderRequest.note());
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setCheckInDate(orderRequest.checkin());
        order.setCheckOutDate(orderRequest.checkout());
        order = orderRepository.save(order);

        BigDecimal deposited = BigDecimal.valueOf(0);
        for(UUID id: orderRequest.roomDetailsId()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            RoomDetail roomDetail = roomDetailRepository.findByIdAndActiveTrue(id);
            if(roomDetail == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your chosen room is not valid!");

            orderDetail.setRoomDetail(roomDetail);
            BigDecimal actualPrice = roomDetail.getRoomType().getPrice();
            deposited = deposited.add(actualPrice.multiply(BigDecimal.valueOf(roomDetail.getRoomType().getDepositedPercent())));
            orderDetail.setActualPrice(actualPrice);
            orderDetailRepository.save(orderDetail);
        }

        return new OrderResponse(true, order.getId(), deposited);
    }


}
