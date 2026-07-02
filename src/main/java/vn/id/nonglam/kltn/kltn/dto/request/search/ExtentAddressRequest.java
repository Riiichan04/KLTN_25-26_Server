package vn.id.nonglam.kltn.kltn.dto.request.search;

import java.util.List;

public record ExtentAddressRequest(List<Double> extentAddress) {

    public Double getMinLatitudeExtent() {
        return extentAddress != null && extentAddress.size() >= 4 ? extentAddress.get(0) : null;
    }

    public Double getMaxLatitudeExtent() {
        return extentAddress != null && extentAddress.size() >= 4 ? extentAddress.get(1) : null;
    }

    public Double getMinLongitudeExtent() {
        return extentAddress != null && extentAddress.size() >= 4 ? extentAddress.get(2) : null;
    }

    public Double getMaxLongitudeExtent() {
        return extentAddress != null && extentAddress.size() >= 4 ? extentAddress.get(3) : null;
    }
}
