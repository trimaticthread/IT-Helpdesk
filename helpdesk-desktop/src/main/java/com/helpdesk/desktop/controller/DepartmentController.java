package com.helpdesk.desktop.controller;

import com.helpdesk.application.service.DepartmentService;
import com.helpdesk.domain.entity.Department;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    public List<Department> getActiveDepartments() {
        return departmentService.getActiveDepartments();
    }

    public Department createDepartment(String name) {
        return departmentService.createDepartment(name);
    }

    public void toggleActive(Long id) {
        departmentService.toggleActive(id);
    }

    public void deleteDepartment(Long id) {
        departmentService.deleteDepartment(id);
    }
}
