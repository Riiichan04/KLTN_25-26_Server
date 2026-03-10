package vn.id.nonglam.kltn.kltn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.dto.SearchDTO;
import vn.id.nonglam.kltn.kltn.models.Hotel;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final HotelRepository hotelRepository;

    public Page<SearchDTO.SearchHotelDTO> searchByHotelName(String hotelName, SearchDTO.AddressRequest addressRequest, int type,
                                                            Date checkIn, Date checkOut,
                                                            int adultNumber, int childrenNumber, boolean hasAnimal,
                                                            Pageable pageable) {
        Page<Hotel> hotels;
        if(type == 0) {
            hotels = hotelRepository.findByNameContainingIgnoreCase(hotelName, pageable);
        } else if(type == 1) {
            if(addressRequest.extentAddress() != null && !addressRequest.extentAddress().isEmpty()) {
                hotels = hotelRepository.findHotelsByExtentAddress(addressRequest.getMaxLongitudeExtent(), addressRequest.getMaxLatitudeExtent(),
                        addressRequest.getMinLongitudeExtent(), addressRequest.getMinLatitudeExtent(), pageable);
            } else {
                hotels = hotelRepository.findHotelsByCoordinates(addressRequest.getLongitude(), addressRequest.getLatitude(), 5, pageable);
            }
        }

        return null;
    }

}
