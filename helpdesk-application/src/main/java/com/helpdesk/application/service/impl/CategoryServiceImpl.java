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
}
