package com.kec.noticeboard.dao;

import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class NoticeDAO {

    private final List<Notice> memoryStore = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    public NoticeDAO() {
        seedSampleData();
        if (DatabaseManager.isMySqlConnected()) {
            syncToDatabase();
        }
    }

    private void seedSampleData() {
        memoryStore.add(new Notice(
                "End Semester Autonomous Theory & Practical Examinations - Time Table",
                "All students of II, III, and IV year B.E./B.Tech/MBA/MCA are hereby informed that the Autonomous End Semester Examinations scheduled for Dec 2026 will commence from 24th November. Detailed hall allocations and session timings are attached. Hall tickets can be downloaded from the student portal starting this Monday.",
                Category.EXAMINATION,
                Priority.URGENT,
                LocalDate.now(),
                "All Departments",
                true,
                "uploads/End_Semester_Exam_TimeTable_2026.pdf",
                "End_Semester_Exam_TimeTable_2026.pdf",
                "application/pdf",
                "Controller of Examinations"
        ));
        memoryStore.get(0).setId(1L);

        memoryStore.add(new Notice(
                "Campus Placement Drive: Zoho Corporation Software Development Engineer",
                "Zoho Corporation is visiting KEC for on-campus recruitment for final year B.E./B.Tech (CSE, IT, ECE, EEE, AI&DS) students. Eligible criteria: CGPA 7.0 and above with no current standing arrears. CTC offered: 8.5 LPA to 12 LPA. Round 1 Online Aptitude & Coding Assessment will take place on Saturday at CC3 Lab.",
                Category.PLACEMENTS,
                Priority.HIGH,
                LocalDate.now(),
                "CSE, IT, ECE, EEE, AI&DS",
                true,
                null,
                null,
                null,
                "Placement & Training Cell"
        ));
        memoryStore.get(1).setId(2L);

        memoryStore.add(new Notice(
                "Annual National Technical Symposium 'INNOVFEST 2026' - Call for Papers",
                "The Department of Computer Science & Engineering is organizing the National Level Technical Symposium INNOVFEST 2026 on 18th December 2026. Events include Paper Presentation, Hackathon, Web-A-Thon, Code Debugging, and Gaming. Cash prizes worth Rs. 50,000 to be won. Early bird registration closes on 30th November.",
                Category.EVENTS,
                Priority.NORMAL,
                LocalDate.now().minusDays(1),
                "Computer Science and Engineering",
                true,
                null,
                null,
                null,
                "CSE Association & HOD"
        ));
        memoryStore.get(2).setId(3L);

        memoryStore.add(new Notice(
                "Declaration of Holiday on Account of National Youth Day Celebrations",
                "As per the circular from Anna University and District Administration, the college will remain closed on Monday, 12th January in view of National Youth Day. Special community service events organized by the NSS and YRC units will proceed as planned in the Convention Center.",
                Category.HOLIDAYS,
                Priority.NORMAL,
                LocalDate.now().minusDays(2),
                "All Departments",
                true,
                null,
                null,
                null,
                "Principal Office"
        ));
        memoryStore.get(3).setId(4L);

        memoryStore.add(new Notice(
                "Department Level Project Expo & Capstone Phase-I Review Schedule",
                "All final year students are requested to be present in their respective project laboratories with their PPT slides and hardware/software prototypes for the Capstone Project Phase-I internal review. External industry mentors will be participating in the evaluation process. Formal dress code is mandatory.",
                Category.DEPARTMENT,
                Priority.HIGH,
                LocalDate.now().minusDays(3),
                "Department of ECE & Mech",
                true,
                null,
                null,
                null,
                "Academic Coordinator"
        ));
        memoryStore.get(4).setId(5L);

        memoryStore.add(new Notice(
                "Mandatory Biometric ID Card Revalidation and Bus Pass Distribution",
                "Students availing college transport are instructed to get their smart RFID bus passes revalidated at the Transport Office (Room No: Admin 104) before 25th of this month. Late renewals will attract a nominal fine. Carry your recent college identity card and fee receipt.",
                Category.GENERAL,
                Priority.NORMAL,
                LocalDate.now().minusDays(4),
                "All Departments",
                true,
                null,
                null,
                null,
                "Administrative Officer"
        ));
        memoryStore.get(5).setId(6L);
    }

    private void syncToDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            Statement check = conn.createStatement();
            ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM notices");
            if (rs.next() && rs.getInt(1) == 0) {
                String sql = "INSERT INTO notices (title, description, category, priority, notice_date, department, is_published, attachment_url, attachment_file_name, attachment_file_type, posted_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (Notice n : memoryStore) {
                        ps.setString(1, n.getTitle());
                        ps.setString(2, n.getDescription());
                        ps.setString(3, n.getCategory().name());
                        ps.setString(4, n.getPriority().name());
                        ps.setDate(5, java.sql.Date.valueOf(n.getNoticeDate()));
                        ps.setString(6, n.getDepartment());
                        ps.setBoolean(7, n.isPublished());
                        ps.setString(8, n.getAttachmentUrl());
                        ps.setString(9, n.getAttachmentFileName());
                        ps.setString(10, n.getAttachmentFileType());
                        ps.setString(11, n.getPostedBy());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (Exception e) {
            System.err.println("Database sync note: " + e.getMessage());
        }
    }

    public synchronized List<Notice> getAllNotices() {
        if (DatabaseManager.isMySqlConnected()) {
            List<Notice> dbList = new ArrayList<>();
            String sql = "SELECT * FROM notices ORDER BY notice_date DESC, id DESC";
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    dbList.add(mapResultSetToNotice(rs));
                }
                return dbList;
            } catch (SQLException e) {
                System.err.println("Error reading notices from MySQL: " + e.getMessage());
            }
        }
        return new ArrayList<>(memoryStore);
    }

    public synchronized List<Notice> getPublishedNotices() {
        return getAllNotices().stream().filter(Notice::isPublished).collect(Collectors.toList());
    }

    public synchronized List<Notice> searchNotices(Category category, Priority priority, String search, boolean publishedOnly) {
        List<Notice> list = publishedOnly ? getPublishedNotices() : getAllNotices();

        return list.stream().filter(n -> {
            boolean matchCat = (category == null || n.getCategory() == category);
            boolean matchPri = (priority == null || n.getPriority() == priority);
            boolean matchSearch = (search == null || search.isBlank() ||
                    n.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                    n.getDescription().toLowerCase().contains(search.toLowerCase()) ||
                    n.getDepartment().toLowerCase().contains(search.toLowerCase()));
            return matchCat && matchPri && matchSearch;
        }).collect(Collectors.toList());
    }

    public synchronized Notice addNotice(Notice notice) {
        if (notice.getNoticeDate() == null) {
            notice.setNoticeDate(LocalDate.now());
        }

        if (DatabaseManager.isMySqlConnected()) {
            String sql = "INSERT INTO notices (title, description, category, priority, notice_date, department, is_published, attachment_url, attachment_file_name, attachment_file_type, posted_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, notice.getTitle());
                ps.setString(2, notice.getDescription());
                ps.setString(3, notice.getCategory().name());
                ps.setString(4, notice.getPriority().name());
                ps.setDate(5, java.sql.Date.valueOf(notice.getNoticeDate()));
                ps.setString(6, notice.getDepartment());
                ps.setBoolean(7, notice.isPublished());
                ps.setString(8, notice.getAttachmentUrl());
                ps.setString(9, notice.getAttachmentFileName());
                ps.setString(10, notice.getAttachmentFileType());
                ps.setString(11, notice.getPostedBy());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    notice.setId(rs.getLong(1));
                }
            } catch (SQLException e) {
                System.err.println("Error inserting into MySQL: " + e.getMessage());
            }
        }

        if (notice.getId() == null) {
            notice.setId(idGenerator.incrementAndGet());
        }
        memoryStore.add(0, notice);
        return notice;
    }

    public synchronized boolean updateNotice(Notice notice) {
        if (DatabaseManager.isMySqlConnected() && notice.getId() != null) {
            String sql = "UPDATE notices SET title=?, description=?, category=?, priority=?, notice_date=?, department=?, is_published=?, attachment_url=?, attachment_file_name=?, attachment_file_type=?, posted_by=? WHERE id=?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, notice.getTitle());
                ps.setString(2, notice.getDescription());
                ps.setString(3, notice.getCategory().name());
                ps.setString(4, notice.getPriority().name());
                ps.setDate(5, java.sql.Date.valueOf(notice.getNoticeDate()));
                ps.setString(6, notice.getDepartment());
                ps.setBoolean(7, notice.isPublished());
                ps.setString(8, notice.getAttachmentUrl());
                ps.setString(9, notice.getAttachmentFileName());
                ps.setString(10, notice.getAttachmentFileType());
                ps.setString(11, notice.getPostedBy());
                ps.setLong(12, notice.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating notice in MySQL: " + e.getMessage());
            }
        }

        for (int i = 0; i < memoryStore.size(); i++) {
            if (Objects.equals(memoryStore.get(i).getId(), notice.getId())) {
                memoryStore.set(i, notice);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteNotice(Long id) {
        if (DatabaseManager.isMySqlConnected()) {
            String sql = "DELETE FROM notices WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error deleting from MySQL: " + e.getMessage());
            }
        }
        return memoryStore.removeIf(n -> Objects.equals(n.getId(), id));
    }

    public synchronized boolean togglePublish(Long id) {
        Notice target = null;
        for (Notice n : memoryStore) {
            if (Objects.equals(n.getId(), id)) {
                n.setPublished(!n.isPublished());
                target = n;
                break;
            }
        }
        if (target != null && DatabaseManager.isMySqlConnected()) {
            String sql = "UPDATE notices SET is_published = ? WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBoolean(1, target.isPublished());
                ps.setLong(2, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating publish status in MySQL: " + e.getMessage());
            }
        }
        return target != null && target.isPublished();
    }

    private Notice mapResultSetToNotice(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setId(rs.getLong("id"));
        n.setTitle(rs.getString("title"));
        n.setDescription(rs.getString("description"));
        n.setCategory(Category.valueOf(rs.getString("category")));
        n.setPriority(Priority.valueOf(rs.getString("priority")));
        java.sql.Date d = rs.getDate("notice_date");
        n.setNoticeDate(d != null ? d.toLocalDate() : LocalDate.now());
        n.setDepartment(rs.getString("department"));
        n.setPublished(rs.getBoolean("is_published"));
        n.setAttachmentUrl(rs.getString("attachment_url"));
        n.setAttachmentFileName(rs.getString("attachment_file_name"));
        n.setAttachmentFileType(rs.getString("attachment_file_type"));
        n.setPostedBy(rs.getString("posted_by"));
        n.setViewCount(rs.getInt("view_count"));
        return n;
    }
}
