package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.DepartmentService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final DepartmentService departmentService;

    public UserController(UserService userService, DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        UserDTO u = SessionUtil.getUser(req);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pageTitle", "Users");
        return "admin/users";
    }

    @GetMapping("/new")
    public String newUserForm(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "New User");
        return "admin/create-user";
    }

    @PostMapping("/new")
    public String createUser(
            @RequestParam String username,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String department,
            HttpServletRequest req, Model model) {

        if (!isAdmin(req)) return "redirect:/access-denied";

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists.");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "admin/create-user";
        }

        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setDepartment(department);
        dto.setIsActive(true);

        userService.createUser(dto, password, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        userService.resetPassword(id);
        return "redirect:/admin/users";
    }
}
