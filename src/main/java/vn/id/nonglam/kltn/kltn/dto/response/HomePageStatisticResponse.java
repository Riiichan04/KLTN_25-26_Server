package vn.id.nonglam.kltn.kltn.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record HomePageStatisticResponse(
        List<ProvinceStatistic> provinceStatistics,
        List<PromotionalHotelResponse> promotionalHotels
) {
    public record ProvinceStatistic(String name, Long hotelCount, int postalCode, String thumbnail) {}
    public record PromotionalHotelResponse(
            UUID id,
            String name,
            String thumbnail,
            String province,
            Double rating,
            BigDecimal originalPrice,
            BigDecimal promotionalPrice
    ) {}
}
