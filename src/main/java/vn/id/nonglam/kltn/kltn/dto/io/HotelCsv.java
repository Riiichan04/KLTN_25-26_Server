package vn.id.nonglam.kltn.kltn.dto.io;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

public record HotelCsv(
//  Map into Hotel
        @CsvBindByName(column = "name") String name,
        @CsvBindByName(column = "description") String description,
        @CsvBindByName(column = "thumbnail") String thumbnail,
        @CsvBindByName(column = "hotline") String hotline,
//  Map into Address
        @CsvBindByName(column = "street") String street,
        @CsvBindByName(column = "ward") String ward,
        @CsvBindByName(column = "province") String province,
        @CsvBindByName(column = "postalCode") int postalCode,
        @CsvBindByName(column = "latitude") double latitude,
        @CsvBindByName(column = "longitude") double longitude,

        @CsvBindByName(column = "createdAt")
        @CsvDate("yyyy-MM-dd HH:mm:ss")
                String createdAt,

        @CsvBindByName(column = "updatedAt")
        @CsvDate("yyyy-MM-dd HH:mm:ss")
        String updatedAt
) {
}
