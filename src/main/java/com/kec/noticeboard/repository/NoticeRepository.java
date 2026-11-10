package com.kec.noticeboard.repository;

import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // For public / student portal: published notices sorted latest first
    List<Notice> findByIsPublishedTrueOrderByNoticeDateDescCreatedAtDesc();

    // For admin dashboard: all notices sorted latest first
    List<Notice> findAllByOrderByNoticeDateDescCreatedAtDesc();

    // Category filter for published notices
    List<Notice> findByCategoryAndIsPublishedTrueOrderByNoticeDateDescCreatedAtDesc(Category category);

    // Advanced search query with optional filters for public
    @Query("SELECT n FROM Notice n WHERE n.isPublished = true " +
           "AND (:category IS NULL OR n.category = :category) " +
           "AND (:priority IS NULL OR n.priority = :priority) " +
           "AND (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(n.department) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY n.noticeDate DESC, n.createdAt DESC")
    List<Notice> searchPublishedNotices(@Param("category") Category category,
                                        @Param("priority") Priority priority,
                                        @Param("search") String search);

    // Advanced search for admin
    @Query("SELECT n FROM Notice n WHERE " +
           "(:category IS NULL OR n.category = :category) " +
           "AND (:priority IS NULL OR n.priority = :priority) " +
           "AND (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(n.department) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY n.noticeDate DESC, n.createdAt DESC")
    List<Notice> searchAllNotices(@Param("category") Category category,
                                  @Param("priority") Priority priority,
                                  @Param("search") String search);

    // Dashboard metrics
    long countByIsPublishedTrue();

    long countByPriorityAndIsPublishedTrue(Priority priority);

    long countByCategory(Category category);

    // Latest N notices for home page marquee / highlights
    List<Notice> findTop5ByIsPublishedTrueOrderByNoticeDateDescCreatedAtDesc();
}
