package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.DepartmentService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        UserDTO u = SessionUtil.getUser(req);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "Departments");
        return "admin/departments";
    }

    @PostMapping("/new")
    public String create(@RequestParam String name, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        departmentService.createDepartment(name.trim());
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        departmentService.toggleActive(id);
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }
}
