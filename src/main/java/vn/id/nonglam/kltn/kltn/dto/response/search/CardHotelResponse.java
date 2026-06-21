package vn.id.nonglam.kltn.kltn.dto.response.search;

import java.math.BigDecimal;
import java.util.UUID;

public interface CardHotelResponse {
    UUID getId();
    String getName();
    String getThumbnail();
    String getStreet();
    String getWard();
    String getProvince();
    double getLatitude();
    double getLongitude();
    String getDescription();
    int getViewCount();
    double getAvgRating();
    int getTotalComment();
    BigDecimal getMinPrice();
    BigDecimal getMaxPrice();
}