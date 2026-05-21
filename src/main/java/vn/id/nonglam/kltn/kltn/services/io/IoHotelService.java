package vn.id.nonglam.kltn.kltn.services.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.common.utils.CsvParser;
import vn.id.nonglam.kltn.kltn.dto.io.HotelCsv;
import vn.id.nonglam.kltn.kltn.models.hotel.Address;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IoHotelService {
    HotelRepository hotelRepository;

    @Autowired
    public IoHotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Transactional
    public UUID importHotel(File hotelFile) {
        if (hotelFile == null || !hotelFile.exists()) {
            return null;
        }
        try {
            List<HotelCsv> result = CsvParser.parseFromCsv(hotelFile, HotelCsv.class);
            //Only 1 hotel
            if (result.size() != 1) {
                return null;
            }
            HotelCsv res = result.getFirst();
            Address address = mapToAddrenss(res);
            Hotel hotel = mapToHotel(res);
            hotel.setAddress(address);
            Hotel saved = hotelRepository.save(hotel);
            return saved.getId();
        }
        catch (Exception e) {
            return null;
        }
    }


    @Transactional
    public List<UUID> importListHotel(File hotelFile) {
        if (hotelFile == null || !hotelFile.exists()) {
            return null;
        }
        List<UUID> result = new ArrayList<>();
        try {
            List<HotelCsv> parsed = CsvParser.parseFromCsv(hotelFile, HotelCsv.class);
            parsed.forEach(res -> {
                Address address = mapToAddrenss(res);
                Hotel hotel = mapToHotel(res);
                hotel.setAddress(address);
                Hotel saved = hotelRepository.save(hotel);
                result.add(saved.getId());
            });
            return result;
        }
        catch (Exception e) {
            return null;
        }
    }

    private Hotel mapToHotel(HotelCsv hotelCsv) {
        Hotel hotel = new Hotel();
        hotel.setActive(true);
        hotel.setHotline(hotelCsv.hotline());
        hotel.setName(hotelCsv.name());
        hotel.setDescription(hotelCsv.description());
        hotel.setThumbnail(hotelCsv.thumbnail());
        hotel.setCreatedAt(hotelCsv.createdAt());
        hotel.setUpdatedAt(hotelCsv.updatedAt());
        return hotel;
    }

    private Address mapToAddrenss(HotelCsv hotelCsv) {
        Address address = new Address();
        address.setStreet(hotelCsv.street());
        address.setWard(hotelCsv.ward());
        address.setPostalCode(hotelCsv.postalCode());
        address.setPostalCode(hotelCsv.postalCode());
        address.setLatitude(hotelCsv.latitude());
        address.setLongitude(hotelCsv.longitude());
        address.setCreatedAt(hotelCsv.createdAt());
        address.setUpdatedAt(hotelCsv.updatedAt());
        return address;
    }
}
