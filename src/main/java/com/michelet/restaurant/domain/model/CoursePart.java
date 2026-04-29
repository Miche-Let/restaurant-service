package com.michelet.restaurant.domain.model;

import lombok.Getter;

@Getter
public enum CoursePart {

    AMUSE_BOUCHE("아뮤즈 부쉬"),
    APPETIZER("애피타이저"),
    FISH("생선"),
    MAIN("메인"),
    DESSERT("디저트");

    private final String displayName;

    CoursePart(String displayName) {
        this.displayName = displayName;
    }
}
