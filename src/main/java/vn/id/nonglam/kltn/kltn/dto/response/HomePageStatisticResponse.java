package vn.id.nonglam.kltn.kltn.dto.response;

import java.util.List;

public record HomePageStatisticResponse(
        List<ProvinceStatistic> provinceStatistics
) {
    public record ProvinceStatistic(String name, int hotelCount, String postalCode, String thumbnail) {}
}
