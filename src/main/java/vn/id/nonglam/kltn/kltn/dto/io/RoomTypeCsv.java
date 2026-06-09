package vn.id.nonglam.kltn.kltn.dto.io;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class RoomTypeCsv {
        @CsvBindByName(column = "name") private String name;
        @CsvBindByName(column = "description") private String description;
        @CsvBindByName(column = "capacity") private int capacity;
        @CsvBindByName(column = "price") private BigDecimal price;
        @CsvBindByName(column = "depositedPercent") private double depositedPercent;
        @CsvBindByName(column = "images_list") private String imagesList;
}