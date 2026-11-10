package com.kec.noticeboard.controller;

import com.kec.noticeboard.dto.ApiResponse;
import com.kec.noticeboard.dto.NoticeResponseDto;
import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Priority;
import com.kec.noticeboard.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getPublishedNotices(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String search) {
        List<NoticeResponseDto> notices = noticeService.getPublishedNotices(category, priority, search);
        return ResponseEntity.ok(ApiResponse.success(notices));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getRecentNotices() {
        List<NoticeResponseDto> recent = noticeService.getRecentNotices();
        return ResponseEntity.ok(ApiResponse.success(recent));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getNoticeById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean incrementView) {
        NoticeResponseDto notice = noticeService.getNoticeById(id, incrementView);
        return ResponseEntity.ok(ApiResponse.success(notice));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getCategories() {
        List<Map<String, String>> categories = new ArrayList<>();
        for (Category cat : Category.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", cat.name());
            item.put("name", cat.getDisplayName());
            categories.add(item);
        }
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
