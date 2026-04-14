package vn.id.nonglam.kltn.kltn.dto.io;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

public record RoomTypeCsv(
        @CsvBindByName(column = "name") String name,
        @CsvBindByName(column = "description") String description,
        @CsvBindByName(column = "capacity") int capacity,
        @CsvBindByName(column = "price") double price,
        @CsvBindByName(column = "depositedPercent") double depositedPercent,

        @CsvBindByName(column = "createdAt")
        @CsvDate("yyyy-MM-dd HH:mm:ss")
                String createdAt,

        @CsvBindByName(column = "updatedAt")
        @CsvDate("yyyy-MM-dd HH:mm:ss")
        String updatedAt
) {
}
