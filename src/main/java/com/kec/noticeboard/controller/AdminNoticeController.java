package com.kec.noticeboard.controller;

import com.kec.noticeboard.dto.ApiResponse;
import com.kec.noticeboard.dto.DashboardStatsDto;
import com.kec.noticeboard.dto.NoticeRequestDto;
import com.kec.noticeboard.dto.NoticeResponseDto;
import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Priority;
import com.kec.noticeboard.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNoticeController {

    private final NoticeService noticeService;

    public AdminNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notices")
    public ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getAllNotices(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String search) {
        List<NoticeResponseDto> notices = noticeService.getAllNoticesForAdmin(category, priority, search);
        return ResponseEntity.ok(ApiResponse.success(notices));
    }

    @PostMapping("/notices")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> createNotice(@Valid @RequestBody NoticeRequestDto request) {
        NoticeResponseDto created = noticeService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Notice published successfully", created));
    }

    @PutMapping("/notices/{id}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestDto request) {
        NoticeResponseDto updated = noticeService.updateNotice(id, request);
        return ResponseEntity.ok(ApiResponse.success("Notice updated successfully", updated));
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(ApiResponse.success("Notice deleted successfully", null));
    }

    @PatchMapping("/notices/{id}/publish")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> togglePublishStatus(@PathVariable Long id) {
        NoticeResponseDto updated = noticeService.togglePublishStatus(id);
        String msg = updated.isPublished() ? "Notice published" : "Notice moved to drafts";
        return ResponseEntity.ok(ApiResponse.success(msg, updated));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = noticeService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
