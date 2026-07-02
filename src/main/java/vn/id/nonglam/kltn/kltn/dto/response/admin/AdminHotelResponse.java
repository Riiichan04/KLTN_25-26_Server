package vn.id.nonglam.kltn.kltn.dto.response.admin;


import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.HotelStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AdminHotelResponse(UUID id, String name, OwnerResponse owner, Set<ImageResponse> images, String description, String thumbnail,
                                       AddressResponse address, Set<RoomTypeResponse> roomTypes,
                                       String hotline, Set<HotelUtilityResponse> hotelUtilities,
                                       int viewCount, boolean isActive, HotelStatus status, Set<HotelRegulationResponse> hotelRegulations, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public record OwnerResponse(UUID id, String username, String avatarUrl, String email, String phone, Gender gender, boolean isActive) {}

    public record ImageResponse(UUID id, String path) {}

    public record HotelRegulationResponse(UUID id, String name, String description) {}

    public record RoomUtilityResponse(UUID id, String name, String iconCode) {}

    public record RoomTypeImageResponse(UUID id, String path) {}

    public record RoomDetailResponse(UUID id, String roomCode, boolean isActive) {}

    public record RoomTypeResponse(UUID id, String name, String description, int capacity,
                                   BigDecimal price, Set<RoomUtilityResponse> roomUtilities,
                                   Set<RoomTypeImageResponse> roomTypeImages, Set<RoomDetailResponse> roomDetails, double depositedPercent) {}

    public record AddressResponse(UUID id, String street, String ward, String province, int postalCode, double latitude, double longitude, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {}

    public record HotelUtilityResponse(UUID id, String name, String iconCode) {}
}
