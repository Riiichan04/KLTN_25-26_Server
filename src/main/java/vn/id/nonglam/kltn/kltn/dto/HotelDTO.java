package vn.id.nonglam.kltn.kltn.dto;

import vn.id.nonglam.kltn.kltn.common.enums.HotelStatus;

import java.util.Set;
import java.util.UUID;

public class HotelDTO {
    public record HotelRegulationResponse(UUID id, String name, String description) {}

    public record RoomUtilityResponse(UUID id, String name, String iconCode) {}
    
    public record RoomTypeImageResponse(UUID id, String url) {}
    
    public record RoomTypeResponse(UUID id, String name, String description, int capacity,
                                    double price, Set<RoomUtilityResponse> roomUtilities, 
                                    Set<RoomTypeImageResponse> roomTypeImages, double depositedPercent) {}

    public record AddressResponse(UUID id, String street, String ward, String province, int postalCode, double latitude, double longitude) {}

    public record HotelUtilityResponse(UUID id, String name) {}

    public record HotelDetailResponse(UUID id, String name, int countComments, double avgRating, String description, String thumbnail,
                                      AddressResponse address, Set<RoomTypeResponse> roomTypes,
                                      String hotline, Set<HotelUtilityResponse> hotelUtilities,
                                      int viewCount, HotelStatus status, Set<HotelRegulationResponse> hotelRegulations) {}
    
}
