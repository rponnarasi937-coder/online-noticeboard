package com.kec.noticeboard.service;

import com.kec.noticeboard.dto.DashboardStatsDto;
import com.kec.noticeboard.dto.NoticeRequestDto;
import com.kec.noticeboard.dto.NoticeResponseDto;
import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;
import com.kec.noticeboard.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // Public / Student methods
    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getPublishedNotices(Category category, Priority priority, String search) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<Notice> notices = noticeRepository.searchPublishedNotices(category, priority, cleanSearch);
        return notices.stream().map(NoticeResponseDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getRecentNotices() {
        return noticeRepository.findTop5ByIsPublishedTrueOrderByNoticeDateDescCreatedAtDesc()
                .stream()
                .map(NoticeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeResponseDto getNoticeById(Long id, boolean incrementView) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notice not found with ID: " + id));

        if (incrementView) {
            notice.setViewCount(notice.getViewCount() + 1);
            notice = noticeRepository.save(notice);
        }

        return NoticeResponseDto.fromEntity(notice);
    }

    // Admin methods
    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getAllNoticesForAdmin(Category category, Priority priority, String search) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<Notice> notices = noticeRepository.searchAllNotices(category, priority, cleanSearch);
        return notices.stream().map(NoticeResponseDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public NoticeResponseDto createNotice(NoticeRequestDto dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());
        notice.setCategory(dto.getCategory());
        notice.setPriority(dto.getPriority() != null ? dto.getPriority() : Priority.NORMAL);
        notice.setNoticeDate(dto.getNoticeDate() != null ? dto.getNoticeDate() : LocalDate.now());
        notice.setDepartment((dto.getDepartment() != null && !dto.getDepartment().isBlank()) ? dto.getDepartment() : "All Departments");
        notice.setPublished(dto.isPublished());
        notice.setAttachmentUrl(dto.getAttachmentUrl());
        notice.setAttachmentFileName(dto.getAttachmentFileName());
        notice.setAttachmentFileType(dto.getAttachmentFileType());
        notice.setPostedBy((dto.getPostedBy() != null && !dto.getPostedBy().isBlank()) ? dto.getPostedBy() : "Principal Office");
        notice.setViewCount(0);

        Notice savedNotice = noticeRepository.save(notice);
        return NoticeResponseDto.fromEntity(savedNotice);
    }

    @Transactional
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notice not found with ID: " + id));

        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());
        notice.setCategory(dto.getCategory());
        notice.setPriority(dto.getPriority());
        if (dto.getNoticeDate() != null) {
            notice.setNoticeDate(dto.getNoticeDate());
        }
        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
            notice.setDepartment(dto.getDepartment());
        }
        notice.setPublished(dto.isPublished());

        // Update attachment only if provided, or retain existing
        if (dto.getAttachmentUrl() != null) {
            notice.setAttachmentUrl(dto.getAttachmentUrl());
            notice.setAttachmentFileName(dto.getAttachmentFileName());
            notice.setAttachmentFileType(dto.getAttachmentFileType());
        }
        if (dto.getPostedBy() != null && !dto.getPostedBy().isBlank()) {
            notice.setPostedBy(dto.getPostedBy());
        }

        Notice updated = noticeRepository.save(notice);
        return NoticeResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new NoSuchElementException("Notice not found with ID: " + id);
        }
        noticeRepository.deleteById(id);
    }

    @Transactional
    public NoticeResponseDto togglePublishStatus(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notice not found with ID: " + id));

        notice.setPublished(!notice.isPublished());
        Notice updated = noticeRepository.save(notice);
        return NoticeResponseDto.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        long total = noticeRepository.count();
        long published = noticeRepository.countByIsPublishedTrue();
        long draft = total - published;
        long urgent = noticeRepository.countByPriorityAndIsPublishedTrue(Priority.URGENT);
        long high = noticeRepository.countByPriorityAndIsPublishedTrue(Priority.HIGH);

        Map<String, Long> categoryCounts = new LinkedHashMap<>();
        for (Category cat : Category.values()) {
            categoryCounts.put(cat.name(), noticeRepository.countByCategory(cat));
        }

        List<NoticeResponseDto> recent = noticeRepository.findTop5ByIsPublishedTrueOrderByNoticeDateDescCreatedAtDesc()
                .stream()
                .map(NoticeResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new DashboardStatsDto(total, published, draft, urgent, high, categoryCounts, recent);
    }
}
