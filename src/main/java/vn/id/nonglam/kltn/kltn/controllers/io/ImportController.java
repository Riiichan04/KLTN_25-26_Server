package vn.id.nonglam.kltn.kltn.controllers.io;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.id.nonglam.kltn.kltn.services.io.IoHotelService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/io/data-import")
@RequiredArgsConstructor
public class ImportController {

    private final IoHotelService ioHotelService;

    @GetMapping
    public String showImportPage(Model model) {
        return "io/import-tool";
    }

    @PostMapping("/utility")
    public String importUtilityCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) return redirectError(redirectAttributes, "Vui lòng chọn file Utility!");
        try {
            File tempFile = convertMultiPartToFile(file);

            int count = ioHotelService.importUtilities(tempFile);

            tempFile.delete();

            if (count > 0) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã import thành công " + count + " Tiện ích!");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "Không có tiện ích nào được lưu. Có thể dữ liệu đã tồn tại hoặc file trống.");
            }
        } catch (Exception e) {
            return redirectError(redirectAttributes, e.getMessage());
        }
        return "redirect:/io/data-import";
    }

    @PostMapping("/hotel")
    public String importHotelCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) return redirectError(redirectAttributes, "Vui lòng chọn file Hotel!");
        try {
            File tempFile = convertMultiPartToFile(file);

            List<UUID> savedIds = ioHotelService.importListHotel(tempFile);

            tempFile.delete();

            if (savedIds != null && !savedIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMsg", "Import thành công " + savedIds.size() + " Khách sạn!");
            } else {
                return redirectError(redirectAttributes, "Lỗi: Không có dữ liệu khách sạn nào được lưu.");
            }
        } catch (Exception e) {
            return redirectError(redirectAttributes, e.getMessage());
        }
        return "redirect:/io/data-import";
    }

    @PostMapping("/room")
    public String importRoomCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) return redirectError(redirectAttributes, "Vui lòng chọn file Room Type!");
        try {
            File tempFile = convertMultiPartToFile(file);

            int count = ioHotelService.importRoomTypes(tempFile);

            tempFile.delete();

            if (count > 0) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã import thành công " + count + " Loại phòng!");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "Không có phòng nào được lưu. Có thể file lỗi hoặc chưa có Khách sạn trong DB.");
            }
        } catch (Exception e) {
            return redirectError(redirectAttributes, e.getMessage());
        }
        return "redirect:/io/data-import";
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("temp_upload_", ".csv");
        file.transferTo(convFile);
        return convFile;
    }

    private String redirectError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + message);
        return "redirect:/io/data-import";
    }
}