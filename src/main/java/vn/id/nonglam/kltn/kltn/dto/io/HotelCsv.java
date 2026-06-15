package vn.id.nonglam.kltn.kltn.dto.io;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
@Data
public class HotelCsv {
    @CsvBindByName(column = "name") private String name;
    @CsvBindByName(column = "description") private String description;
    @CsvBindByName(column = "thumbnail") private String thumbnail;
    @CsvBindByName(column = "images_list") private String imagesList;
    @CsvBindByName(column = "street") private String street;
    @CsvBindByName(column = "ward") private String ward;
    @CsvBindByName(column = "province") private String province;
    @CsvBindByName(column = "hotline") private String hotline;
    @CsvBindByName(column = "postalCode") private int postalCode;
    @CsvBindByName(column = "latitude") private double latitude;
    @CsvBindByName(column = "longitude") private double longitude;
}