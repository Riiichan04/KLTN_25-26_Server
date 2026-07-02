package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.dto.request.search.CoordinatesRequest;
import vn.id.nonglam.kltn.kltn.dto.request.search.ExtentAddressRequest;
import vn.id.nonglam.kltn.kltn.dto.response.search.CardHotelResponse;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final HotelRepository hotelRepository;

    public Page<CardHotelResponse> searchHotels(String keyWord, List<Double> extentAddressRequest,
                                                int type, Integer minRating,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                Pageable pageable) {
        Page<CardHotelResponse> hotels = Page.empty();
        /**
         * If type = 0, search by key word
         * If type = 1, search by coordinates
         * */
        if(type == 0 && keyWord != null && !keyWord.isEmpty()) {
            hotels = hotelRepository.findHotels(keyWord, null,
                    null, null, null, minRating, minPrice, maxPrice, pageable);
        }
        else if(type == 1 && extentAddressRequest != null && !extentAddressRequest.isEmpty()) {
            if(extentAddressRequest.size() == 4) {
                ExtentAddressRequest addressRequest = new ExtentAddressRequest(extentAddressRequest);
                hotels = hotelRepository.findHotels(null, addressRequest.getMinLongitudeExtent(),
                        addressRequest.getMinLatitudeExtent(),
                        addressRequest.getMaxLongitudeExtent(),
                        addressRequest.getMaxLatitudeExtent(), minRating, minPrice, maxPrice,pageable);
            }
            else if(extentAddressRequest.size() == 2) {
                CoordinatesRequest addressRequest = new CoordinatesRequest(extentAddressRequest);
                double[] minAndMaxCoordinates = calMinMaxLatitudeAndLongitude(addressRequest.getLatitude(),
                        addressRequest.getLongitude(), 5);
                hotels = hotelRepository.findHotels(null, minAndMaxCoordinates[0],
                        minAndMaxCoordinates[1], minAndMaxCoordinates[2],
                        minAndMaxCoordinates[3], minRating, minPrice, maxPrice, pageable);

            }
        }
        return hotels;
    }

    private double[] calMinMaxLatitudeAndLongitude(double latitude, double longitude, double distance) {
        /**
         * Calculator Min and Max Latitude:
         * We have: 1 latitude ~ 111km
         * => Bounding box: +- (distance)/111 latitude
         *
         * Calculator Min and Max Longitude:
         * Longitude depends on latitude
         * We have: 1 longitude = (111 x cos(latitude))km
         * => Bounding box: +- (distance)/ (111 x cos(latitude))
         */
        double deltaLatitude = distance/111;
        // Only use radian in java
        double deltaLongitude = distance/(111 * Math.cos(Math.toRadians(latitude)));

        return new double[] {longitude - deltaLongitude, latitude - deltaLatitude,
                            longitude + deltaLongitude, latitude + deltaLatitude};
    }

}
