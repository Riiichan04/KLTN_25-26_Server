package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.common.enums.OrderStatus;
import vn.id.nonglam.kltn.kltn.common.enums.PaymentStatus;
import vn.id.nonglam.kltn.kltn.dto.OrderDTO;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomDetail;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomType;
import vn.id.nonglam.kltn.kltn.models.order.Order;
import vn.id.nonglam.kltn.kltn.models.order.OrderDetail;
import vn.id.nonglam.kltn.kltn.repositories.*;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

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

    public List<OrderDTO.RoomDetailValidResponse> getRoomDetailsValid(UUID hotelId, LocalDateTime startDate, LocalDateTime endDate) {
        List<RoomType> roomTypes = roomTypeRepository.findByHotel_IdAndActiveTrue(hotelId);
        List<OrderDTO.RoomDetailValidResponse> result = new ArrayList<>();

        for (RoomType rt : roomTypes) {
            List<OrderDTO.RoomDetailSnapShotResponse> roomDetailSnapshot = rt.getRoomDetails().stream()
                    .filter(rd -> rd.isActive()).map(rd -> new OrderDTO.RoomDetailSnapShotResponse(rd.getId(), rd.getRoomCode(),
                            orderDetailRepository.checkValidRoomDetail(rd.getId(), startDate, endDate))).toList();
            OrderDTO.RoomDetailValidResponse roomValid = new OrderDTO.RoomDetailValidResponse(
                    rt.getId(), rt.getName(), roomDetailSnapshot);
            result.add(roomValid);
        }
        return result;
    }

    @Transactional
    public OrderDTO.OrderResponse createOrder(OrderDTO.OrderRequest orderRequest) {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

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

        double deposited = 0;
        for(UUID id: orderRequest.roomDetailsId()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            RoomDetail roomDetail = roomDetailRepository.findByIdAndActiveTrue(id);
            if(roomDetail == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your chosen room is not valid!");

            orderDetail.setRoomDetail(roomDetail);
            double actualPrice = roomDetail.getRoomType().getPrice();
            deposited += actualPrice*roomDetail.getRoomType().getDepositedPercent();
            orderDetail.setActualPrice(actualPrice);
            orderDetailRepository.save(orderDetail);
        }

        return new OrderDTO.OrderResponse(true, order.getId(), deposited);
    }


}
