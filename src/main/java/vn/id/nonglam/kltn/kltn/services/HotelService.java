package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.dto.HotelDTO;
import vn.id.nonglam.kltn.kltn.models.hotel.*;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;

    public HotelDTO.HotelDetailResponse getHotelById(UUID id) {
        Hotel hotel = hotelRepository.getHotelById(id);
        if(hotel == null) return null;
        return mapper(hotel);
    }

    private HotelDTO.HotelDetailResponse mapper(Hotel h) {
        return new HotelDTO.HotelDetailResponse(h.getId(), h.getName(), h.getDescription(),
                h.getThumbnail(), mapperAddress(h.getAddress()), mapperRoomType(h.getRoomTypes()),
                h.getHotline(), mapperHotelUtility(h.getUtilities()),
                h.getViewCount(), h.getStatus());
    }

    private HotelDTO.AddressResponse mapperAddress(Address address) {
        return new HotelDTO.AddressResponse(address.getId(), address.getStreet(), address.getWard(),
                address.getProvince(), address.getPostalCode(), address.getLatitude(), address.getLongitude());
    }

    private Set<HotelDTO.RoomTypeResponse> mapperRoomType(Set<RoomType> roomTypes) {
        return roomTypes.stream().map(r -> new HotelDTO.RoomTypeResponse(r.getId(),
                r.getName(), r.getDescription(), r.getCapacity(),
                r.getPrice(), mapperRoomUtility(r.getUtilities()),
                mapperRoomTypeImage(r.getImages()), r.getDepositedPercent()))
                .collect(Collectors.toSet());
    }

    private Set<HotelDTO.RoomUtilityResponse> mapperRoomUtility(Set<RoomUtility> roomUtilities) {
        return roomUtilities.stream().map(u -> new HotelDTO.RoomUtilityResponse(u.getId(), u.getName())).collect(Collectors.toSet());
    }

    private Set<HotelDTO.RoomTypeImageResponse> mapperRoomTypeImage(Set<RoomTypeImage> roomTypeImages) {
        return roomTypeImages.stream().map(i -> new HotelDTO.RoomTypeImageResponse(i.getId(), i.getPath())).collect(Collectors.toSet());
    }

    private Set<HotelDTO.HotelUtilityResponse> mapperHotelUtility(Set<HotelUtility> hotelUtilities) {
        return hotelUtilities.stream().map(u -> new HotelDTO.HotelUtilityResponse(u.getId(), u.getName())).collect(Collectors.toSet());
    }

}
