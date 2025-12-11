import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class CalculatorApp extends JFrame {

    // Modes
    private enum Mode {
        STANDARD, SCIENTIFIC, PROGRAMMER
    }

    private Mode currentMode = Mode.STANDARD;

    // UI
    private JTextField displayField;
    private JLabel historyLabel;
    private JLabel modeLabel;
    private JPanel buttonPanel;

    // Logique
    private String operator = "";
    private double firstOperand = 0;
    private boolean startNewNumber = true;
    private final ArrayList<String> history = new ArrayList<>();

    // Couleurs / thème (dark fixe pour un rendu 2025)
    private Color bgPrimary = new Color(18, 18, 18);
    private Color bgSecondary = new Color(28, 28, 30);
    private Color bgTertiary = new Color(44, 44, 46);
    private Color accentColor = new Color(10, 132, 255);
    private Color textPrimary = new Color(255, 255, 255);
    private Color textSecondary = new Color(152, 152, 157);
    private Color operatorColor = new Color(255, 159, 10);
    private Color numberColor = new Color(58, 58, 60);

    public CalculatorApp() {
        setTitle("CalculatorApp Pro 2025");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal avec gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, bgPrimary, 0, getHeight(), bgSecondary);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top (titre + affichage)
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Boutons (dépend du mode)
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        updateButtonsForMode(); // initialise layout selon mode standard

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        // Barre de titre + mode + boutons de mode
        JPanel menuBar = createMenuBar();
        topPanel.add(menuBar, BorderLayout.NORTH);

        // Affichage
        displayField = new JTextField("0");
        displayField.setFont(new Font("SF Pro Display", Font.BOLD, 46));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(false);
        displayField.setBorder(new EmptyBorder(10, 20, 10, 20));
        displayField.setBackground(bgSecondary);
        displayField.setForeground(textPrimary);
        displayField.setCaretColor(textPrimary);

        historyLabel = new JLabel(" ");
        historyLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        historyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        historyLabel.setForeground(textSecondary);
        historyLabel.setBorder(new EmptyBorder(8, 20, 0, 20));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(bgSecondary);
        displayPanel.setBorder(new RoundedBorder(20, bgTertiary));
        displayPanel.add(historyLabel, BorderLayout.NORTH);
        displayPanel.add(displayField, BorderLayout.CENTER);

        topPanel.add(displayPanel, BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createMenuBar() {
        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setOpaque(false);

        // Titre + label de mode
        JLabel titleLabel = new JLabel("CalculatorApp Pro");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        titleLabel.setForeground(textPrimary);

        modeLabel = new JLabel("Mode : Standard");
        modeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        modeLabel.setForeground(textSecondary);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(modeLabel);

        // 3 boutons = modes (remplacent totalement les anciennes fonctions)
        JPanel modeButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        modeButtonsPanel.setOpaque(false);

        JButton stdBtn = createModeButton("STD", "Mode Standard", Mode.STANDARD);
        JButton sciBtn = createModeButton("SCI", "Mode Scientifique", Mode.SCIENTIFIC);
        JButton progBtn = createModeButton("PROG", "Mode Programmeur", Mode.PROGRAMMER);

        modeButtonsPanel.add(stdBtn);
        modeButtonsPanel.add(sciBtn);
        modeButtonsPanel.add(progBtn);

        menuBar.add(leftPanel, BorderLayout.WEST);
        menuBar.add(modeButtonsPanel, BorderLayout.EAST);

        return menuBar;
    }

    private JButton createModeButton(String text, String tooltip, Mode mode) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SF Pro Display", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(textSecondary);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(60, 26));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(accentColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (currentMode == mode) {
                    btn.setForeground(accentColor);
                } else {
                    btn.setForeground(textSecondary);
                }
            }
        });

        btn.addActionListener(e -> setMode(mode, btn));
        // Colorer le bouton du mode initial
        if (mode == currentMode) {
            btn.setForeground(accentColor);
        }

        return btn;
    }

    private void setMode(Mode newMode, JButton clickedButton) {
        currentMode = newMode;
        switch (currentMode) {
            case STANDARD -> modeLabel.setText("Mode : Standard");
            case SCIENTIFIC -> modeLabel.setText("Mode : Scientifique");
            case PROGRAMMER -> modeLabel.setText("Mode : Programmeur");
        }

        // Recolorer tous les boutons de mode
        Container parent = clickedButton.getParent();
        for (Component c : parent.getComponents()) {
            if (c instanceof JButton b) {
                b.setForeground(b == clickedButton ? accentColor : textSecondary);
            }
        }

        clear();
        updateButtonsForMode();
    }

    // Met à jour le panel de boutons selon le mode
    private void updateButtonsForMode() {
        buttonPanel.removeAll();

        String[][] layout;
        switch (currentMode) {
            case STANDARD -> layout = getStandardLayout();
            case SCIENTIFIC -> layout = getScientificLayout();
            case PROGRAMMER -> layout = getProgrammerLayout();
            default -> layout = getStandardLayout();
        }

        buttonPanel.setLayout(new GridLayout(layout.length, 4, 12, 12));
        for (String[] row : layout) {
            for (String text : row) {
                JButton btn = createStyledButton(text);
                buttonPanel.add(btn);
            }
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    // Layouts des différents modes
    private String[][] getStandardLayout() {
        return new String[][]{
                {"C", "⌫", "%", "÷"},
                {"√", "x²", "xʸ", "×"},
                {"7", "8", "9", "-"},
                {"4", "5", "6", "+"},
                {"1", "2", "3", "="},
                {"±", "0", ".", "="}
        };
    }

    private String[][] getScientificLayout() {
        return new String[][]{
                {"C", "⌫", "sin", "cos"},
                {"tan", "ln", "log", "÷"},
                {"√", "x²", "xʸ", "×"},
                {"7", "8", "9", "-"},
                {"4", "5", "6", "+"},
                {"±", "0", ".", "="}
        };
    }

    private String[][] getProgrammerLayout() {
        return new String[][]{
                {"C", "⌫", "AND", "OR"},
                {"XOR", "NOT", "<<", ">>"},
                {"%", "÷", "×", "-"},
                {"7", "8", "9", "+"},
                {"4", "5", "6", "="},
                {"±", "0", ".", "="}
        };
    }

    // Bouton stylé (arrondi + hover + opérateurs colorés)
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = numberColor;

                if (isOperator(text)) {
                    bgColor = operatorColor;
                }
                if (text.equals("=")) {
                    bgColor = accentColor;
                }
                if (text.equals("C") || text.equals("⌫")) {
                    bgColor = new Color(255, 69, 58);
                }

                if (getModel().isRollover()) {
                    bgColor = bgColor.brighter();
                }
                if (getModel().isPressed()) {
                    bgColor = bgColor.darker();
                }

                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SF Pro Display", Font.BOLD, 22));
        button.setForeground(textPrimary);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 70));
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> handleButtonClick(text));

        return button;
    }

    private boolean isOperator(String text) {
        return Arrays.asList("÷", "×", "-", "+", "%", "xʸ",
                "AND", "OR", "XOR", "<<", ">>").contains(text);
    }

    // Gestion des clics sur boutons
    private void handleButtonClick(String text) {
        try {
            switch (text) {
                case "C" -> clear();
                case "⌫" -> backspace();
                case "=" -> calculate();
                case "±" -> toggleSign();
                case "." -> handleNumber(".");
                case "√" -> squareRoot();
                case "x²" -> square();
                case "xʸ" -> {
                    operator = "^";
                    firstOperand = Double.parseDouble(displayField.getText());
                    startNewNumber = true;
                    updateHistory(firstOperand + " ^ ");
                }
                // Scientifique
                case "sin" -> applyUnary("sin", v -> Math.sin(Math.toRadians(v)));
                case "cos" -> applyUnary("cos", v -> Math.cos(Math.toRadians(v)));
                case "tan" -> applyUnary("tan", v -> Math.tan(Math.toRadians(v)));
                case "log" -> {
                    double v = Double.parseDouble(displayField.getText());
                    if (v <= 0) {
                        displayError("Erreur: x ≤ 0");
                    } else {
                        applyUnary("log", Math::log10);
                    }
                }
                case "ln" -> {
                    double v = Double.parseDouble(displayField.getText());
                    if (v <= 0) {
                        displayError("Erreur: x ≤ 0");
                    } else {
                        applyUnary("ln", Math::log);
                    }
                }
                // Programmeur
                case "AND", "OR", "XOR", "<<", ">>", "%", "÷", "×", "-", "+" -> handleOperator(text);
                case "NOT" -> bitwiseNot();
                default -> handleNumber(text);
            }
        } catch (Exception ex) {
            displayError("Erreur");
        }
    }

    // Interface pour les opérations unaires
    @FunctionalInterface
    private interface UnaryOperation {
        double apply(double value);
    }

    private void applyUnary(String name, UnaryOperation op) {
        double value = Double.parseDouble(displayField.getText());
        double result = op.apply(value);
        String calc = name + "(" + value + ") = " + formatResult(result);
        history.add(calc);
        displayField.setText(formatResult(result));
        updateHistory(calc);
        startNewNumber = true;
    }

    // Gestion des nombres
    private void handleNumber(String num) {
        if (startNewNumber) {
            if (num.equals(".")) {
                displayField.setText("0.");
            } else {
                displayField.setText(num);
            }
            startNewNumber = false;
        } else {
            String current = displayField.getText();
            if (num.equals(".") && current.contains(".")) return;
            displayField.setText(current + num);
        }
    }

    private void handleOperator(String op) {
        if (!operator.isEmpty() && !startNewNumber) {
            calculate();
        }
        firstOperand = Double.parseDouble(displayField.getText());
        operator = op;
        startNewNumber = true;
        updateHistory(firstOperand + " " + op + " ");
    }

    private void calculate() {
        if (operator.isEmpty()) return;

        double secondOperand = Double.parseDouble(displayField.getText());
        double result = 0;

        switch (operator) {
            case "+" -> result = firstOperand + secondOperand;
            case "-" -> result = firstOperand - secondOperand;
            case "×" -> result = firstOperand * secondOperand;
            case "÷" -> {
                if (secondOperand == 0) {
                    displayError("Erreur: Division par 0");
                    operator = "";
                    return;
                }
                result = firstOperand / secondOperand;
            }
            case "%" -> result = firstOperand % secondOperand;
            case "^" -> result = Math.pow(firstOperand, secondOperand);

            // Mode programmeur (opérations bit à bit sur des entiers)
            case "AND" -> {
                int a = (int) Math.round(firstOperand);
                int b = (int) Math.round(secondOperand);
                result = a & b;
            }
            case "OR" -> {
                int a = (int) Math.round(firstOperand);
                int b = (int) Math.round(secondOperand);
                result = a | b;
            }
            case "XOR" -> {
                int a = (int) Math.round(firstOperand);
                int b = (int) Math.round(secondOperand);
                result = a ^ b;
            }
            case "<<" -> {
                int a = (int) Math.round(firstOperand);
                int b = (int) Math.round(secondOperand);
                result = a << b;
            }
            case ">>" -> {
                int a = (int) Math.round(firstOperand);
                int b = (int) Math.round(secondOperand);
                result = a >> b;
            }
        }

        String calculation = firstOperand + " " + operator + " " + secondOperand + " = " + formatResult(result);
        history.add(calculation);
        displayField.setText(formatResult(result));
        updateHistory(calculation);
        operator = "";
        startNewNumber = true;
    }

    private void clear() {
        displayField.setText("0");
        operator = "";
        firstOperand = 0;
        startNewNumber = true;
        historyLabel.setText(" ");
    }

    private void backspace() {
        String current = displayField.getText();
        if (current.length() > 1) {
            displayField.setText(current.substring(0, current.length() - 1));
        } else {
            displayField.setText("0");
            startNewNumber = true;
        }
    }

    private void toggleSign() {
        double value = Double.parseDouble(displayField.getText());
        displayField.setText(formatResult(-value));
    }

    private void squareRoot() {
        double value = Double.parseDouble(displayField.getText());
        if (value < 0) {
            displayError("Erreur: Nombre négatif");
            return;
        }
        double result = Math.sqrt(value);
        String calculation = "√" + value + " = " + formatResult(result);
        history.add(calculation);
        displayField.setText(formatResult(result));
        updateHistory(calculation);
        startNewNumber = true;
    }

    private void square() {
        double value = Double.parseDouble(displayField.getText());
        double result = value * value;
        String calculation = value + "² = " + formatResult(result);
        history.add(calculation);
        displayField.setText(formatResult(result));
        updateHistory(calculation);
        startNewNumber = true;
    }

    private void bitwiseNot() {
        int value = (int) Math.round(Double.parseDouble(displayField.getText()));
        int result = ~value;
        String calculation = "~" + value + " = " + result;
        history.add(calculation);
        displayField.setText(String.valueOf(result));
        updateHistory(calculation);
        startNewNumber = true;
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            DecimalFormat df = new DecimalFormat("#.##########");
            return df.format(result);
        }
    }

    private void updateHistory(String text) {
        historyLabel.setText(text);
    }

    private void displayError(String message) {
        displayField.setText(message);
        startNewNumber = true;
    }

    // Bordure arrondie
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            Shape round = new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius);
            g2d.draw(round);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = radius / 2;
            return insets;
        }
    }

    public static void main(String[] args) {
        // Look & Feel natif pour plus de cohérence
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(CalculatorApp::new);
    }
}