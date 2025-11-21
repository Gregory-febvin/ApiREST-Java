package com.letocart.java_apirest_2026.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    public CreateCategoryRequest() {}

    public CreateCategoryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

