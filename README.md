# 🏛️ KEC Online Notice Board - Full-Stack College Web Application

A modern, responsive, and secure **College Online Notice Board Web Application** developed for Kongu Engineering College (KEC). Designed to streamline campus announcements, autonomous exam circulars, placement opportunities, symposiums, and holiday declarations.

![College Theme](https://img.shields.io/badge/Theme-Navy%20%26%20Light%20Blue-002147?style=for-the-badge)
![Tech Stack](https://img.shields.io/badge/Backend-Java%20Spring%20Boot-6db33f?style=for-the-badge&logo=springboot)
![Frontend](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20JS-e34f26?style=for-the-badge)
![Database](https://img.shields.io/badge/Database-MySQL-4479a1?style=for-the-badge&logo=mysql)
![Security](https://img.shields.io/badge/Security-Spring%20Security%20%26%20BCrypt-red?style=for-the-badge)

---

## 📌 Project Overview

Traditional paper notices on notice boards often get overlooked, delayed, or torn. The **KEC Online Notice Board** provides a centralized, instant, role-based platform where:
- **Students & Faculty** can browse, search, and filter official notices in real-time on desktop, tablet, and mobile.
- **Authorized Administrators** can log in securely with BCrypt-hashed credentials to create, edit, publish/draft, attach official circular documents (PDF/Docs/Images), and delete announcements.

---

## 🚀 Key Modules & Features

### 1. 🏠 Home Page (`index.html`)
- **Branding & Identity**: KEC Crest badge, official title, and subtitle.
- **Urgent Notice Marquee / Ticker**: Live pulsing animated ticker showing high-priority alerts.
- **Hero Section**: Welcome banner with live metric counters (Total Bulletins, Exams, Placements, Events).
- **Categories Showcase**: Quick cards linking to specific categories.
- **Latest Announcements Grid**: Visual cards with category badges, priority tags (Urgent, High, Normal), dates, departments, and "View Details" trigger.
- **Institutional Footer**: Contact desk, social links, accreditation details, and copyright.

### 2. 🔒 Secure Admin Authentication (`login.html`)
- Clean, modern college-themed login card.
- Authenticates against backend via `POST /api/auth/login`.
- **BCrypt Password Hashing**: Passwords stored as one-way cryptographic hashes.
- **Role-Based Access Control (RBAC)**: Only users with `ROLE_ADMIN` receive admin permissions.
- **Demo Auto-Fill**: One-click fill button for evaluator testing (`admin` / `admin123`).

### 3. 📊 Admin Dashboard (`admin-dashboard.html`)
- **Key Metrics Overview**: Real-time counts for Total Notices, Published Live, Drafts, and Urgent Circulars.
- **Notice Management Data Table**:
  - Columns: ID, Notice Title, Category, Priority, Date, Department, Status (Published/Draft), Attachment, and Actions.
  - Action Buttons: View Details, Toggle Publish Status (Live/Draft), Edit Notice, and Delete Notice.
- **Create / Edit Notice Modal**:
  - Notice Title
  - Category Dropdown (*Examination, Placements, Events, Department, Holidays, General*)
  - Priority Selector (*Normal, High, Urgent*)
  - Target Department / Audience
  - Notice Publication Date
  - Issuing Authority (*e.g., Principal Office, Controller of Examinations*)
  - Rich description text area
  - **Attachment Upload**: Drag-and-drop dropzone supporting PDF, Word, and Image files.
  - "Publish immediately" checkbox toggle.
- **Confirmation Modals & Toast Notifications**: Non-blocking toasts for every action.

### 4. 🎓 Student Notice Board (`notices.html`)
- Open public portal accessible by any student without login.
- **Instant Search**: Real-time filtering across titles, descriptions, and departments.
- **Category Filter Pills**: One-click pills for *All, Examination, Placements, Events, Department, Holidays, General*.
- **Priority Filter**: Filter by *Urgent, High, Normal*.
- **Sort Options**: Sort by *Latest First* or *Oldest First*.
- **Full Notice Modal**: View full notice with line breaks, department tag, and **one-click attachment download / preview** & print option.

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
| :--- | :--- |
| **Frontend** | HTML5 Semantic Markup, Modern CSS3 (CSS Variables, Flexbox, CSS Grid, Responsive Media Queries), Vanilla JavaScript (ES6+ Fetch API, DOM manipulation, LocalStorage resilience) |
| **Backend** | Java 21, Spring Boot 3.3.5 (Spring Web, Spring Data JPA, Spring Security, Spring Validation) |
| **Database** | MySQL (with H2 in-memory profile available for instant zero-config testing) |
| **Security** | Spring Security, BCryptPasswordEncoder, Stateless JWT authentication |
| **Build Tool** | Apache Maven 3.x |

---

## 📂 Project Directory Structure

```text
kec-noticeboard/
├── pom.xml                                # Maven build and dependency configuration
├── database/
│   └── kec_noticeboard.sql                # Complete standalone MySQL database schema & sample data
├── uploads/                               # File attachment storage directory
├── src/
│   ├── main/
│   │   ├── java/com/kec/noticeboard/
│   │   │   ├── KecNoticeBoardApplication.java  # Main Spring Boot Runner
│   │   │   ├── config/                    # Security, JWT, CORS, Web and Data Initializers
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   └── DataInitializer.java   # Auto-seeds default admin and sample notices
│   │   │   ├── controller/                # REST API Endpoints
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── NoticeController.java
│   │   │   │   ├── AdminNoticeController.java
│   │   │   │   └── FileController.java
│   │   │   ├── dto/                       # Request and Response Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── NoticeRequestDto.java
│   │   │   │   ├── NoticeResponseDto.java
│   │   │   │   ├── DashboardStatsDto.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── entity/                    # JPA Entities and Enums
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Notice.java
│   │   │   │   ├── Category.java
│   │   │   │   └── Priority.java
│   │   │   ├── repository/                # Spring Data JPA Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── NoticeRepository.java
│   │   │   └── service/                   # Business Logic & File Services
│   │   │       ├── AuthService.java
│   │   │       ├── CustomUserDetailsService.java
│   │   │       ├── NoticeService.java
│   │   │       └── FileStorageService.java
│   │   └── resources/
│   │       ├── application.properties     # MySQL configuration
│   │       ├── application-h2.properties  # Standalone in-memory profile
│   │       └── static/                    # Frontend Web Assets (Served at http://localhost:8080)
│   │           ├── index.html             # Home page
│   │           ├── notices.html           # Student notice board
│   │           ├── login.html             # Admin login
│   │           ├── admin-dashboard.html   # Admin dashboard
│   │           ├── css/
│   │           │   ├── styles.css         # College theme styling
│   │           │   └── dashboard.css      # Dashboard styling
│   │           └── js/
│   │               ├── api.js             # Shared API client & toasts
│   │               ├── home.js            # Home page logic
│   │               ├── notices.js         # Student filters & search
│   │               ├── auth.js            # Login handler
│   │               └── dashboard.js       # CRUD & Metrics handler
└── frontend/                              # Standalone copy for instant VS Code Live Server testing
```

---

## 🗄️ Database Setup (MySQL)

### Option 1: Automatic Spring Boot Setup (Recommended)
Spring Boot will **automatically create the database tables and seed sample data** on first run!
1. Ensure your MySQL server (e.g., MySQL Workbench or XAMPP) is running on port `3306`.
2. Open `src/main/resources/application.properties` and verify your username and password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/kec_noticeboard?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root    # Change to your MySQL password (leave empty if using XAMPP default)
   ```

### Option 2: Manual Import via MySQL CLI or phpMyAdmin
Run the provided SQL script:
```bash
mysql -u root -p < database/kec_noticeboard.sql
```
Or in **phpMyAdmin** / **MySQL Workbench**:
1. Create database: `kec_noticeboard`
2. Click **Import** and select `database/kec_noticeboard.sql`.

---

## ⚡ How to Run in VS Code

### Method A: Full-Stack Mode (Spring Boot + Frontend + MySQL)
1. **Open the project in VS Code**:
   - File -> Open Folder -> Select `d:\Java\javamini` (or your project directory).
2. **Open Integrated Terminal** (`Ctrl + \``).
3. **Run the Spring Boot Application**:
   ```bash
   mvn spring-boot:run
   ```
   *(If you want to run with the zero-config in-memory database without starting MySQL, run: `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"`)*.
4. **Open in Browser**:
   - Open: [http://localhost:8080](http://localhost:8080)
   - Home Page: `http://localhost:8080/index.html`
   - Student Board: `http://localhost:8080/notices.html`
   - Admin Login: `http://localhost:8080/login.html`
   - Admin Dashboard: `http://localhost:8080/admin-dashboard.html`

### Method B: Frontend-Only Mode (VS Code Live Server)
If you just want to demonstrate the responsive UI, search, category filters, and Admin CRUD without running Java:
1. In VS Code, install the extension **Live Server** (by Ritwick Dey).
2. Right-click `src/main/resources/static/index.html` (or `frontend/index.html`).
3. Select **"Open with Live Server"**.
4. The frontend will launch at `http://127.0.0.1:5500/` and seamlessly run with local storage sample data!

---

## 🔑 Default Administrator Credentials

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `admin123` | Full Access (Create, Edit, Delete, Publish, Upload) |

> 🔒 *Security Note: The password `admin123` is securely hashed with BCrypt (`$2a$10$...`) on the backend before validation.*

---

## 🧪 Sample Notices Included

1. **Examination (Urgent)**: End Semester Autonomous Theory & Practical Examinations - Time Table (with PDF attachment).
2. **Placements (High)**: Campus Placement Drive: Zoho Corporation Software Development Engineer (8.5 - 12 LPA).
3. **Events (Normal)**: Annual National Technical Symposium 'INNOVFEST 2026' - Call for Papers.
4. **Holidays (Normal)**: Declaration of Holiday on Account of National Youth Day Celebrations.
5. **Department (High)**: Department Level Project Expo & Capstone Phase-I Review Schedule.
6. **General (Normal)**: Mandatory Biometric ID Card Revalidation and Bus Pass Distribution.

---

## 🌐 Deploying to Obtain a Public URL

### 1. Push to GitHub
```bash
git init
git add .
git commit -m "Initial commit - KEC Online Notice Board"
git branch -M main
git remote add origin https://github.com/<your-username>/kec-noticeboard.git
git push -u origin main
```

### 2. Deploy Backend & Database on Render / Railway
1. **Database**: Create a free managed MySQL database on [Railway.app](https://railway.app) or [Aiven.io](https://aiven.io).
2. **Backend**:
   - Link your GitHub repository to [Render.com](https://render.com) or [Railway](https://railway.app).
   - Set Build Command: `mvn clean package -DskipTests`
   - Set Start Command: `java -jar target/kec-noticeboard-1.0.0.jar`
   - Add Environment Variables:
     - `SPRING_DATASOURCE_URL`: your live MySQL JDBC URL
     - `SPRING_DATASOURCE_USERNAME`: your live MySQL user
     - `SPRING_DATASOURCE_PASSWORD`: your live MySQL password
3. **Public URL**: Render/Railway will provide a live HTTPS URL such as `https://kec-noticeboard.onrender.com`.

### 3. Deploy Frontend on Vercel / Netlify / GitHub Pages (Optional)
If deploying frontend separately:
- Simply upload the `frontend/` folder to [Netlify](https://www.netlify.com/) or [Vercel](https://vercel.com/) via drag-and-drop or GitHub repository link.
- In `frontend/js/api.js`, update `API_BASE_URL` to point to your live Render backend URL.

---

## 📜 License
This project was created for educational purposes as a college mini project for Kongu Engineering College (KEC). Free for academic use and adaptation.
