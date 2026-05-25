package com.helpdesk.application.service;

import com.helpdesk.domain.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAllActive();
    List<Category> getAllCategories();
    Category createCategory(String name, String description);
    void toggleActive(Long id);
    void deleteCategory(Long id);
}
