package vn.id.nonglam.kltn.kltn.dto.request.search;

import java.util.List;

public record CoordinatesRequest(List<Double> coordinates) {
    public Double getLongitude() {
        return coordinates.get(0);
    }
    public Double getLatitude() {
        return coordinates.get(1);
    }
}
