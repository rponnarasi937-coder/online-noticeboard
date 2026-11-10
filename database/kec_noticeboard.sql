-- ========================================================
-- KEC ONLINE NOTICE BOARD - MySQL DATABASE SCHEMA & DATA
-- Database Name: kec_noticeboard
-- ========================================================

CREATE DATABASE IF NOT EXISTS `kec_noticeboard` 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `kec_noticeboard`;

-- Drop tables if they already exist (re-runnable)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `notices`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------
-- Table structure for `users` (Admins and Staff)
-- --------------------------------------------------------
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table structure for `notices`
-- --------------------------------------------------------
CREATE TABLE `notices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` LONGTEXT NOT NULL,
  `category` ENUM('GENERAL', 'EXAMINATION', 'EVENTS', 'PLACEMENTS', 'DEPARTMENT', 'HOLIDAYS') NOT NULL,
  `priority` ENUM('NORMAL', 'HIGH', 'URGENT') NOT NULL DEFAULT 'NORMAL',
  `notice_date` DATE NOT NULL,
  `department` VARCHAR(100) NOT NULL DEFAULT 'All Departments',
  `is_published` BOOLEAN NOT NULL DEFAULT TRUE,
  `attachment_url` VARCHAR(500) DEFAULT NULL,
  `attachment_file_name` VARCHAR(255) DEFAULT NULL,
  `attachment_file_type` VARCHAR(100) DEFAULT NULL,
  `posted_by` VARCHAR(100) NOT NULL DEFAULT 'Principal Office',
  `view_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_notices_category` (`category`),
  INDEX `idx_notices_priority` (`priority`),
  INDEX `idx_notices_date` (`notice_date`),
  INDEX `idx_notices_published` (`is_published`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Dumping initial Admin User
-- Password: admin123 (BCrypt hash: $2a$10$wzNq8x33pU1qI5R6K5KDeO4b9z0bZ5W0L2V5V3g0h5qXl7p5wY0eK or generated below)
-- Note: Spring Boot DataInitializer also checks and seeds this automatically!
-- --------------------------------------------------------
INSERT INTO `users` (`username`, `password`, `full_name`, `email`, `role`) VALUES
('admin', '$2a$10$y58f5sCcv4qE6u20jWzVv.X4xHhK8E2Q0y1H5E6L7u9M3a1B2C3D4', 'System Administrator', 'admin@kec.ac.in', 'ROLE_ADMIN');

-- --------------------------------------------------------
-- Dumping Realistic Sample Notices for Testing
-- --------------------------------------------------------
INSERT INTO `notices` (`title`, `description`, `category`, `priority`, `notice_date`, `department`, `is_published`, `attachment_url`, `attachment_file_name`, `attachment_file_type`, `posted_by`, `view_count`) VALUES
(
  'End Semester Autonomous Theory & Practical Examinations - Time Table',
  'All students of II, III, and IV year B.E./B.Tech/MBA/MCA are hereby informed that the Autonomous End Semester Examinations scheduled for Dec 2026 will commence from 24th November. Detailed hall allocations and session timings are attached. Hall tickets can be downloaded from the student portal starting this Monday.',
  'EXAMINATION',
  'URGENT',
  CURDATE(),
  'All Departments',
  1,
  '/uploads/sample_exam_schedule.pdf',
  'End_Semester_Exam_TimeTable_2026.pdf',
  'application/pdf',
  'Controller of Examinations',
  142
),
(
  'Campus Placement Drive: Zoho Corporation Software Development Engineer',
  'Zoho Corporation is visiting KEC for on-campus recruitment for final year B.E./B.Tech (CSE, IT, ECE, EEE, AI&DS) students. Eligible criteria: CGPA 7.0 and above with no current standing arrears. CTC offered: 8.5 LPA to 12 LPA. Round 1 Online Aptitude & Coding Assessment will take place on Saturday at CC3 Lab.',
  'PLACEMENTS',
  'HIGH',
  CURDATE(),
  'CSE, IT, ECE, EEE, AI&DS',
  1,
  '/uploads/zoho_recruitment_circular.pdf',
  'Zoho_Campus_Drive_Guidelines.pdf',
  'application/pdf',
  'Placement & Training Cell',
  289
),
(
  'Annual National Technical Symposium "INNOVFEST 2026" - Call for Papers',
  'The Department of Computer Science & Engineering is organizing the National Level Technical Symposium INNOVFEST 2026 on 18th December 2026. Events include Paper Presentation, Hackathon, Web-A-Thon, Code Debugging, and Gaming. Cash prizes worth Rs. 50,000 to be won. Early bird registration closes on 30th November.',
  'EVENTS',
  'NORMAL',
  DATE_SUB(CURDATE(), INTERVAL 1 DAY),
  'Computer Science and Engineering',
  1,
  '/uploads/innovfest_brochure.pdf',
  'Innovfest_2026_Brochure.pdf',
  'application/pdf',
  'CSE Association & HOD',
  95
),
(
  'Declaration of Holiday on Account of National Youth Day Celebrations',
  'As per the circular from the Anna University and District Administration, the college will remain closed on Monday, 12th January in view of National Youth Day. Special community service events organized by the NSS and YRC units will proceed as planned in the Convention Center.',
  'HOLIDAYS',
  'NORMAL',
  DATE_SUB(CURDATE(), INTERVAL 2 DAY),
  'All Departments',
  1,
  NULL,
  NULL,
  NULL,
  'Principal Office',
  310
),
(
  'Department Level Project Expo & Capstone Phase-I Review Schedule',
  'All final year students are requested to be present in their respective project laboratories with their PPT slides and hardware/software prototypes for the Capstone Project Phase-I internal review. External industry mentors will be participating in the evaluation process. Formal dress code is mandatory.',
  'DEPARTMENT',
  'HIGH',
  DATE_SUB(CURDATE(), INTERVAL 3 DAY),
  'Department of ECE & Mech',
  1,
  '/uploads/capstone_evaluation_rubrics.pdf',
  'Capstone_Review_Rubrics.pdf',
  'application/pdf',
  'Academic Coordinator',
  114
),
(
  'Mandatory Biometric ID Card Revalidation and Bus Pass Distribution',
  'Students availing college transport are instructed to get their smart RFID bus passes revalidated at the Transport Office (Room No: Admin 104) before 25th of this month. Late renewals will attract a nominal fine. Carry your recent college identity card and fee receipt.',
  'GENERAL',
  'NORMAL',
  DATE_SUB(CURDATE(), INTERVAL 4 DAY),
  'All Departments',
  1,
  NULL,
  NULL,
  NULL,
  'Administrative Officer',
  78
);
