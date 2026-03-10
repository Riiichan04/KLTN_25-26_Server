package vn.id.nonglam.kltn.kltn.dto;

import vn.id.nonglam.kltn.kltn.models.Address;

import java.util.List;
import java.util.UUID;

public class SearchDTO {

    public record AddressRequest(List<Double> extentAddress, List<Double> coordinate) {
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
        public Double getLongitude() {
            return coordinate.get(0);
        }
        public Double getLatitude() {
            return coordinate.get(1);
        }
    }

    public record SearchHotelDTO(UUID id, String title, String url, Address address, String description, double avgRating,
                                    int totalComment, double price) {}
}

