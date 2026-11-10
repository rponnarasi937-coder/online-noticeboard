package com.kec.noticeboard.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    private long totalNotices;
    private long publishedNotices;
    private long draftNotices;
    private long urgentNotices;
    private long highNotices;
    private Map<String, Long> categoryCounts;
    private List<NoticeResponseDto> recentNotices;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalNotices, long publishedNotices, long draftNotices, 
                             long urgentNotices, long highNotices, 
                             Map<String, Long> categoryCounts, 
                             List<NoticeResponseDto> recentNotices) {
        this.totalNotices = totalNotices;
        this.publishedNotices = publishedNotices;
        this.draftNotices = draftNotices;
        this.urgentNotices = urgentNotices;
        this.highNotices = highNotices;
        this.categoryCounts = categoryCounts;
        this.recentNotices = recentNotices;
    }

    public long getTotalNotices() {
        return totalNotices;
    }

    public void setTotalNotices(long totalNotices) {
        this.totalNotices = totalNotices;
    }

    public long getPublishedNotices() {
        return publishedNotices;
    }

    public void setPublishedNotices(long publishedNotices) {
        this.publishedNotices = publishedNotices;
    }

    public long getDraftNotices() {
        return draftNotices;
    }

    public void setDraftNotices(long draftNotices) {
        this.draftNotices = draftNotices;
    }

    public long getUrgentNotices() {
        return urgentNotices;
    }

    public void setUrgentNotices(long urgentNotices) {
        this.urgentNotices = urgentNotices;
    }

    public long getHighNotices() {
        return highNotices;
    }

    public void setHighNotices(long highNotices) {
        this.highNotices = highNotices;
    }

    public Map<String, Long> getCategoryCounts() {
        return categoryCounts;
    }

    public void setCategoryCounts(Map<String, Long> categoryCounts) {
        this.categoryCounts = categoryCounts;
    }

    public List<NoticeResponseDto> getRecentNotices() {
        return recentNotices;
    }

    public void setRecentNotices(List<NoticeResponseDto> recentNotices) {
        this.recentNotices = recentNotices;
    }
}
