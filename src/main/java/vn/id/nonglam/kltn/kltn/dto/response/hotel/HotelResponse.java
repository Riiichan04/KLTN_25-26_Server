package vn.id.nonglam.kltn.kltn.dto.response.hotel;

import vn.id.nonglam.kltn.kltn.common.enums.HotelStatus;

import java.util.Set;
import java.util.UUID;

public record HotelResponse(UUID id, String name, int countComments, double avgRating, Set<String> images, String description, String thumbnail,
                            AddressResponse address, Set<RoomTypeResponse> roomTypes,
                            String hotline, Set<HotelUtilityResponse> hotelUtilities,
                            int viewCount, HotelStatus status, Set<HotelRegulationResponse> hotelRegulations) {
    public record HotelRegulationResponse(UUID id, String name, String description) {}

    public record RoomUtilityResponse(String name, String iconCode) {}

    public record RoomTypeResponse(UUID id, String name, String description, int capacity,
                                    double price, Set<RoomUtilityResponse> roomUtilities, 
                                    Set<String> roomTypeImages, double depositedPercent) {}

    public record AddressResponse(UUID id, String street, String ward, String province, int postalCode, double latitude, double longitude) {}

    public record HotelUtilityResponse(UUID id, String name) {}
}
