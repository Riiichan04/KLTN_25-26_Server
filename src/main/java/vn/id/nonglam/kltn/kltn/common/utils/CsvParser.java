package vn.id.nonglam.kltn.kltn.common.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class CsvParser {
    public static <T> List<T> parseFromCsv(File csvFile, Class<T> tClass) {
        if (csvFile == null || !csvFile.exists()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(csvFile);
            return parseCsv(fis, tClass);
        } catch (Exception e) {
            return null;
        }
    }


    private static <T> List<T> parseCsv(InputStream inputStream, Class<T> tclass) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return new CsvToBeanBuilder<T>(br)
                    .withType(tclass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
        }
        catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
