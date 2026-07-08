package vn.id.nonglam.kltn.kltn.dto.request.admin;

import vn.id.nonglam.kltn.kltn.common.enums.HotelStatus;

import java.util.List;
import java.util.UUID;

public record UpdateHotelAdminRequest(
        UUID id,
        String name,
        List<String> images,
        String description,
        String thumbnail,
        String street,
        String ward,
        String province,
        Integer postalCode,
        Double latitude,
        Double longitude,
        String hotline,
        List<UUID> hotelUtilities,
        HotelStatus status,
        List<HotelRegulationRequest> hotelRegulations
) {
   public record HotelRegulationRequest(UUID id, String name, String description) {};
}