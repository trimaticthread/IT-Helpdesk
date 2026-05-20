package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.CategoryController;
import com.helpdesk.desktop.controller.DepartmentController;
import com.helpdesk.desktop.controller.GroupController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private final CategoryController categoryController;
    private final DepartmentController departmentController;
    private final GroupController groupController;

    public DashboardFrame(AuthController authController, TicketController ticketController,
                          UserController userController, CategoryController categoryController,
                          DepartmentController departmentController, GroupController groupController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        this.categoryController = categoryController;
        this.departmentController = departmentController;
        this.groupController = groupController;
        initUI();
    }

    private void initUI() {
        setTitle("IT Helpdesk — Admin");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel appTitle = new JLabel("IT Helpdesk — Admin Panel");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "Admin";
        JLabel welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(60, 70, 95));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            authController.logout();
            dispose();
            new LoginFrame(authController, ticketController, userController, categoryController, departmentController, groupController).setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ─── TABBED PANE ─────────────────────────────────────────────────────
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBorder(new EmptyBorder(8, 8, 8, 8));

        tabbedPane.addTab("Users", new UserManagementPanel(userController, departmentController));
        tabbedPane.addTab("Categories", new CategoryManagementPanel(categoryController));
        tabbedPane.addTab("Departments", new DepartmentManagementPanel(departmentController));
        tabbedPane.addTab("Groups", new GroupManagementPanel(groupController, userController));

        root.add(tabbedPane, BorderLayout.CENTER);
    }
}