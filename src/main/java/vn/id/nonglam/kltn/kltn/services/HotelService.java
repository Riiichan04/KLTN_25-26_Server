package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.dto.response.HomePageStatisticResponse;
import vn.id.nonglam.kltn.kltn.dto.response.hotel.HotelResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.*;
import vn.id.nonglam.kltn.kltn.repositories.AddressRepository;
import vn.id.nonglam.kltn.kltn.repositories.CommentRepository;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;
import vn.id.nonglam.kltn.kltn.dto.response.HomePageStatisticResponse.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final CommentRepository commentRepository;
    private final AddressRepository addressRepository;

    public HotelResponse getHotelById(UUID id) {
        Hotel hotel = hotelRepository.getHotelById(id);
        if(hotel == null) return null;
        return mapper(hotel);
    }

    public HomePageStatisticResponse getHomePageData() {
        List<ProvinceStatistic> provinces = addressRepository.statisticAddress();

        List<Hotel> rawExploreHotels = hotelRepository.findTop5ByIsActiveTrueOrderByCreatedAtDesc();
        List<HomePageStatisticResponse.ExploreHotelResponse> exploreHotels = rawExploreHotels.stream().map(h ->
                new HomePageStatisticResponse.ExploreHotelResponse(
                        h.getId(),
                        h.getName(),
                        h.getThumbnail(),
                        h.getAddress() != null ? h.getAddress().getProvince() : ""
                )
        ).toList();

        List<Hotel> topHotels = hotelRepository.findTop5ByIsActiveTrueOrderByViewCountDesc();

        List<HomePageStatisticResponse.PromotionalHotelResponse> promotions = topHotels.stream().map(h -> {
            Double rating = commentRepository.avgRatingByHotelId(h.getId());

            BigDecimal minPrice = h.getRoomTypes().stream()
                    .map(RoomType::getPrice)
                    .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.valueOf(0));

            return new HomePageStatisticResponse.PromotionalHotelResponse(
                    h.getId(),
                    h.getName(),
                    h.getThumbnail(),
                    h.getAddress() != null ? h.getAddress().getProvince() : "",
                    rating,
                    minPrice,
                    minPrice    //FIXME: Add Promotion Here
            );
        }).toList();

        return new HomePageStatisticResponse(provinces, promotions, exploreHotels);
    }

    private HotelResponse mapper(Hotel h) {
        return new HotelResponse(h.getId(), h.getName(), commentRepository.countByHotelIdAndActiveTrue(h.getId()),
                commentRepository.avgRatingByHotelId(h.getId()), mapperHotelImages(h.getImages()), h.getDescription(),
                h.getThumbnail(), mapperAddress(h.getAddress()), mapperRoomType(h.getRoomTypes()),
                h.getHotline(), mapperHotelUtility(h.getUtilities()),
                h.getViewCount(), h.getStatus(), mapperHotelRegulation(h.getRegulations()));
    }

    private HotelResponse.AddressResponse mapperAddress(Address address) {
        return new HotelResponse.AddressResponse(address.getId(), address.getStreet(), address.getWard(),
                address.getProvince(), address.getPostalCode(), address.getLatitude(), address.getLongitude());
    }

    private Set<String> mapperHotelImages(Set<HotelImage> images) {
        return images.stream().map(i -> i.getPath()).collect(Collectors.toSet());
    }

    private Set<HotelResponse.RoomTypeResponse> mapperRoomType(Set<RoomType> roomTypes) {
        return roomTypes.stream().map(r -> new HotelResponse.RoomTypeResponse(r.getId(),
                r.getName(), r.getDescription(), r.getCapacity(),
                r.getPrice(), mapperRoomUtility(r.getUtilities()),
                mapperRoomTypeImage(r.getImages()), r.getDepositedPercent()))
                .collect(Collectors.toSet());
    }

    private Set<HotelResponse.RoomUtilityResponse> mapperRoomUtility(Set<RoomUtility> roomUtilities) {
        return roomUtilities.stream().map(u -> new HotelResponse.RoomUtilityResponse(u.getName(), u.getIconCode())).collect(Collectors.toSet());
    }

    private Set<String> mapperRoomTypeImage(Set<RoomTypeImage> roomTypeImages) {
        return roomTypeImages.stream().map(i -> i.getPath()).collect(Collectors.toSet());
    }

    private Set<HotelResponse.HotelUtilityResponse> mapperHotelUtility(Set<HotelUtility> hotelUtilities) {
        return hotelUtilities.stream().map(u -> new HotelResponse.HotelUtilityResponse(u.getId(), u.getName())).collect(Collectors.toSet());
    }

    private Set<HotelResponse.HotelRegulationResponse> mapperHotelRegulation(Set<HotelRegulation> hotelRegulations) {
        return hotelRegulations.stream().map(r -> new HotelResponse.HotelRegulationResponse(r.getId(), r.getName(), r.getDescription())).collect(Collectors.toSet());
    }

}
