package vn.id.nonglam.kltn.kltn.dto.request.search;

import java.util.List;

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
