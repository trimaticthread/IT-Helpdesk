package com.helpdesk.application.service.impl;

import com.helpdesk.application.service.CategoryService;
import com.helpdesk.domain.entity.Category;
import com.helpdesk.persistence.dao.CategoryDAO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public List<Category> findAllActive() {
        return categoryDAO.findAllActive();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);
        return categoryDAO.save(category);
    }

    @Override
    public void toggleActive(Long id) {
        categoryDAO.findById(id).ifPresent(cat -> {
            cat.setIsActive(!cat.getIsActive());
            categoryDAO.update(cat);
        });
    }

    @Override
    public void deleteCategory(Long id) {
        categoryDAO.deleteById(id);
    }
}
