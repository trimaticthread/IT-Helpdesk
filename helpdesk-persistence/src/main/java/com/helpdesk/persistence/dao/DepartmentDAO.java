package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentDAO {
    List<Department> findAll();
    List<Department> findAllActive();
    Optional<Department> findById(Long id);
    Optional<Department> findByName(String name);
    Department save(Department department);
    void update(Department department);
    void deleteById(Long id);
    int countUsersByDepartmentName(String name);
}
