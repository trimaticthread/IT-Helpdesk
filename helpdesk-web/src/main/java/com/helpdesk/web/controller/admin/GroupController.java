package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.GroupService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        UserDTO u = SessionUtil.getUser(req);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        List<Group> groups = groupService.getAllGroups();
        Map<Long, List<User>> groupMembers = new LinkedHashMap<>();
        for (Group g : groups) {
            groupMembers.put(g.getId(), groupService.getUsersInGroup(g.getId()));
        }
        model.addAttribute("groups", groups);
        model.addAttribute("groupMembers", groupMembers);
        model.addAttribute("agents", userService.findByRole("AGENT"));
        model.addAttribute("pageTitle", "Groups");
        return "admin/groups";
    }

    @PostMapping("/new")
    public String create(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String email,
            HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        groupService.createGroup(name.trim(), description, email);
        return "redirect:/admin/groups";
    }

    @PostMapping("/{id}/users/add")
    public String addUser(@PathVariable Long id,
                          @RequestParam(required = false) Long userId,
                          HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        if (userId == null) return "redirect:/admin/groups";
        groupService.addUserToGroup(id, userId);
        return "redirect:/admin/groups";
    }

    @PostMapping("/{id}/users/remove")
    public String removeUser(@PathVariable Long id, @RequestParam Long userId, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        groupService.removeUserFromGroup(id, userId);
        return "redirect:/admin/groups";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        groupService.deleteGroup(id);
        return "redirect:/admin/groups";
    }
}
