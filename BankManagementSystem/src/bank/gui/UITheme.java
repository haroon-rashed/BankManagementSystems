package bank.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * UITheme - centralized styling for a gorgeous, modern bank UI.
 */
public class UITheme {

    // ===== COLOR PALETTE =====
    public static final Color PRIMARY        = new Color(10,  25,  60);   // Deep navy
    public static final Color SECONDARY      = new Color(26,  86, 219);   // Royal blue
    public static final Color ACCENT         = new Color(251, 191,  36);  // Gold
    public static final Color SUCCESS        = new Color( 16, 185, 129);  // Emerald green
    public static final Color DANGER         = new Color(239,  68,  68);  // Red
    public static final Color WARNING        = new Color(245, 158,  11);  // Amber
    public static final Color BG_DARK        = new Color( 15,  23,  42);  // Almost black
    public static final Color BG_CARD        = new Color( 30,  41,  59);  // Dark card
    public static final Color BG_LIGHT       = new Color(248, 250, 252);  // Off white
    public static final Color TEXT_LIGHT     = new Color(226, 232, 240);  // Light gray
    public static final Color TEXT_MUTED     = new Color(148, 163, 184);  // Muted
    public static final Color TEXT_DARK      = new Color( 15,  23,  42);  // Dark text
    public static final Color BORDER_DARK    = new Color( 51,  65,  85);  // Dark border
    public static final Color GRADIENT_TOP   = new Color(10, 25, 60);
    public static final Color GRADIENT_BOT   = new Color(30, 58, 138);
    public static final Color SIDEBAR_BG     = new Color(15, 23, 50);
    public static final Color SIDEBAR_HOVER  = new Color(30, 58, 138);
    public static final Color SIDEBAR_ACTIVE = new Color(26, 86, 219);

    // ===== FONTS =====
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_H1     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_H3     = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_NAV    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_LOGO   = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font FONT_BIG    = new Font("Segoe UI", Font.BOLD,  36);

    // ===== FACTORY METHODS =====

    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_TOP, getWidth(), getHeight(), GRADIENT_BOT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(SECONDARY.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(SECONDARY.brighter());
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, SECONDARY, 0, getHeight(), new Color(14, 55, 160));
                    g2.setPaint(gp);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(180, 44));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createAccentButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? ACCENT.darker() : (getModel().isRollover() ? ACCENT.brighter() : ACCENT);
                GradientPaint gp = new GradientPaint(0, 0, base, 0, getHeight(), base.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(PRIMARY);
                g2.setFont(FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(180, 44));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(255,255,255,30) : new Color(0,0,0,0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                g2.setColor(TEXT_LIGHT);
                g2.setFont(FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(180, 44));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,15));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(isFocusOwner() ? SECONDARY : BORDER_DARK);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(ACCENT);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        field.setPreferredSize(new Dimension(300, 44));
        field.putClientProperty("placeholder", placeholder);
        return field;
    }

    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,15));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(isFocusOwner() ? SECONDARY : BORDER_DARK);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(ACCENT);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        field.setPreferredSize(new Dimension(300, 44));
        return field;
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JPanel createCard(Color bg, int arc) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(BORDER_DARK);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                g2.dispose();
            }
            @Override
            public boolean isOpaque() { return false; }
        };
    }

    public static JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_CARD);
        combo.setForeground(TEXT_LIGHT);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        combo.setPreferredSize(new Dimension(300, 44));
        return combo;
    }

    public static JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(FONT_MONO);
        area.setBackground(new Color(10, 20, 40));
        area.setForeground(new Color(100, 220, 140));
        area.setCaretColor(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        area.setEditable(false);
        return area;
    }

    public static void applyGlobalLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("Panel.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_LIGHT);
    }

    // Draw a decorative circle (used in backgrounds)
    public static void drawGlowCircle(Graphics2D g2, int cx, int cy, int radius, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = radius; i > 0; i -= 5) {
            float alpha = (float)(radius - i) / radius * 0.15f;
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha*255)));
            g2.fillOval(cx - i, cy - i, i*2, i*2);
        }
    }
}
