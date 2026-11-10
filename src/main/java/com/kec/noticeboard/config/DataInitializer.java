package com.kec.noticeboard.config;

import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;
import com.kec.noticeboard.entity.Role;
import com.kec.noticeboard.entity.User;
import com.kec.noticeboard.repository.NoticeRepository;
import com.kec.noticeboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final PasswordEncoder passwordEncoder;
    private final String uploadDir;

    public DataInitializer(UserRepository userRepository,
                           NoticeRepository noticeRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.upload.dir:uploads/}") String uploadDir) {
        this.userRepository = userRepository;
        this.noticeRepository = noticeRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadDir = uploadDir;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Create sample attachment file if not exists
        Path sampleFile = uploadPath.resolve("End_Semester_Exam_TimeTable_2026.pdf");
        if (!Files.exists(sampleFile)) {
            String dummyContent = "%PDF-1.4\n1 0 obj << /Title (KEC End Semester Exam Timetable 2026) >> endobj\ntrailer << /Root 1 0 R >> %%EOF";
            Files.write(sampleFile, dummyContent.getBytes(StandardCharsets.UTF_8));
        }

        // Initialize default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "KEC Admin Controller",
                    "admin@kec.ac.in",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            System.out.println(">>> Initialized default Admin: username='admin', password='admin123'");
        }

        // Seed initial notices if database is empty
        if (noticeRepository.count() == 0) {
            List<Notice> sampleNotices = Arrays.asList(
                new Notice(
                    "End Semester Autonomous Theory & Practical Examinations - Time Table",
                    "All students of II, III, and IV year B.E./B.Tech/MBA/MCA are hereby informed that the Autonomous End Semester Examinations scheduled for Dec 2026 will commence from 24th November. Detailed hall allocations and session timings are attached. Hall tickets can be downloaded from the student portal starting this Monday.",
                    Category.EXAMINATION,
                    Priority.URGENT,
                    LocalDate.now(),
                    "All Departments",
                    true,
                    "/uploads/End_Semester_Exam_TimeTable_2026.pdf",
                    "End_Semester_Exam_TimeTable_2026.pdf",
                    "application/pdf",
                    "Controller of Examinations"
                ),
                new Notice(
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
                ),
                new Notice(
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
                ),
                new Notice(
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
                ),
                new Notice(
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
                ),
                new Notice(
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
                )
            );

            noticeRepository.saveAll(sampleNotices);
            System.out.println(">>> Initialized 6 sample notices across categories!");
        }
    }
}
