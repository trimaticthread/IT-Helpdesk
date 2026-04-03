package com.helpdesk.application.service;

import com.helpdesk.domain.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAllActive();
}
