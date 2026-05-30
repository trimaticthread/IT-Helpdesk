package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CategoryService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        UserDTO u = SessionUtil.getUser(req);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Categories");
        return "admin/categories";
    }

    @PostMapping("/new")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        try {
            categoryService.createCategory(name.trim(), description);
        } catch (com.helpdesk.domain.exception.BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Categories");
            return "admin/categories";
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        categoryService.toggleActive(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}
