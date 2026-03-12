package vn.id.nonglam.kltn.kltn.dto;

import java.util.List;
import java.util.UUID;

public class SearchDTO {

    public record ExtentAddressRequest(List<Double> extentAddress) {
        public Double getMaxLongitudeExtent() {
            return extentAddress.get(0);
        }
        public Double getMaxLatitudeExtent() {
            return extentAddress.get(1);
        }
        public Double getMinLongitudeExtent() {
            return extentAddress.get(2);
        }
        public Double getMinLatitudeExtent() {
            return extentAddress.get(3);
        }
    }

    public record CoordinatesRequest(List<Double> coordinates) {
        public Double getLongitude() {
            return coordinates.get(0);
        }
        public Double getLatitude() {
            return coordinates.get(1);
        }
    }

    public record SearchHotelResponse(UUID id, String name, String thumbnail, String street, String ward,
                                      String province, double latitude, double longitude,
                                      String description, int viewCount, double avgRating, int totalComment,
                                      double minPrice, double maxPrice) {}
}

