package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.GroupController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GroupManagementPanel extends JPanel {

    private final GroupController groupController;
    private final UserController userController;

    private DefaultTableModel groupModel;
    private JTable groupTable;
    private DefaultTableModel memberModel;
    private JTable memberTable;

    private Long selectedGroupId = null;

    public GroupManagementPanel(GroupController groupController, UserController userController) {
        this.groupController = groupController;
        this.userController = userController;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initUI();
        loadGroups();
    }

    private void initUI() {
        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton addGroupButton = new JButton("+ Add Group");
        addGroupButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addGroupButton.setBackground(new Color(41, 98, 255));
        addGroupButton.setForeground(Color.WHITE);
        addGroupButton.setFocusPainted(false);
        addGroupButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        addGroupButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addGroupButton.addActionListener(e -> openAddGroupDialog());

        JButton deleteGroupButton = new JButton("Delete Group");
        deleteGroupButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteGroupButton.setForeground(new Color(200, 50, 50));
        deleteGroupButton.setFocusPainted(false);
        deleteGroupButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        deleteGroupButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteGroupButton.setEnabled(false);
        deleteGroupButton.addActionListener(e -> deleteSelectedGroup());

        JButton addUserButton = new JButton("Add User to Group");
        addUserButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addUserButton.setFocusPainted(false);
        addUserButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        addUserButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addUserButton.setEnabled(false);
        addUserButton.addActionListener(e -> openAddUserDialog());

        JButton removeUserButton = new JButton("Remove User");
        removeUserButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        removeUserButton.setForeground(new Color(180, 80, 0));
        removeUserButton.setFocusPainted(false);
        removeUserButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        removeUserButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeUserButton.setEnabled(false);
        removeUserButton.addActionListener(e -> removeSelectedUser());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadGroups());

        toolbar.add(addGroupButton);
        toolbar.add(deleteGroupButton);
        toolbar.add(addUserButton);
        toolbar.add(removeUserButton);
        toolbar.add(refreshButton);

        // ─── SOL: GRUP LİSTESİ ────────────────────────────────────────────────
        String[] groupCols = {"ID", "Group Name", "Email", "Members"};
        groupModel = new DefaultTableModel(groupCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        groupTable = new JTable(groupModel);
        styleTable(groupTable);
        groupTable.getColumnModel().getColumn(0).setMinWidth(0);
        groupTable.getColumnModel().getColumn(0).setMaxWidth(0);
        groupTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        groupTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        groupTable.getColumnModel().getColumn(3).setPreferredWidth(70);

        groupTable.getSelectionModel().addListSelectionListener(e -> {
            int row = groupTable.getSelectedRow();
            boolean selected = row != -1;
            deleteGroupButton.setEnabled(selected);
            addUserButton.setEnabled(selected);
            if (selected) {
                selectedGroupId = (Long) groupModel.getValueAt(row, 0);
                loadMembers(selectedGroupId);
            } else {
                selectedGroupId = null;
                memberModel.setRowCount(0);
            }
        });

        // ─── SAĞ: GRUP ÜYELERİ ───────────────────────────────────────────────
        String[] memberCols = {"ID", "Username", "Full Name", "Department"};
        memberModel = new DefaultTableModel(memberCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        memberTable = new JTable(memberModel);
        styleTable(memberTable);
        memberTable.getColumnModel().getColumn(0).setMinWidth(0);
        memberTable.getColumnModel().getColumn(0).setMaxWidth(0);
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(130);

        memberTable.getSelectionModel().addListSelectionListener(e ->
                removeUserButton.setEnabled(memberTable.getSelectedRow() != -1));

        // ─── KARTLAR ─────────────────────────────────────────────────────────
        JPanel leftCard = buildTableCard("Groups", groupTable);
        JPanel rightCard = buildTableCard("Group Members", memberTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCard, rightCard);
        splitPane.setDividerLocation(420);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setResizeWeight(0.45);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel buildTableCard(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 8, 16, 8),
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1)
        ));
        JLabel header = new JLabel("  " + title);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(80, 90, 110));
        header.setBackground(new Color(235, 238, 245));
        header.setOpaque(true);
        header.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 230, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(235, 238, 245));
        table.getTableHeader().setForeground(new Color(80, 90, 110));
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(r);
    }

    private void loadGroups() {
        List<Group> groups = groupController.getAllGroups();
        groupModel.setRowCount(0);
        for (Group g : groups) {
            int memberCount = groupController.getUsersInGroup(g.getId()).size();
            groupModel.addRow(new Object[]{g.getId(), g.getName(), g.getEmail(), memberCount});
        }
        memberModel.setRowCount(0);
        selectedGroupId = null;
    }

    private void loadMembers(Long groupId) {
        List<User> users = groupController.getUsersInGroup(groupId);
        memberModel.setRowCount(0);
        for (User u : users) {
            memberModel.addRow(new Object[]{
                u.getId(), u.getUsername(),
                u.getFirstName() + " " + u.getLastName(),
                u.getDepartment()
            });
        }
    }

    private void openAddGroupDialog() {
        JTextField nameField = new JTextField(20);
        JTextField descField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
        p.add(new JLabel("Group Name:")); p.add(nameField);
        p.add(new JLabel("Description (optional):")); p.add(descField);
        p.add(new JLabel("Email (optional):")); p.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, p, "Add Group",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                groupController.createGroup(nameField.getText(), descField.getText(), emailField.getText());
                loadGroups();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedGroup() {
        int row = groupTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) groupModel.getValueAt(row, 0);
        String name = (String) groupModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete group '" + name + "'?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                groupController.deleteGroup(id);
                loadGroups();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAddUserDialog() {
        if (selectedGroupId == null) return;
        List<com.helpdesk.application.dto.UserDTO> allUsers = userController.getAllUsers();
        List<User> members = groupController.getUsersInGroup(selectedGroupId);
        java.util.Set<Long> memberIds = new java.util.HashSet<>();
        members.forEach(m -> memberIds.add(m.getId()));

        // Grupta olmayan kullanıcıları listele
        String[] candidates = allUsers.stream()
                .filter(u -> !memberIds.contains(u.getId()))
                .map(u -> u.getId() + " — " + u.getUsername() + " (" + u.getFullName() + ")")
                .toArray(String[]::new);

        if (candidates.length == 0) {
            JOptionPane.showMessageDialog(this, "All users are already in this group.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select user to add:", "Add User to Group",
                JOptionPane.PLAIN_MESSAGE, null, candidates, candidates[0]);
        if (selected != null) {
            Long userId = Long.parseLong(selected.split(" — ")[0]);
            try {
                groupController.addUserToGroup(selectedGroupId, userId);
                loadMembers(selectedGroupId);
                loadGroups();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSelectedUser() {
        int row = memberTable.getSelectedRow();
        if (row == -1 || selectedGroupId == null) return;
        Long userId = (Long) memberModel.getValueAt(row, 0);
        String username = (String) memberModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove '" + username + "' from group?", "Confirm Remove",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            groupController.removeUserFromGroup(selectedGroupId, userId);
            loadMembers(selectedGroupId);
            loadGroups();
        }
    }
}
