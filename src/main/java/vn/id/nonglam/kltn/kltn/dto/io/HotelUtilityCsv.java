package vn.id.nonglam.kltn.kltn.dto.io;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

public record HotelUtilityCsv(
      @CsvBindByName(column = "name") String name,
      @CsvBindByName(column = "isActive") boolean isActive,

      @CsvBindByName(column = "createdAt")
      @CsvDate("yyyy-MM-dd HH:mm:ss")
      String createdAt,

      @CsvBindByName(column = "updatedAt")
      @CsvDate("yyyy-MM-dd HH:mm:ss")
      String updatedAt
) {
}
