package bank.gui;

import bank.models.User;
import bank.utils.BankService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private BankService bankService;
    private JTabbedPane tabPane;

    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JLabel loginStatusLabel;

    private JTextField signupUserField, signupFullNameField, signupEmailField, signupPhoneField;
    private JPasswordField signupPassField, signupConfirmPassField;
    private JLabel signupStatusLabel;

    public LoginFrame() {
        bankService = BankService.getInstance();
        UITheme.applyGlobalLook();
        buildUI();
    }

    private void buildUI() {
        setTitle("Nova Bank — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_DARK,
                        getWidth(), getHeight(), new Color(20, 40, 100));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                UITheme.drawGlowCircle(g2, 120, 120, 200, UITheme.SECONDARY);
                UITheme.drawGlowCircle(g2, getWidth() - 100, getHeight() - 80, 220, UITheme.ACCENT);
            }
        };
        root.setOpaque(false);
        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    // ==================== LEFT PANEL ====================

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(360, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 30, 0, 30));

        JPanel logoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.ACCENT, 80, 80, new Color(180, 130, 10));
                g2.setPaint(gp);
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(UITheme.PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("N", (80 - fm.stringWidth("N")) / 2, 54);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
            @Override public Dimension getMinimumSize()   { return new Dimension(80, 80); }
        };
        logoCircle.setOpaque(false);
        logoCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankName = UITheme.createLabel("NOVA BANK", UITheme.FONT_LOGO, UITheme.ACCENT);
        bankName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = UITheme.createLabel("Your Trusted Financial Partner",
                UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(260, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(logoCircle);
        content.add(Box.createVerticalStrut(14));
        content.add(bankName);
        content.add(Box.createVerticalStrut(6));
        content.add(tagline);
        content.add(Box.createVerticalStrut(18));
        content.add(sep);
        content.add(Box.createVerticalStrut(22));

        String[] features = {
            "  \u2713  Bank-grade Security",
            "  \u2713  Multi-Account Support",
            "  \u2713  Real-time Analytics",
            "  \u2713  Savings, Current & FD",
            "  \u2713  Instant Transfers",
            "  \u2713  Complete File Storage"
        };
        for (String f : features) {
            JLabel lbl = UITheme.createLabel(f, UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(lbl);
            content.add(Box.createVerticalStrut(10));
        }

        panel.add(content);
        return panel;
    }

    // ==================== RIGHT PANEL ====================

    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 25, 55, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(UITheme.BORDER_DARK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        card.setBorder(new EmptyBorder(24, 36, 24, 36));
        card.setPreferredSize(new Dimension(500, 570));
        card.setMinimumSize(new Dimension(440, 500));

        // ---- Tab pane with NO emoji, fixed-width tabs ----
        tabPane = new JTabbedPane(JTabbedPane.TOP);
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabPane.setOpaque(false);
        tabPane.setForeground(UITheme.TEXT_LIGHT);

        tabPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight      = UITheme.BG_DARK;
                lightHighlight = UITheme.SECONDARY;
                shadow         = UITheme.BORDER_DARK;
                darkShadow     = UITheme.BORDER_DARK;
                focus          = UITheme.SECONDARY;
            }
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? UITheme.SECONDARY : new Color(255, 255, 255, 12));
                g2.fillRoundRect(x + 3, y + 3, w - 6, h - 2, 8, 8);
                // selected = white text, unselected = muted
                g2.setColor(isSelected ? Color.WHITE : UITheme.TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String lbl = tabIndex == 0 ? "  LOGIN  " : "  SIGN UP  ";
                g2.drawString(lbl, x + (w - fm.stringWidth(lbl)) / 2,
                        y + (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override
            protected void paintTabBorder(Graphics g, int tp2, int i,
                    int x, int y, int w, int h, boolean s) { }
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement,
                    int selectedIndex) { }
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 170;
            }
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 40;
            }
        });

        tabPane.addTab("LOGIN",   buildLoginTab());
        tabPane.addTab("SIGN UP", buildSignupTab());

        card.add(tabPane, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    // ==================== LOGIN TAB ====================

    private JPanel buildLoginTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 2, 8, 2));

        JLabel title = UITheme.createLabel("Welcome Back!", UITheme.FONT_H1, Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = UITheme.createLabel("Sign in to your account",
                UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        loginUserField   = UITheme.createStyledTextField("Enter username");
        loginPassField   = UITheme.createStyledPasswordField("Enter password");
        loginStatusLabel = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.DANGER);
        loginStatusLabel.setAlignmentX(LEFT_ALIGNMENT);

        JButton loginBtn = UITheme.createAccentButton("Sign In");
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.addActionListener(e -> doLogin());

        loginPassField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        JPanel hint = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        hint.setOpaque(false);
        hint.setBorder(BorderFactory.createLineBorder(new Color(26, 86, 219, 70), 1));
        hint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        hint.setAlignmentX(LEFT_ALIGNMENT);
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED));

        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(26));
        addField(p, "Username", loginUserField);
        p.add(Box.createVerticalStrut(14));
        addField(p, "Password", loginPassField);
        p.add(Box.createVerticalStrut(6));
        p.add(loginStatusLabel);
        p.add(Box.createVerticalStrut(18));
        p.add(loginBtn);
        p.add(Box.createVerticalStrut(14));
        p.add(hint);
        return p;
    }

    // ==================== SIGNUP TAB ====================

    private JPanel buildSignupTab() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(18, 2, 12, 2));

        JLabel title = UITheme.createLabel("Create Account", UITheme.FONT_H1, Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = UITheme.createLabel("Join Nova Bank today",
                UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        signupFullNameField    = UITheme.createStyledTextField("e.g. Ali Hassan");
        signupUserField        = UITheme.createStyledTextField("min 4 characters");
        signupEmailField       = UITheme.createStyledTextField("e.g. ali@email.com");
        signupPhoneField       = UITheme.createStyledTextField("e.g. 0300-1234567");
        signupPassField        = UITheme.createStyledPasswordField("6+ chars, 1 uppercase, 1 digit");
        signupConfirmPassField = UITheme.createStyledPasswordField("Re-enter password");
        signupStatusLabel      = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.DANGER);
        signupStatusLabel.setAlignmentX(LEFT_ALIGNMENT);

        JButton btn = UITheme.createPrimaryButton("Create Account");
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.addActionListener(e -> doSignup());

        inner.add(title);
        inner.add(Box.createVerticalStrut(4));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(18));
        addField(inner, "Full Name",        signupFullNameField);
        inner.add(Box.createVerticalStrut(12));
        addField(inner, "Username",         signupUserField);
        inner.add(Box.createVerticalStrut(12));
        addField(inner, "Email Address",    signupEmailField);
        inner.add(Box.createVerticalStrut(12));
        addField(inner, "Phone Number",     signupPhoneField);
        inner.add(Box.createVerticalStrut(12));
        addField(inner, "Password",         signupPassField);
        inner.add(Box.createVerticalStrut(12));
        addField(inner, "Confirm Password", signupConfirmPassField);
        inner.add(Box.createVerticalStrut(8));
        inner.add(signupStatusLabel);
        inner.add(Box.createVerticalStrut(14));
        inner.add(btn);
        inner.add(Box.createVerticalStrut(8));

        JScrollPane scroll = new JScrollPane(inner,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ==================== HELPERS ====================

    private void addField(JPanel parent, String labelText, JComponent field) {
        JLabel lbl = UITheme.createLabel(labelText, UITheme.FONT_LABEL, UITheme.TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(5));
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        parent.add(field);
    }

    private void doLogin() {
        String user = loginUserField.getText().trim();
        String pass = new String(loginPassField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            setStatus(loginStatusLabel, "Please fill all fields.", UITheme.WARNING);
            return;
        }
        try {
            User loggedIn = bankService.login(user, pass);
            setStatus(loginStatusLabel, "Welcome " + loggedIn.getFullName(), UITheme.SUCCESS);
            Timer t = new Timer(600, e -> { dispose(); new DashboardFrame(loggedIn); });
            t.setRepeats(false);
            t.start();
        } catch (Exception ex) {
            setStatus(loginStatusLabel, ex.getMessage(), UITheme.DANGER);
            loginPassField.setText("");
        }
    }

    private void doSignup() {
        String username = signupUserField.getText().trim();
        String fullName = signupFullNameField.getText().trim();
        String email    = signupEmailField.getText().trim();
        String phone    = signupPhoneField.getText().trim();
        String pass     = new String(signupPassField.getPassword());
        String confirm  = new String(signupConfirmPassField.getPassword());

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            setStatus(signupStatusLabel, "All fields are required.", UITheme.WARNING);
            return;
        }
        if (username.length() < 4) {
            setStatus(signupStatusLabel, "Username must be at least 4 characters.", UITheme.WARNING);
            return;
        }
        if (!pass.equals(confirm)) {
            setStatus(signupStatusLabel, "Passwords do not match.", UITheme.DANGER);
            return;
        }
        try {
            bankService.signup(username, pass, fullName, email, phone);
            setStatus(signupStatusLabel, "Account created! Please login.", UITheme.SUCCESS);
            clearSignupFields();
            Timer t = new Timer(1200, e -> tabPane.setSelectedIndex(0));
            t.setRepeats(false);
            t.start();
        } catch (Exception ex) {
            setStatus(signupStatusLabel, ex.getMessage(), UITheme.DANGER);
        }
    }

    private void setStatus(JLabel lbl, String msg, Color color) {
        lbl.setText(msg);
        lbl.setForeground(color);
    }

    private void clearSignupFields() {
        signupUserField.setText(""); signupFullNameField.setText("");
        signupEmailField.setText(""); signupPhoneField.setText("");
        signupPassField.setText(""); signupConfirmPassField.setText("");
    }
}