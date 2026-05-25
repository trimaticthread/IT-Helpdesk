package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.CategoryController;
import com.helpdesk.desktop.controller.DepartmentController;
import com.helpdesk.desktop.controller.GroupController;
import com.helpdesk.desktop.controller.SlaController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;
import com.helpdesk.domain.enums.TicketStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ADMIN rolune ozel ana dashboard ekrani.
 * Sistemin tam yetkili panelidir; ticket'lar ve yönetim işlemleri üzerinde
 * tam CRUD yetkisi bulunur.
 *
 * Sekmeler:
 * - Tickets        : Tüm ticket'ları listeler. Durum değiştirme, atama, silme.
 * - Reports        : Duruma, önceliğe, kategoriye ve atanan agente göre istatistikler.
 * - Users          : Kullanıcı yönetimi (oluşturma, düzenleme, şifre sıfırlama).
 * - Categories     : Kategori CRUD.
 * - Departments    : Departman CRUD.
 * - Groups         : Grup yönetimi (kullanıcı atama).
 * - SLA Settings   : SLA kuralları.
 */
public class DashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private final CategoryController categoryController;
    private final DepartmentController departmentController;
    private final GroupController groupController;
    private final SlaController slaController;

    private DefaultTableModel tableModel;
    private JTable ticketTable;
    private JPanel reportsPanel;
    private List<TicketDTO> currentTickets = new java.util.ArrayList<>();

    public DashboardFrame(AuthController authController, TicketController ticketController,
                          UserController userController, CategoryController categoryController,
                          DepartmentController departmentController, GroupController groupController,
                          SlaController slaController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        this.categoryController = categoryController;
        this.departmentController = departmentController;
        this.groupController = groupController;
        this.slaController = slaController;
        initUI();
        loadTickets();
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
            new LoginFrame(authController, ticketController, userController,
                    categoryController, departmentController, groupController, slaController)
                    .setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ─── SEKMELER ────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Operasyonel sekmeler
        tabs.addTab("Tickets", buildTicketsPanel());
        tabs.addTab("Reports", buildReportsPanel());

        // Yönetim sekmeleri
        tabs.addTab("Users",       new UserManagementPanel(userController, departmentController));
        tabs.addTab("Categories",  new CategoryManagementPanel(categoryController));
        tabs.addTab("Departments", new DepartmentManagementPanel(departmentController));
        tabs.addTab("Groups",      new GroupManagementPanel(groupController, userController));
        tabs.addTab("SLA Settings", new SlaManagementPanel(slaController));

        // Reports sekmesine geçildiğinde istatistikler anında yenilenir
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) refreshReports();
        });

        root.add(tabs, BorderLayout.CENTER);
    }

    /**
     * "Tickets" sekmesinin içeriğini oluşturur.
     * Toolbar butonları + ticket tablosunu içerir.
     */
    private JPanel buildTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton newTicketButton = new JButton("+ New Ticket");
        newTicketButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newTicketButton.setBackground(new Color(41, 98, 255));
        newTicketButton.setForeground(Color.WHITE);
        newTicketButton.setFocusPainted(false);
        newTicketButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newTicketButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newTicketButton.addActionListener(e -> openCreateTicketDialog());

        JButton changeStatusButton = new JButton("Change Status");
        changeStatusButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        changeStatusButton.setFocusPainted(false);
        changeStatusButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        changeStatusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeStatusButton.setEnabled(false);
        changeStatusButton.addActionListener(e -> openChangeStatusDialog());

        JButton assignButton = new JButton("Assign Ticket");
        assignButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        assignButton.setFocusPainted(false);
        assignButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        assignButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        assignButton.setEnabled(false);
        assignButton.addActionListener(e -> openAssignDialog());

        JButton deleteButton = new JButton("Delete Ticket");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.setForeground(new Color(200, 50, 50));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedTicket());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTickets());

        toolbar.add(newTicketButton);
        toolbar.add(changeStatusButton);
        toolbar.add(assignButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);
        panel.add(toolbar, BorderLayout.NORTH);

        // ─── TİCKET TABLOSU ──────────────────────────────────────────────────
        String[] columns = {"ID", "Ticket No", "Title", "Status", "Priority", "Requester", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        ticketTable = new JTable(tableModel);
        ticketTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ticketTable.setRowHeight(32);
        ticketTable.setShowGrid(false);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setSelectionBackground(new Color(220, 230, 255));
        ticketTable.setSelectionForeground(new Color(30, 40, 60));
        ticketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ticketTable.getTableHeader().setBackground(new Color(235, 238, 245));
        ticketTable.getTableHeader().setForeground(new Color(80, 90, 110));

        // ID kolonu gizlenir
        ticketTable.getColumnModel().getColumn(0).setMinWidth(0);
        ticketTable.getColumnModel().getColumn(0).setMaxWidth(0);
        ticketTable.getColumnModel().getColumn(0).setWidth(0);

        int[] widths = {0, 110, 220, 100, 80, 140, 120, 100};
        for (int i = 1; i < widths.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = ticketTable.getSelectedRow() != -1;
            changeStatusButton.setEnabled(selected);
            assignButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        ticketTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = ticketTable.getSelectedRow();
                    if (row >= 0 && row < currentTickets.size()) {
                        new ViewTicketDialog(
                                DashboardFrame.this,
                                currentTickets.get(row),
                                ticketController,
                                true
                        ).setVisible(true);
                        loadTickets();
                    }
                }
            }
        });

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                }
                return this;
            }
        };
        for (int i = 0; i < columns.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 16, 16, 16),
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    /**
     * "Reports" sekmesinin boş container'ını oluşturur.
     * İçerik, sekme açıldığında refreshReports() ile dinamik doldurulur.
     */
    private JPanel buildReportsPanel() {
        reportsPanel = new JPanel();
        reportsPanel.setLayout(new BoxLayout(reportsPanel, BoxLayout.Y_AXIS));
        reportsPanel.setBackground(new Color(245, 247, 250));
        reportsPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        JScrollPane scroll = new JScrollPane(reportsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(245, 247, 250));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Reports sekmesini güncel verilerle yeniden doldurur.
     */
    private void refreshReports() {
        reportsPanel.removeAll();

        List<TicketDTO> all = ticketController.getAllTickets();
        int total = all.size();

        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getStatus, Collectors.counting()));
        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getPriority, Collectors.counting()));
        Map<String, Long> byCategory = all.stream()
                .filter(t -> t.getCategoryName() != null)
                .collect(Collectors.groupingBy(TicketDTO::getCategoryName, Collectors.counting()));
        Map<String, Long> byAssignee = all.stream()
                .filter(t -> t.getAssigneeName() != null)
                .collect(Collectors.groupingBy(TicketDTO::getAssigneeName, Collectors.counting()));

        JLabel titleLabel = new JLabel("Ticket Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportsPanel.add(titleLabel);
        reportsPanel.add(Box.createVerticalStrut(20));

        reportsPanel.add(makeSectionLabel("Summary"));
        reportsPanel.add(makeStatRow("Total Tickets", String.valueOf(total)));
        reportsPanel.add(makeStatRow("Unassigned",
                String.valueOf(all.stream().filter(t -> t.getAssigneeName() == null).count())));
        reportsPanel.add(Box.createVerticalStrut(20));

        reportsPanel.add(makeSectionLabel("By Status"));
        for (Map.Entry<String, Long> e : byStatus.entrySet()) {
            reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
        }
        reportsPanel.add(Box.createVerticalStrut(20));

        reportsPanel.add(makeSectionLabel("By Priority"));
        for (Map.Entry<String, Long> e : byPriority.entrySet()) {
            reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
        }
        reportsPanel.add(Box.createVerticalStrut(20));

        reportsPanel.add(makeSectionLabel("By Category"));
        if (byCategory.isEmpty()) {
            reportsPanel.add(makeStatRow("No category data", "-"));
        } else {
            for (Map.Entry<String, Long> e : byCategory.entrySet()) {
                reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
            }
        }
        reportsPanel.add(Box.createVerticalStrut(20));

        reportsPanel.add(makeSectionLabel("By Assignee"));
        if (byAssignee.isEmpty()) {
            reportsPanel.add(makeStatRow("No assignments yet", "-"));
        } else {
            for (Map.Entry<String, Long> e : byAssignee.entrySet()) {
                reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
            }
        }

        reportsPanel.revalidate();
        reportsPanel.repaint();
    }

    private JLabel makeSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 80, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel makeStatRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        row.setBackground(new Color(245, 247, 250));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(500, 30));

        JLabel lbl = new JLabel(label + ":  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(80, 90, 110));
        lbl.setPreferredSize(new Dimension(200, 24));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(new Color(41, 98, 255));

        row.add(lbl);
        row.add(val);
        return row;
    }

    private void openCreateTicketDialog() {
        CreateTicketDialog dialog = new CreateTicketDialog(this, ticketController);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            loadTickets();
        }
    }

    private void openChangeStatusDialog() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0);

        TicketStatus[] options = {
            TicketStatus.OPEN, TicketStatus.IN_PROGRESS, TicketStatus.PENDING,
            TicketStatus.RESOLVED, TicketStatus.CLOSED
        };
        TicketStatus selected = (TicketStatus) JOptionPane.showInputDialog(
                this, "Select new status:", "Change Status",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (selected != null) {
            try {
                ticketController.updateStatus(ticketId, selected);
                loadTickets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAssignDialog() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0);

        List<UserDTO> agents = ticketController.getAgents();
        if (agents.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No active agents found in the system.",
                    "No Agents", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDTO selected = (UserDTO) JOptionPane.showInputDialog(
                this,
                "Select agent to assign:",
                "Assign Ticket",
                JOptionPane.PLAIN_MESSAGE,
                null,
                agents.toArray(),
                agents.get(0));

        if (selected != null) {
            try {
                ticketController.assignTicket(ticketId, selected.getId());
                loadTickets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedTicket() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0);
        String ticketNo = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete ticket '" + ticketNo + "'?\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ticketController.deleteTicket(ticketId);
                loadTickets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTickets() {
        currentTickets = ticketController.getAllTickets();
        tableModel.setRowCount(0);
        for (TicketDTO t : currentTickets) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getTicketNumber(),
                t.getTitle(),
                t.getStatus(),
                t.getPriority(),
                t.getRequesterName(),
                t.getCategoryName(),
                t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : ""
            });
        }
    }
}
