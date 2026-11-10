package com.kec.noticeboard.entity;

public enum Category {
    GENERAL("General Notices"),
    EXAMINATION("Examinations & Schedules"),
    EVENTS("Events & Workshops"),
    PLACEMENTS("Placements & Internships"),
    DEPARTMENT("Department Specific"),
    HOLIDAYS("Holidays & Circulars");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
