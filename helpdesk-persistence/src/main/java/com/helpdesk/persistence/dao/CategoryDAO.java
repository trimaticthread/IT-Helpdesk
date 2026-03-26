package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO {

    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);

    List<Category> findAll();
    List<Category> findAllActive();

    Category save(Category category);

    void update(Category category);
    void deleteById(Long id);

}
