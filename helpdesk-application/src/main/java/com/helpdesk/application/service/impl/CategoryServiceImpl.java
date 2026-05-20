package com.helpdesk.application.service.impl;

import com.helpdesk.application.service.CategoryService;
import com.helpdesk.domain.entity.Category;
import com.helpdesk.domain.exception.BusinessException;
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
        if (name == null || name.isBlank()) {
            throw new BusinessException("Category name cannot be empty.");
        }
        if (categoryDAO.findByName(name.trim()).isPresent()) {
            throw new BusinessException("A category with this name already exists.");
        }
        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : "");
        category.setIsActive(true);
        return categoryDAO.save(category);
    }

    @Override
    public void toggleActive(Long id) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found: " + id));
        category.setIsActive(!category.getIsActive());
        categoryDAO.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (categoryDAO.countTicketsByCategoryId(id) > 0) {
            throw new BusinessException("Cannot delete: this category has associated tickets.");
        }
        categoryDAO.deleteById(id);
    }
}
