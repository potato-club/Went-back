package com.example.demo.domain;

public enum CategoryType {
    MOVIE("영화"),
    PLACE("장소"),
    BOOK("책"),
    MUSIC("음악"),
    PERFORMANCE("공연");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}