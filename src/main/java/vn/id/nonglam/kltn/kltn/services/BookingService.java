package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.dto.OrderDTO;
import vn.id.nonglam.kltn.kltn.models.hotel.RoomType;
import vn.id.nonglam.kltn.kltn.repositories.OrderDetailRepository;
import vn.id.nonglam.kltn.kltn.repositories.RoomDetailRepository;
import vn.id.nonglam.kltn.kltn.repositories.RoomTypeRepository;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final OrderDetailRepository orderDetailRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomDetailRepository roomDetailRepository;

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
    public OrderDTO.OrderResponse createOrder(String item) {
        UUID userId = SecurityUtil.currentUserId().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));


        return null;
    }
}
