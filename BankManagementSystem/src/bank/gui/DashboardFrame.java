package bank.gui;

import bank.base.AbstractAccount;
import bank.models.*;
import bank.utils.BankService;
import bank.utils.FileHandler;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * DashboardFrame - Main application window with sidebar and content panels.
 */
public class DashboardFrame extends JFrame {

    private User currentUser;
    private BankService bankService;
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JPanel activeSidebarBtn = null;
    private String currentSection = "";

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.bankService = BankService.getInstance();
        buildUI();
    }

    private void buildUI() {
        setTitle("Nova Bank — Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(UITheme.BG_DARK);

        JScrollPane contentScroll = new JScrollPane(contentPanel);
        contentScroll.setBorder(null);
        contentScroll.setBackground(UITheme.BG_DARK);
        contentScroll.getViewport().setBackground(UITheme.BG_DARK);

        root.add(contentScroll, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);

        // Show dashboard home by default
        showOverview();
        setVisible(true);
    }

    // ==================== TOP BAR ====================

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.PRIMARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER_DARK);
                g2.fillRect(0, getHeight()-1, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Logo
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logo.setOpaque(false);
        JLabel logoCircle = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,UITheme.ACCENT,36,36,new Color(180,130,10));
                g2.setPaint(gp);
                g2.fillOval(0, 0, 36, 36);
                g2.setColor(UITheme.PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("N", (36-fm.stringWidth("N"))/2, 25);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36,36); }
        };
        JLabel bankTitle = UITheme.createLabel("NOVA BANK", new Font("Segoe UI", Font.BOLD, 20), UITheme.ACCENT);
        JLabel version = UITheme.createLabel("v2.0", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        logo.add(logoCircle); logo.add(bankTitle); logo.add(version);
        logo.setAlignmentY(CENTER_ALIGNMENT);

        // User info
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userInfo.setOpaque(false);

        String roleTag = currentUser.isAdmin() ? " 👑 Admin" : " 👤 Customer";
        JLabel roleLabel = UITheme.createLabel(roleTag, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel nameLabel = UITheme.createLabel(currentUser.getFullName(), UITheme.FONT_H3, Color.WHITE);

        JButton logoutBtn = UITheme.createOutlineButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 36));
        logoutBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) { dispose(); new LoginFrame(); }
        });

        userInfo.add(roleLabel); userInfo.add(nameLabel); userInfo.add(logoutBtn);

        bar.add(logo, BorderLayout.WEST);
        bar.add(userInfo, BorderLayout.EAST);
        return bar;
    }

    // ==================== SIDEBAR ====================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER_DARK);
                g2.fillRect(getWidth()-1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setOpaque(false);
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        String[][] navItems = {
            {"🏠", "Overview",       "overview"},
            {"➕", "Open Account",   "open"},
            {"💰", "Deposit",        "deposit"},
            {"💸", "Withdraw",       "withdraw"},
            {"🔄", "Transfer",       "transfer"},
            {"🔍", "Search Account", "search"},
            {"📋", "All Accounts",   "accounts"},
            {"📜", "Transactions",   "transactions"},
            {"✏️",  "Update Account", "update"},
            {"🗑️",  "Delete Account", "delete"},
            {"👤", "My Profile",     "profile"},
        };

        if (currentUser.isAdmin()) {
            String[][] adminItems = {{"👥", "Manage Users", "users"}, {"📊", "Reports", "reports"}};
            String[][] combined = Arrays.copyOf(navItems, navItems.length + adminItems.length);
            System.arraycopy(adminItems, 0, combined, navItems.length, adminItems.length);
            navItems = combined;
        }

        JLabel menuLabel = UITheme.createLabel("  MENU", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);
        menuLabel.setBorder(new EmptyBorder(0, 20, 8, 0));
        sidebar.add(menuLabel);

        for (String[] item : navItems) {
            JPanel btn = createNavItem(item[0], item[1], item[2]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());
        JLabel ver = UITheme.createLabel("  Nova Bank © 2026", UITheme.FONT_SMALL, new Color(60,80,110));
        ver.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(ver);

        return sidebar;
    }

    private JPanel createNavItem(String icon, String label, String section) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeSidebarBtn) {
                    GradientPaint gp = new GradientPaint(0,0,UITheme.SECONDARY,getWidth(),0,new Color(14,55,160));
                    g2.setPaint(gp);
                    g2.fillRoundRect(10, 2, getWidth()-20, getHeight()-4, 10, 10);
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRoundRect(6, getHeight()/2-10, 4, 20, 4, 4);
                }
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(220, 44));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel lbl = UITheme.createLabel(label, UITheme.FONT_NAV, UITheme.TEXT_LIGHT);

        item.add(ico); item.add(lbl);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleNav(section, item, lbl); }
            @Override public void mouseEntered(MouseEvent e) {
                if (item != activeSidebarBtn) {
                    item.setBackground(new Color(255,255,255,8));
                    lbl.setForeground(Color.WHITE);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (item != activeSidebarBtn) lbl.setForeground(UITheme.TEXT_LIGHT);
            }
        });
        return item;
    }

    private void handleNav(String section, JPanel btn, JLabel lbl) {
        if (activeSidebarBtn != null) activeSidebarBtn.repaint();
        activeSidebarBtn = btn;
        lbl.setForeground(Color.WHITE);
        btn.repaint();
        currentSection = section;

        switch (section) {
            case "overview":    showOverview(); break;
            case "open":        showOpenAccount(); break;
            case "deposit":     showDeposit(); break;
            case "withdraw":    showWithdraw(); break;
            case "transfer":    showTransfer(); break;
            case "search":      showSearch(); break;
            case "accounts":    showAllAccounts(); break;
            case "transactions":showTransactions(); break;
            case "update":      showUpdate(); break;
            case "delete":      showDelete(); break;
            case "profile":     showProfile(); break;
            case "users":       showUsers(); break;
            case "reports":     showReports(); break;
        }
    }

    // ==================== STATUS BAR ====================

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_DARK);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0, UITheme.BORDER_DARK),
                new EmptyBorder(6,20,6,20)));
        statusLabel = UITheme.createLabel("✅ Ready", UITheme.FONT_SMALL, UITheme.SUCCESS);
        JLabel time = UITheme.createLabel("Nova Bank Management System", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(time, BorderLayout.EAST);
        return bar;
    }

    private void setStatus(String msg, Color c) {
        statusLabel.setText(msg);
        statusLabel.setForeground(c);
    }

    // ==================== CONTENT SECTIONS ====================

    private void setContent(JPanel panel) {
        contentPanel.removeAll();
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setBackground(UITheme.BG_DARK);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(sp);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createContentShell(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLbl = UITheme.createLabel(title, UITheme.FONT_H1, Color.WHITE);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subLbl = UITheme.createLabel(subtitle, UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        subLbl.setAlignmentX(LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_DARK);
        sep.setMaximumSize(new Dimension(2000, 1));

        p.add(titleLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(subLbl);
        p.add(Box.createVerticalStrut(16));
        p.add(sep);
        p.add(Box.createVerticalStrut(24));
        return p;
    }

    // ==================== OVERVIEW ====================

    private void showOverview() {
        JPanel p = createContentShell("🏠 Dashboard Overview", "Welcome back, " + currentUser.getFullName());

        Map<String, AbstractAccount> all = bankService.getAllAccounts();
        long savings = all.values().stream().filter(a -> a instanceof bank.models.SavingsAccount).count();
        long current = all.values().stream().filter(a -> a instanceof bank.models.CurrentAccount).count();
        long fd      = all.values().stream().filter(a -> a instanceof bank.models.FixedDepositAccount).count();
        double total = bankService.getTotalDeposits();
        int users    = bankService.getAllUsers().size();
        int txns     = bankService.getAllTransactions().size();

        // Stats cards row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(LEFT_ALIGNMENT);

        statsRow.add(createStatCard("Total Deposits", String.format("PKR %,.0f", total), UITheme.SECONDARY, "💰"));
        statsRow.add(createStatCard("Total Accounts", String.valueOf(all.size()), new Color(16,185,129), "🏦"));
        statsRow.add(createStatCard("Transactions", String.valueOf(txns), UITheme.ACCENT, "📜"));
        statsRow.add(createStatCard("Registered Users", String.valueOf(users), new Color(168,85,247), "👥"));

        p.add(statsRow);
        p.add(Box.createVerticalStrut(28));

        // Account type breakdown
        JLabel breakdownTitle = UITheme.createLabel("Account Breakdown", UITheme.FONT_H2, Color.WHITE);
        breakdownTitle.setAlignmentX(LEFT_ALIGNMENT);
        p.add(breakdownTitle);
        p.add(Box.createVerticalStrut(14));

        JPanel breakdown = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        breakdown.setOpaque(false);
        breakdown.setAlignmentX(LEFT_ALIGNMENT);
        breakdown.add(createTypeCard("💼 Savings Accounts", savings, UITheme.SUCCESS));
        breakdown.add(createTypeCard("🏢 Current Accounts", current, UITheme.SECONDARY));
        breakdown.add(createTypeCard("🔒 Fixed Deposits", fd, UITheme.WARNING));
        p.add(breakdown);
        p.add(Box.createVerticalStrut(28));

        // Recent transactions
        JLabel recentTitle = UITheme.createLabel("Recent Transactions", UITheme.FONT_H2, Color.WHITE);
        recentTitle.setAlignmentX(LEFT_ALIGNMENT);
        p.add(recentTitle);
        p.add(Box.createVerticalStrut(14));

        List<Transaction> txList = bankService.getAllTransactions();
        int start = Math.max(0, txList.size() - 5);
        List<Transaction> recent = txList.subList(start, txList.size());
        Collections.reverse(recent);

        JPanel txPanel = buildTransactionTable(recent);
        txPanel.setAlignmentX(LEFT_ALIGNMENT);
        txPanel.setMaximumSize(new Dimension(2000, 280));
        p.add(txPanel);

        setContent(p);
    }

    private JPanel createStatCard(String title, String value, Color accent, String icon) {
        JPanel card = UITheme.createCard(UITheme.BG_CARD, 16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 120));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel ico = new JLabel(icon + "  " + title);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        ico.setForeground(UITheme.TEXT_MUTED);

        JLabel val = UITheme.createLabel(value, UITheme.FONT_BIG, accent);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JPanel accent_bar = new JPanel();
        accent_bar.setBackground(accent);
        accent_bar.setPreferredSize(new Dimension(40, 3));
        accent_bar.setMaximumSize(new Dimension(40, 3));

        card.add(ico); card.add(Box.createVerticalStrut(6));
        card.add(val); card.add(Box.createVerticalStrut(8)); card.add(accent_bar);
        return card;
    }

    private JPanel createTypeCard(String label, long count, Color color) {
        JPanel card = UITheme.createCard(UITheme.BG_CARD, 12);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 14));
        card.setPreferredSize(new Dimension(220, 64));

        JLabel cnt = UITheme.createLabel(String.valueOf(count), UITheme.FONT_H1, color);
        JLabel lbl = UITheme.createLabel(label, UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        card.add(cnt); card.add(lbl);
        return card;
    }

    private JPanel buildTransactionTable(List<Transaction> txns) {
        String[] cols = {"Transaction ID", "Account", "Type", "Amount (PKR)", "Description", "Date/Time"};
        Object[][] data = new Object[txns.size()][6];
        for (int i = 0; i < txns.size(); i++) {
            Transaction t = txns.get(i);
            String sign = (t.getType() == Transaction.Type.DEPOSIT || t.getType() == Transaction.Type.INTEREST) ? "+" : "-";
            data[i] = new Object[]{
                t.getTransactionId(), t.getAccountNumber(), t.getTransactionType(),
                sign + String.format("%,.2f", t.getAmount()), t.getDescription(), t.getFormattedTime()
            };
        }
        return createStyledTable(cols, data);
    }

    private JPanel createStyledTable(String[] cols, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_LIGHT);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(40);
        table.setSelectionBackground(UITheme.SECONDARY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(UITheme.BORDER_DARK);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(null);
        table.setFillsViewportHeight(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.PRIMARY);
        header.setForeground(UITheme.ACCENT);
        header.setFont(UITheme.FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0,UITheme.SECONDARY));
        header.setReorderingAllowed(false);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setOpaque(true);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (sel) {
                    setBackground(UITheme.SECONDARY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(r % 2 == 0 ? UITheme.BG_CARD : new Color(20, 32, 55));
                    String str = val != null ? val.toString() : "";
                    if (str.startsWith("+")) setForeground(UITheme.SUCCESS);
                    else if (str.startsWith("-")) setForeground(UITheme.DANGER);
                    else setForeground(UITheme.TEXT_LIGHT);
                }
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_DARK));
        sp.setBackground(UITheme.BG_CARD);
        sp.getViewport().setBackground(UITheme.BG_CARD);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(sp);
        return wrapper;
    }

    // ==================== OPEN ACCOUNT ====================

    private void showOpenAccount() {
        JPanel p = createContentShell("➕ Open New Account", "Create a new bank account for a customer");

        JPanel form = UITheme.createCard(UITheme.BG_CARD, 16);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(28, 32, 28, 32));
        form.setMaximumSize(new Dimension(680, 500));
        form.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField ownerField = UITheme.createStyledTextField("Account Owner Full Name");
        JTextField depositField = UITheme.createStyledTextField("Initial Deposit Amount (PKR)");
        JComboBox<String> typeCombo = UITheme.createStyledCombo(new String[]{
            "Savings Account", "Current Account", "Fixed Deposit (6 months)",
            "Fixed Deposit (12 months)", "Fixed Deposit (24 months)"
        });
        JTextField extraField = UITheme.createStyledTextField("Overdraft Limit (Current only, default 10000)");
        JLabel resultLabel = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        resultLabel.setAlignmentX(LEFT_ALIGNMENT);

        typeCombo.addActionListener(e -> {
            String sel = (String) typeCombo.getSelectedItem();
            extraField.setVisible(sel != null && sel.startsWith("Current"));
            form.revalidate();
        });
        extraField.setVisible(false);

        addFormRow(form, gbc, 0, "Account Type:", typeCombo);
        addFormRow(form, gbc, 1, "Owner Name:", ownerField);
        addFormRow(form, gbc, 2, "Initial Deposit (PKR):", depositField);
        addFormRow(form, gbc, 3, "Overdraft Limit:", extraField);

        JButton createBtn = UITheme.createAccentButton("Open Account");
        createBtn.setPreferredSize(new Dimension(200, 48));

        gbc.gridx=1; gbc.gridy=4; gbc.fill=GridBagConstraints.NONE;
        form.add(createBtn, gbc);
        gbc.gridy=5; gbc.gridwidth=2;
        form.add(resultLabel, gbc);

        createBtn.addActionListener(e -> {
            try {
                String owner = ownerField.getText().trim();
                if (owner.isEmpty()) throw new IllegalArgumentException("Owner name is required.");
                double deposit = Double.parseDouble(depositField.getText().trim());
                String type = (String) typeCombo.getSelectedItem();
                AbstractAccount acc = null;

                assert type != null;
                if (type.startsWith("Savings")) {
                    if (deposit < 1000) throw new IllegalArgumentException("Min initial deposit for Savings: PKR 1,000");
                    acc = bankService.createAccount(owner, deposit, currentUser.getUsername());
                } else if (type.startsWith("Current")) {
                    if (deposit < 5000) throw new IllegalArgumentException("Min initial deposit for Current: PKR 5,000");
                    double od = extraField.getText().trim().isEmpty() ? 10000 : Double.parseDouble(extraField.getText().trim());
                    acc = bankService.createAccount(owner, deposit, od, currentUser.getUsername());
                } else {
                    if (deposit < 10000) throw new IllegalArgumentException("Min initial deposit for FD: PKR 10,000");
                    int months = type.contains("6") ? 6 : (type.contains("12") ? 12 : 24);
                    acc = bankService.createAccount(owner, deposit, months, true, currentUser.getUsername());
                }

                resultLabel.setForeground(UITheme.SUCCESS);
                resultLabel.setText("✅ Account created! Account No: " + acc.getAccountNumber());
                ownerField.setText(""); depositField.setText(""); extraField.setText("");
                setStatus("✅ New account opened: " + acc.getAccountNumber(), UITheme.SUCCESS);
            } catch (NumberFormatException ex) {
                resultLabel.setForeground(UITheme.DANGER);
                resultLabel.setText("❌ Invalid deposit amount.");
            } catch (Exception ex) {
                resultLabel.setForeground(UITheme.DANGER);
                resultLabel.setText("❌ " + ex.getMessage());
            }
        });

        p.add(form);
        setContent(p);
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx=0; gbc.gridy=row; gbc.fill=GridBagConstraints.NONE;
        form.add(UITheme.createLabel(label, UITheme.FONT_LABEL, UITheme.TEXT_MUTED), gbc);
        gbc.gridx=1; gbc.fill=GridBagConstraints.HORIZONTAL;
        field.setPreferredSize(new Dimension(320, 44));
        form.add(field, gbc);
    }

    // ==================== DEPOSIT ====================

    private void showDeposit() {
        JPanel p = createContentShell("💰 Deposit Funds", "Add money to a bank account");
        p.add(buildTransactionForm("deposit"));
        setContent(p);
    }

    private void showWithdraw() {
        JPanel p = createContentShell("💸 Withdraw Funds", "Withdraw money from a bank account");
        p.add(buildTransactionForm("withdraw"));
        setContent(p);
    }

    private JPanel buildTransactionForm(String mode) {
        JPanel form = UITheme.createCard(UITheme.BG_CARD, 16);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(28, 32, 28, 32));
        form.setMaximumSize(new Dimension(600, 360));
        form.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField accField = UITheme.createStyledTextField("Account Number");
        JTextField amtField = UITheme.createStyledTextField("Amount (PKR)");
        JTextField descField = UITheme.createStyledTextField("Description (optional)");
        JLabel statusLbl = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);

        addFormRow(form, gbc, 0, "Account Number:", accField);
        addFormRow(form, gbc, 1, "Amount (PKR):", amtField);
        addFormRow(form, gbc, 2, "Description:", descField);

        String btnText = mode.equals("deposit") ? "Deposit  💰" : "Withdraw  💸";
        JButton btn = mode.equals("deposit") ? UITheme.createAccentButton(btnText) : UITheme.createPrimaryButton(btnText);
        btn.setPreferredSize(new Dimension(200, 48));

        gbc.gridx=1; gbc.gridy=3; form.add(btn, gbc);
        gbc.gridy=4; gbc.gridwidth=2; form.add(statusLbl, gbc);

        btn.addActionListener(e -> {
            try {
                String accNo = accField.getText().trim();
                double amt = Double.parseDouble(amtField.getText().trim());
                String desc = descField.getText().trim();
                if (accNo.isEmpty()) throw new IllegalArgumentException("Account number required.");
                if (desc.isEmpty()) {
                    if (mode.equals("deposit")) bankService.deposit(accNo, amt);
                    else bankService.withdraw(accNo, amt);
                } else {
                    if (mode.equals("deposit")) bankService.deposit(accNo, amt, desc);
                    else bankService.withdraw(accNo, amt, desc);
                }
                AbstractAccount acc = bankService.getAccount(accNo);
                statusLbl.setForeground(UITheme.SUCCESS);
                statusLbl.setText("✅ " + (mode.equals("deposit") ? "Deposit" : "Withdrawal") +
                        " successful! New Balance: PKR " + String.format("%,.2f", acc.getBalance()));
                amtField.setText(""); descField.setText("");
                setStatus("✅ " + mode + " of PKR " + amt + " processed.", UITheme.SUCCESS);
            } catch (NumberFormatException ex) {
                statusLbl.setForeground(UITheme.DANGER);
                statusLbl.setText("❌ Invalid amount.");
            } catch (Exception ex) {
                statusLbl.setForeground(UITheme.DANGER);
                statusLbl.setText("❌ " + ex.getMessage());
            }
        });
        return form;
    }

    // ==================== TRANSFER ====================

    private void showTransfer() {
        JPanel p = createContentShell("🔄 Fund Transfer", "Transfer money between accounts");

        JPanel form = UITheme.createCard(UITheme.BG_CARD, 16);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(28, 32, 28, 32));
        form.setMaximumSize(new Dimension(600, 320));
        form.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField fromField = UITheme.createStyledTextField("From Account Number");
        JTextField toField   = UITheme.createStyledTextField("To Account Number");
        JTextField amtField  = UITheme.createStyledTextField("Amount (PKR)");
        JLabel statusLbl     = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);

        addFormRow(form, gbc, 0, "From Account:", fromField);
        addFormRow(form, gbc, 1, "To Account:",   toField);
        addFormRow(form, gbc, 2, "Amount (PKR):", amtField);

        JButton btn = UITheme.createAccentButton("Transfer  🔄");
        btn.setPreferredSize(new Dimension(200, 48));
        gbc.gridx=1; gbc.gridy=3; form.add(btn, gbc);
        gbc.gridy=4; gbc.gridwidth=2; form.add(statusLbl, gbc);

        btn.addActionListener(e -> {
            try {
                String from = fromField.getText().trim();
                String to   = toField.getText().trim();
                double amt  = Double.parseDouble(amtField.getText().trim());
                if (from.isEmpty() || to.isEmpty()) throw new IllegalArgumentException("Both account numbers required.");
                if (from.equals(to)) throw new IllegalArgumentException("Cannot transfer to the same account.");
                bankService.transfer(from, to, amt);
                statusLbl.setForeground(UITheme.SUCCESS);
                statusLbl.setText("✅ Transfer of PKR " + String.format("%,.2f", amt) + " completed!");
                amtField.setText("");
                setStatus("✅ Transfer successful.", UITheme.SUCCESS);
            } catch (NumberFormatException ex) {
                statusLbl.setForeground(UITheme.DANGER); statusLbl.setText("❌ Invalid amount.");
            } catch (Exception ex) {
                statusLbl.setForeground(UITheme.DANGER); statusLbl.setText("❌ " + ex.getMessage());
            }
        });

        p.add(form);
        setContent(p);
    }

    // ==================== SEARCH ====================

    private void showSearch() {
        JPanel p = createContentShell("🔍 Search Account", "Find accounts by number or owner name");

        JPanel form = UITheme.createCard(UITheme.BG_CARD, 16);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 28, 24, 28));
        form.setMaximumSize(new Dimension(900, 600));
        form.setAlignmentX(LEFT_ALIGNMENT);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchRow.setOpaque(false);

        JTextField searchField = UITheme.createStyledTextField("Account Number or Owner Name");
        searchField.setPreferredSize(new Dimension(360, 44));
        JComboBox<String> modeCombo = UITheme.createStyledCombo(new String[]{"By Account Number", "By Owner Name"});
        modeCombo.setPreferredSize(new Dimension(200, 44));
        JButton searchBtn = UITheme.createPrimaryButton("Search 🔍");
        searchBtn.setPreferredSize(new Dimension(140, 44));

        searchRow.add(searchField); searchRow.add(modeCombo); searchRow.add(searchBtn);
        form.add(searchRow);
        form.add(Box.createVerticalStrut(20));

        JTextArea resultArea = UITheme.createStyledTextArea();
        resultArea.setPreferredSize(new Dimension(860, 320));
        JScrollPane sp = new JScrollPane(resultArea);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_DARK));
        sp.setMaximumSize(new Dimension(860, 320));
        form.add(sp);

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) { resultArea.setText("⚠️ Enter a search term."); return; }
            int mode = modeCombo.getSelectedIndex();
            StringBuilder sb = new StringBuilder();
            if (mode == 0) {
                AbstractAccount acc = bankService.searchAccount(query);
                if (acc == null) sb.append("❌ No account found with number: ").append(query);
                else sb.append(acc.getAccountDetails()).append("\n\n")
                       .append("─".repeat(60)).append("\n")
                       .append("Recent Transactions:\n")
                       .append(getRecentTxText(query));
            } else {
                List<AbstractAccount> results = bankService.searchAccount(query, true);
                if (results.isEmpty()) sb.append("❌ No accounts found for owner: ").append(query);
                else for (AbstractAccount a : results) {
                    sb.append(a.getAccountDetails()).append("\n").append("─".repeat(60)).append("\n");
                }
            }
            resultArea.setText(sb.toString());
            resultArea.setCaretPosition(0);
        });

        p.add(form);
        setContent(p);
    }

    private String getRecentTxText(String accNo) {
        List<Transaction> txs = bankService.getTransactions(accNo);
        if (txs.isEmpty()) return "(No transactions yet)";
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(5, txs.size());
        for (int i = 0; i < limit; i++) sb.append(txs.get(i).getFormattedEntry()).append("\n");
        return sb.toString();
    }

    // ==================== ALL ACCOUNTS ====================

    private void showAllAccounts() {
        JPanel p = createContentShell("📋 All Accounts", "View all bank accounts");

        Map<String, AbstractAccount> all = bankService.getAllAccounts();
        String[] cols = {"Account No", "Type", "Owner", "Balance (PKR)", "Status", "Interest/Month"};
        Object[][] data = new Object[all.size()][6];
        int i = 0;
        for (AbstractAccount acc : all.values()) {
            data[i++] = new Object[]{
                acc.getAccountNumber(), acc.getAccountType(), acc.getOwnerName(),
                String.format("%,.2f", acc.getBalance()),
                acc.isActive() ? "Active" : "Inactive",
                String.format("%,.2f", acc.calculateInterest())
            };
        }

        JPanel tablePanel = createStyledTable(cols, data);
        tablePanel.setAlignmentX(LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(2000, 500));
        p.add(tablePanel);
        setContent(p);
    }

    // ==================== TRANSACTIONS ====================

    private void showTransactions() {
        JPanel p = createContentShell("📜 Transaction History", "View transaction records");

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(LEFT_ALIGNMENT);

        JTextField accField = UITheme.createStyledTextField("Account No (leave blank for all)");
        accField.setPreferredSize(new Dimension(280, 40));
        JButton filterBtn = UITheme.createPrimaryButton("Filter");
        filterBtn.setPreferredSize(new Dimension(120, 40));
        JButton exportBtn = UITheme.createOutlineButton("Export  📄");
        exportBtn.setPreferredSize(new Dimension(140, 40));

        filterRow.add(accField); filterRow.add(filterBtn); filterRow.add(exportBtn);
        p.add(filterRow);
        p.add(Box.createVerticalStrut(16));

        JPanel[] tableHolder = {null};

        Runnable refresh = () -> {
            if (tableHolder[0] != null) p.remove(tableHolder[0]);
            String query = accField.getText().trim();
            List<Transaction> txs = query.isEmpty() ?
                    new ArrayList<>(bankService.getAllTransactions()) :
                    bankService.getTransactions(query);
            Collections.reverse(txs);

            String[] cols = {"TX ID", "Account", "Type", "Amount (PKR)", "Description", "Date/Time"};
            Object[][] data = new Object[txs.size()][6];
            for (int i2 = 0; i2 < txs.size(); i2++) {
                Transaction t = txs.get(i2);
                String sign = (t.getType() == Transaction.Type.DEPOSIT || t.getType() == Transaction.Type.INTEREST) ? "+" : "-";
                data[i2] = new Object[]{t.getTransactionId(), t.getAccountNumber(), t.getType().name(),
                        sign + String.format("%,.2f", t.getAmount()), t.getDescription(), t.getFormattedTime()};
            }
            tableHolder[0] = createStyledTable(cols, data);
            tableHolder[0].setAlignmentX(LEFT_ALIGNMENT);
            tableHolder[0].setMaximumSize(new Dimension(2000, 600));
            p.add(tableHolder[0]);
            p.revalidate(); p.repaint();
        };

        filterBtn.addActionListener(e -> refresh.run());
        exportBtn.addActionListener(e -> {
            try {
                String fn = "data/transactions_export.txt";
                String query = accField.getText().trim();
                FileHandler.exportTransactionHistory(bankService.getAllTransactions(),
                        query.isEmpty() ? "ALL" : query, fn);
                JOptionPane.showMessageDialog(this, "Exported to: " + fn, "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refresh.run();
        setContent(p);
    }

    // ==================== UPDATE ====================

    private void showUpdate() {
        JPanel p = createContentShell("✏️ Update Account", "Modify account owner information");

        JPanel form = UITheme.createCard(UITheme.BG_CARD, 16);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(28, 32, 28, 32));
        form.setMaximumSize(new Dimension(600, 320));
        form.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField accField  = UITheme.createStyledTextField("Account Number");
        JTextField nameField = UITheme.createStyledTextField("New Owner Name");
        JLabel statusLbl     = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JLabel currentInfo   = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_MUTED);

        addFormRow(form, gbc, 0, "Account Number:", accField);

        JButton fetchBtn = UITheme.createOutlineButton("Fetch Info");
        fetchBtn.setPreferredSize(new Dimension(160, 40));
        gbc.gridx=1; gbc.gridy=1; form.add(fetchBtn, gbc);
        gbc.gridy=2; gbc.gridwidth=2; form.add(currentInfo, gbc);
        gbc.gridwidth=1;
        addFormRow(form, gbc, 3, "New Owner Name:", nameField);

        JButton updateBtn = UITheme.createAccentButton("Update  ✓");
        updateBtn.setPreferredSize(new Dimension(200, 48));
        gbc.gridx=1; gbc.gridy=4; form.add(updateBtn, gbc);
        gbc.gridy=5; gbc.gridwidth=2; form.add(statusLbl, gbc);

        fetchBtn.addActionListener(e -> {
            AbstractAccount acc = bankService.searchAccount(accField.getText().trim());
            if (acc == null) { currentInfo.setForeground(UITheme.DANGER); currentInfo.setText("Account not found."); }
            else { currentInfo.setForeground(UITheme.TEXT_MUTED); currentInfo.setText("Current Owner: " + acc.getOwnerName() + " | Balance: PKR " + String.format("%,.2f", acc.getBalance())); }
        });

        updateBtn.addActionListener(e -> {
            try {
                String accNo = accField.getText().trim();
                String name  = nameField.getText().trim();
                if (accNo.isEmpty() || name.isEmpty()) throw new IllegalArgumentException("All fields required.");
                bankService.updateAccountOwner(accNo, name);
                statusLbl.setForeground(UITheme.SUCCESS);
                statusLbl.setText("✅ Account updated successfully!");
                setStatus("✅ Account " + accNo + " updated.", UITheme.SUCCESS);
            } catch (Exception ex) {
                statusLbl.setForeground(UITheme.DANGER); statusLbl.setText("❌ " + ex.getMessage());
            }
        });

        p.add(form);
        setContent(p);
    }

    // ==================== DELETE ====================

    private void showDelete() {
        JPanel p = createContentShell("🗑️ Delete Account", "Permanently remove a bank account");

        JPanel form = UITheme.createCard(new Color(60, 20, 20, 180), 16);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(28, 32, 28, 32));
        form.setMaximumSize(new Dimension(600, 280));
        form.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField accField = UITheme.createStyledTextField("Account Number to Delete");
        JLabel warningLbl = UITheme.createLabel("⚠️ This action is permanent! Account must have zero balance.", UITheme.FONT_BODY, UITheme.WARNING);
        JLabel statusLbl  = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);

        addFormRow(form, gbc, 0, "Account Number:", accField);
        gbc.gridx=0; gbc.gridy=1; gbc.gridwidth=2; form.add(warningLbl, gbc);

        JButton deleteBtn = UITheme.createDangerButton("Delete Account 🗑️");
        deleteBtn.setPreferredSize(new Dimension(220, 48));
        gbc.gridy=2; gbc.gridwidth=1; gbc.gridx=1; form.add(deleteBtn, gbc);
        gbc.gridy=3; gbc.gridwidth=2; form.add(statusLbl, gbc);

        deleteBtn.addActionListener(e -> {
            String accNo = accField.getText().trim();
            if (accNo.isEmpty()) { statusLbl.setForeground(UITheme.DANGER); statusLbl.setText("❌ Account number required."); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you absolutely sure you want to delete account: " + accNo + "?\nThis cannot be undone!",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                bankService.deleteAccount(accNo);
                statusLbl.setForeground(UITheme.SUCCESS);
                statusLbl.setText("✅ Account " + accNo + " deleted.");
                accField.setText("");
                setStatus("✅ Account deleted.", UITheme.SUCCESS);
            } catch (Exception ex) {
                statusLbl.setForeground(UITheme.DANGER); statusLbl.setText("❌ " + ex.getMessage());
            }
        });

        p.add(form);
        setContent(p);
    }

    // ==================== PROFILE ====================

    private void showProfile() {
        JPanel p = createContentShell("👤 My Profile", "View and update your account information");

        JPanel card = UITheme.createCard(UITheme.BG_CARD, 16);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(28, 32, 28, 32));
        card.setMaximumSize(new Dimension(680, 540));
        card.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField  = UITheme.createStyledTextField(currentUser.getFullName());
        JTextField emailField = UITheme.createStyledTextField(currentUser.getEmail());
        JTextField phoneField = UITheme.createStyledTextField(currentUser.getPhone());
        nameField.setText(currentUser.getFullName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());

        JPasswordField oldPassField  = UITheme.createStyledPasswordField("Current Password");
        JPasswordField newPassField  = UITheme.createStyledPasswordField("New Password");
        JLabel profileStatus = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);

        // Info display
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        infoPanel.setOpaque(false);
        infoPanel.add(UITheme.createLabel("Username: " + currentUser.getUsername(), UITheme.FONT_LABEL, UITheme.ACCENT));
        infoPanel.add(UITheme.createLabel("|", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        infoPanel.add(UITheme.createLabel("Role: " + currentUser.getRole(), UITheme.FONT_LABEL, UITheme.TEXT_MUTED));
        form: card.add(infoPanel, gbc);
        gbc.gridwidth = 1;

        addFormRow(card, gbc, 1, "Full Name:", nameField);
        addFormRow(card, gbc, 2, "Email:", emailField);
        addFormRow(card, gbc, 3, "Phone:", phoneField);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(UITheme.BORDER_DARK);
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.fill=GridBagConstraints.HORIZONTAL;
        card.add(sep2, gbc);
        gbc.fill=GridBagConstraints.NONE; gbc.gridwidth=1;

        JLabel chPwLbl = UITheme.createLabel("Change Password", UITheme.FONT_H3, UITheme.TEXT_MUTED);
        gbc.gridx=0; gbc.gridy=5; gbc.gridwidth=2; card.add(chPwLbl, gbc); gbc.gridwidth=1;

        addFormRow(card, gbc, 6, "Current Password:", oldPassField);
        addFormRow(card, gbc, 7, "New Password:", newPassField);

        JButton saveBtn = UITheme.createAccentButton("Save Changes ✓");
        saveBtn.setPreferredSize(new Dimension(220, 48));
        gbc.gridx=1; gbc.gridy=8; card.add(saveBtn, gbc);
        gbc.gridy=9; gbc.gridwidth=2; card.add(profileStatus, gbc);

        saveBtn.addActionListener(e -> {
            try {
                bankService.updateUserInfo(currentUser.getUsername(),
                        nameField.getText().trim(), emailField.getText().trim(), phoneField.getText().trim());
                String oldP = new String(oldPassField.getPassword());
                String newP = new String(newPassField.getPassword());
                if (!oldP.isEmpty() && !newP.isEmpty()) {
                    bankService.changePassword(currentUser.getUsername(), oldP, newP);
                }
                profileStatus.setForeground(UITheme.SUCCESS);
                profileStatus.setText("✅ Profile updated successfully!");
                oldPassField.setText(""); newPassField.setText("");
            } catch (Exception ex) {
                profileStatus.setForeground(UITheme.DANGER);
                profileStatus.setText("❌ " + ex.getMessage());
            }
        });

        p.add(card);
        setContent(p);
    }

    // ==================== ADMIN: USERS ====================

    private void showUsers() {
        JPanel p = createContentShell("👥 User Management", "Manage system users (Admin only)");
        Map<String, User> users = bankService.getAllUsers();
        String[] cols = {"Username", "Full Name", "Email", "Phone", "Role", "Status", "Created"};
        Object[][] data = new Object[users.size()][7];
        int i = 0;
        for (User u : users.values()) {
            data[i++] = new Object[]{u.getUsername(), u.getFullName(), u.getEmail(), u.getPhone(),
                    u.getRole(), u.isActive() ? "Active" : "Inactive",
                    u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate().toString() : ""};
        }
        JPanel table = createStyledTable(cols, data);
        table.setAlignmentX(LEFT_ALIGNMENT);
        table.setMaximumSize(new Dimension(2000, 500));
        p.add(table);
        setContent(p);
    }

    // ==================== ADMIN: REPORTS ====================

    private void showReports() {
        JPanel p = createContentShell("📊 Reports & Export", "Generate and export reports");

        JPanel card = UITheme.createCard(UITheme.BG_CARD, 16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setMaximumSize(new Dimension(600, 400));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = UITheme.createLabel("Export Options", UITheme.FONT_H2, Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title); card.add(Box.createVerticalStrut(20));

        JLabel statusLbl = UITheme.createLabel("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        statusLbl.setAlignmentX(LEFT_ALIGNMENT);

        String[][] reports = {
            {"📋 All Accounts Report", "accounts"},
            {"📜 All Transactions", "transactions"},
        };

        for (String[] r : reports) {
            JButton btn = UITheme.createPrimaryButton(r[0]);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(320, 48));
            btn.addActionListener(e -> {
                try {
                    if (r[1].equals("accounts")) {
                        String fn = "data/accounts_report.txt";
                        FileHandler.exportAccountsReport(bankService.getAllAccounts(), fn);
                        statusLbl.setForeground(UITheme.SUCCESS);
                        statusLbl.setText("✅ Exported to: " + fn);
                    } else {
                        String fn = "data/all_transactions.txt";
                        FileHandler.exportTransactionHistory(bankService.getAllTransactions(), "ALL", fn);
                        statusLbl.setForeground(UITheme.SUCCESS);
                        statusLbl.setText("✅ Exported to: " + fn);
                    }
                } catch (Exception ex) {
                    statusLbl.setForeground(UITheme.DANGER);
                    statusLbl.setText("❌ Export failed: " + ex.getMessage());
                }
            });
            card.add(btn); card.add(Box.createVerticalStrut(12));
        }

        card.add(Box.createVerticalStrut(8));
        card.add(statusLbl);

        // Summary stats
        card.add(Box.createVerticalStrut(20));
        JLabel statsLbl = UITheme.createLabel("System Summary", UITheme.FONT_H3, UITheme.TEXT_MUTED);
        statsLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(statsLbl); card.add(Box.createVerticalStrut(10));

        String[] summaryLines = {
            "Total Accounts: " + bankService.getAllAccounts().size(),
            "Total Users: " + bankService.getAllUsers().size(),
            "Total Transactions: " + bankService.getAllTransactions().size(),
            "Total Deposits: PKR " + String.format("%,.2f", bankService.getTotalDeposits()),
        };
        for (String line : summaryLines) {
            JLabel l = UITheme.createLabel("  • " + line, UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
            l.setAlignmentX(LEFT_ALIGNMENT);
            card.add(l); card.add(Box.createVerticalStrut(4));
        }

        p.add(card);
        setContent(p);
    }
}
