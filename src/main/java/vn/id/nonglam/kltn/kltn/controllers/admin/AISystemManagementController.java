package vn.id.nonglam.kltn.kltn.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.nonglam.kltn.kltn.services.admin.AdminAISystemService;

import java.util.Map;

@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
public class AISystemManagementController {
    private final AdminAISystemService adminAISystemService;

    @PostMapping("/batch-analyze")
    public ResponseEntity<Map<String, Object>> batchAnalyze() {
        return ResponseEntity.ok(adminAISystemService.batchAnalyze());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(adminAISystemService.getDashboard());
    }
}
