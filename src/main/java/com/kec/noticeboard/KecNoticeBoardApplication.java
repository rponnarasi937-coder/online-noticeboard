package com.kec.noticeboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KecNoticeBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(KecNoticeBoardApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("   KEC ONLINE NOTICE BOARD - APPLICATION STARTED");
        System.out.println("   Frontend & API URL: http://localhost:8080");
        System.out.println("   Default Admin Login: username=admin, password=admin123");
        System.out.println("==========================================================");
    }
}
