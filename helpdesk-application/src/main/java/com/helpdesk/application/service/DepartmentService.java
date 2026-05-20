package com.helpdesk.application.service;

import com.helpdesk.domain.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    List<Department> getActiveDepartments();
    Department createDepartment(String name);
    void toggleActive(Long id);
    void deleteDepartment(Long id);
}
