package com.letocart.java_apirest_2026.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class CreateBookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private Set<Integer> categoryIds;

    public CreateBookRequest() {}

    public CreateBookRequest(String title, Set<Integer> categoryIds) {
        this.title = title;
        this.categoryIds = categoryIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }
}

