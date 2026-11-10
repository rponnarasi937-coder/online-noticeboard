package com.kec.noticeboard.dto;

import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NoticeResponseDto {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private String categoryDisplayName;
    private Priority priority;
    private LocalDate noticeDate;
    private String department;
    private boolean published;
    private String attachmentUrl;
    private String attachmentFileName;
    private String attachmentFileType;
    private String postedBy;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NoticeResponseDto() {
    }

    public static NoticeResponseDto fromEntity(Notice notice) {
        NoticeResponseDto dto = new NoticeResponseDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setDescription(notice.getDescription());
        dto.setCategory(notice.getCategory());
        dto.setCategoryDisplayName(notice.getCategory() != null ? notice.getCategory().getDisplayName() : "");
        dto.setPriority(notice.getPriority());
        dto.setNoticeDate(notice.getNoticeDate());
        dto.setDepartment(notice.getDepartment());
        dto.setPublished(notice.isPublished());
        dto.setAttachmentUrl(notice.getAttachmentUrl());
        dto.setAttachmentFileName(notice.getAttachmentFileName());
        dto.setAttachmentFileType(notice.getAttachmentFileType());
        dto.setPostedBy(notice.getPostedBy());
        dto.setViewCount(notice.getViewCount());
        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(LocalDate noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileType() {
        return attachmentFileType;
    }

    public void setAttachmentFileType(String attachmentFileType) {
        this.attachmentFileType = attachmentFileType;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
