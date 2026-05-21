package vn.id.nonglam.kltn.kltn.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeActiveHotelRequest;
import vn.id.nonglam.kltn.kltn.dto.request.admin.ChangeOwnerHotelRequest;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Address;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminHotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public Page<GetSnapshotHotelResponse> getHotels(Pageable pageable) {
        return hotelRepository.getHotels(pageable);
    }

    public AdminHotelResponse getHotelById(UUID id) {
        return hotelRepository.findById(id)
                .map(this::mapperHotelToGetHotelResponse)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách sạn với ID: " + id));
    }

    private AdminHotelResponse mapperHotelToGetHotelResponse(Hotel h) {
        Set<AdminHotelResponse.ImageResponse> images = h.getImages().stream().map(i -> new AdminHotelResponse.ImageResponse(i.getId(), i.getPath())).collect(Collectors.toSet());
        User o = h.getOwner();
        AdminHotelResponse.OwnerResponse owner = new AdminHotelResponse.OwnerResponse(o.getId(), o.getUsername(), o.getAvatarUrl(), o.getEmail(), o.getPhone(), o.getGender(), o.isActive());
        Address a = h.getAddress();
        AdminHotelResponse.AddressResponse address = new AdminHotelResponse.AddressResponse(a.getId(), a.getStreet(), a.getWard(), a.getProvince(),
                a.getPostalCode(), a.getLatitude(), a.getLongitude(), a.isActive(), a.getCreatedAt(), a.getUpdatedAt());

        Set<AdminHotelResponse.RoomTypeResponse> roomTypes = h.getRoomTypes().stream().map(rt -> new AdminHotelResponse.RoomTypeResponse(
                rt.getId(), rt.getName(), rt.getDescription(), rt.getCapacity(), rt.getPrice(),
                rt.getUtilities().stream().map(u -> new AdminHotelResponse.RoomUtilityResponse(u.getId(), u.getName(), u.getIconCode())).collect(Collectors.toSet()),
                rt.getImages().stream().map(i -> new AdminHotelResponse.RoomTypeImageResponse(i.getId(), i.getPath())).collect(Collectors.toSet()),
                rt.getRoomDetails().stream().map(rd -> new AdminHotelResponse.RoomDetailResponse(rd.getId(), rd.getRoomCode(), rd.isActive())).collect(Collectors.toSet()),
                rt.getDepositedPercent())).collect(Collectors.toSet());

        Set<AdminHotelResponse.HotelUtilityResponse> utilities = h.getUtilities().stream().map(u -> new AdminHotelResponse.HotelUtilityResponse(u.getId(), u.getName(), u.getIconCode())).collect(Collectors.toSet());

        Set<AdminHotelResponse.HotelRegulationResponse> regulations = h.getRegulations().stream().map(r -> new AdminHotelResponse.HotelRegulationResponse(r.getId(), r.getName(), r.getDescription())).collect(Collectors.toSet());

        return new AdminHotelResponse(
                h.getId(), h.getName(), owner, images, h.getDescription(),
                h.getThumbnail(), address, roomTypes, h.getHotline(), utilities, h.getViewCount(), h.isActive(), h.getStatus(), regulations,
                h.getCreatedAt(), h.getUpdatedAt()
        );
    }

    public ChangeActiveHotelResponse changActive(ChangeActiveHotelRequest request) {
        Hotel h = hotelRepository.getReferenceById(request.id());
        h.setActive(request.active());
        return new ChangeActiveHotelResponse(hotelRepository.save(h) != null, request.active());
    }

    @Transactional
    public AdminHotelResponse.OwnerResponse changeOwner(ChangeOwnerHotelRequest request) {
        Hotel h = hotelRepository.getReferenceById(request.hotelId());
        User u = userRepository.findUserById(request.userId());

        h.setOwner(u);
        hotelRepository.save(h);

        return new AdminHotelResponse.OwnerResponse(u.getId(), u.getUsername(), u.getAvatarUrl(), u.getEmail(), u.getPhone(), u.getGender(), u.isActive());
    }
}
