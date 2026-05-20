package com.helpdesk.desktop.controller;

import com.helpdesk.application.service.CategoryService;
import com.helpdesk.domain.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public Category createCategory(String name, String description) {
        return categoryService.createCategory(name, description);
    }

    public void toggleActive(Long id) {
        categoryService.toggleActive(id);
    }

    public void deleteCategory(Long id) {
        categoryService.deleteCategory(id);
    }
}
