package com.helpdesk.application.service.impl;

import com.helpdesk.application.service.DepartmentService;
import com.helpdesk.domain.entity.Department;
import com.helpdesk.domain.exception.BusinessException;
import com.helpdesk.persistence.dao.DepartmentDAO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentServiceImpl(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    @Override
    public List<Department> getActiveDepartments() {
        return departmentDAO.findAllActive();
    }

    @Override
    public Department createDepartment(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Department name cannot be empty.");
        }
        if (departmentDAO.findByName(name.trim()).isPresent()) {
            throw new BusinessException("A department with this name already exists.");
        }
        Department dept = new Department();
        dept.setName(name.trim());
        dept.setIsActive(true);
        return departmentDAO.save(dept);
    }

    @Override
    public void toggleActive(Long id) {
        Department dept = departmentDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Department not found: " + id));
        dept.setIsActive(!dept.getIsActive());
        departmentDAO.update(dept);
    }

    @Override
    public void deleteDepartment(Long id) {
        Department dept = departmentDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Department not found: " + id));
        if (departmentDAO.countUsersByDepartmentName(dept.getName()) > 0) {
            throw new BusinessException("Cannot delete: this department has active users.");
        }
        departmentDAO.deleteById(id);
    }
}
