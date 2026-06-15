package vn.id.nonglam.kltn.kltn.services.io;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.common.utils.CsvParser;
import vn.id.nonglam.kltn.kltn.dto.io.HotelCsv;
import vn.id.nonglam.kltn.kltn.dto.io.RoomTypeCsv;
import vn.id.nonglam.kltn.kltn.models.hotel.*;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;
import vn.id.nonglam.kltn.kltn.repositories.HotelUtilityRepository;
import vn.id.nonglam.kltn.kltn.repositories.RoomTypeRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IoHotelService {
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelUtilityRepository utilityRepository;

    @Transactional
    public UUID importHotel(File hotelFile) {
        if (hotelFile == null || !hotelFile.exists()) {
            return null;
        }
        try {
            List<HotelCsv> result = CsvParser.parseFromCsv(hotelFile, HotelCsv.class);
            if (result.size() != 1) {
                return null;
            }
            HotelCsv res = result.getFirst();
            Address address = mapToAddress(res);
            Hotel hotel = mapToHotel(res);
            hotel.setAddress(address);
            Hotel saved = hotelRepository.save(hotel);
            return saved.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

       private Hotel mapToHotel(HotelCsv hotelCsv) {
        Hotel hotel = new Hotel();
        hotel.setActive(true);
        hotel.setHotline(hotelCsv.getHotline());
        hotel.setName(hotelCsv.getName());
        hotel.setDescription(hotelCsv.getDescription());
        hotel.setThumbnail(hotelCsv.getThumbnail());
        return hotel;
    }

    private Address mapToAddress(HotelCsv hotelCsv) {
        Address address = new Address();
        address.setStreet(hotelCsv.getStreet());
        address.setWard(hotelCsv.getWard());
        address.setProvince(hotelCsv.getProvince());

        if (hotelCsv.getPostalCode() != 0) {
            address.setPostalCode(hotelCsv.getPostalCode());
        }

        address.setLatitude(hotelCsv.getLatitude());
        address.setLongitude(hotelCsv.getLongitude());
        address.setActive(true);
        return address;
    }

    @Transactional
    public int importUtilities(File file) {
        if (file == null || !file.exists()) return 0;
        int count = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            List<String> existingNames = new ArrayList<>(utilityRepository.findAll().stream()
                    .map(HotelUtility::getName)
                    .toList());

            List<HotelUtility> utilitiesToSave = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split(",");
                if (columns.length > 0) {
                    String name = columns[0].trim();

                    if (name.isEmpty() || existingNames.contains(name)) continue;

                    HotelUtility utility = new HotelUtility();
                    utility.setName(name);

                    boolean isActive = true;
                    if (columns.length > 1) {
                        isActive = columns[1].trim().equalsIgnoreCase("true");
                    }

                    utility.setActive(isActive);
                    utilitiesToSave.add(utility);
                    existingNames.add(name);
                }
            }

            utilityRepository.saveAll(utilitiesToSave);
            count = utilitiesToSave.size();

        } catch (Exception e) {
            System.out.println("LỖI IMPORT TIỆN ÍCH:");
            e.printStackTrace();
        }

        return count;
    }

    @Transactional
    public List<UUID> importListHotel(File hotelFile) {
        List<UUID> result = new ArrayList<>();
        try {
            List<HotelCsv> parsed = CsvParser.parseFromCsv(hotelFile, HotelCsv.class);
            for (HotelCsv res : parsed) {
                Hotel hotel = new Hotel();
                hotel.setName(res.getName());
                hotel.setDescription(res.getDescription());
                hotel.setThumbnail(res.getThumbnail());
                hotel.setHotline(res.getHotline());

                // Address
                Address address = new Address();
                address.setStreet(res.getStreet());
                address.setWard(res.getWard());
                address.setProvince(res.getProvince());
                address.setPostalCode(res.getPostalCode());
                address.setLatitude(res.getLatitude());
                address.setLongitude(res.getLongitude());
                hotel.setAddress(address);

                // Images
                if (res.getImagesList() != null && !res.getImagesList().isEmpty()) {
                    Set<HotelImage> images = new HashSet<>();
                    for (String url : res.getImagesList().split("\\|")) {
                        HotelImage img = new HotelImage();
                        img.setPath(url.trim());
                        img.setHotel(hotel);
                        images.add(img);
                    }
                    hotel.setImages(images);
                }
                hotelRepository.save(hotel);
                result.add(hotel.getId());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    @Transactional
    public int importRoomTypes(File file) {
        try {
            List<RoomTypeCsv> parsedRooms = CsvParser.parseFromCsv(file, RoomTypeCsv.class);
            List<Hotel> allHotels = hotelRepository.findAll();
            if (allHotels.isEmpty()) return 0;

            int count = 0;
            int hotelIndex = 0;
            Random rand = new Random();

            for (RoomTypeCsv rCsv : parsedRooms) {
                RoomType room = new RoomType();
                room.setName(rCsv.getName());
                room.setDescription(rCsv.getDescription());
                room.setCapacity(rCsv.getCapacity());
                room.setPrice(rCsv.getPrice());
                room.setDepositedPercent(rCsv.getDepositedPercent());
                room.setHotel(allHotels.get(hotelIndex % allHotels.size()));

                if (rCsv.getImagesList() != null && !rCsv.getImagesList().isEmpty()) {
                    Set<RoomTypeImage> images = new HashSet<>();
                    for (String url : rCsv.getImagesList().split("\\|")) {
                        RoomTypeImage img = new RoomTypeImage();
                        img.setPath(url.trim());
                        img.setRoomType(room);
                        images.add(img);
                    }
                    room.setImages(images);
                }

                // Sinh RoomDetail ngẫu nhiên
                List<RoomDetail> details = new ArrayList<>();
                for (int i = 0; i < rand.nextInt(4) + 2; i++) {
                    RoomDetail rd = new RoomDetail();
                    rd.setRoomCode("R-" + UUID.randomUUID().toString().substring(0, 6));
                    rd.setRoomType(room);
                    details.add(rd);
                }
                room.setRoomDetails(details);

                roomTypeRepository.save(room);
                count++;
                hotelIndex++;
            }
            return count;
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }
}