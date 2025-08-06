package parser;

// GUI Libraries for Swing interface components
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// For displaying AST structure as a JTree
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

// File input/output libraries
import java.io.*;
import java.nio.file.Files;

// Collections for handling lists of AST nodes, tokens, etc.
import java.util.List;
import java.util.ArrayList;

/*
   Main class for the Java Mini Compiler GUI.
   Handles file loading, token display, AST generation,
   semantic analysis, optimization, code generation, and dark mode.
 */
public class Main extends JFrame {
    // UI Components
    private JTextArea inputArea, outputArea, lineNumberArea;
    private String inputCode = "";
    private boolean darkMode = false;

    // AST Viewer
    private JTree astTree;
    private JPanel astPanel;
    private JScrollPane astScrollPane;


    // Tabbed pane to switch between Output and AST view
    private JTabbedPane tabbedPane;


    // Constructor: sets window properties and initializes the GUI
    public Main() {
        setTitle("Java Mini Compiler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);   // Center window
        initUI();
    }


    // Initializes all GUI components and layout
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel to input source code
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("Input Code"));

        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputArea.setEditable(true);

        // Line number updates on input changes
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void changedUpdate(DocumentEvent e) { updateLineNumbers(); }
        });

        // Line number area beside code input
        lineNumberArea = new JTextArea("1");
        lineNumberArea.setEditable(false);
        lineNumberArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lineNumberArea.setBackground(Color.LIGHT_GRAY);

        // Scroll pane with input area and line numbers
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setRowHeaderView(lineNumberArea);
        inputScroll.setPreferredSize(new Dimension(800, 200));  // Set fixed height for input area
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        // Output area for tokens, intermediate code, errors
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Panel to hold AST Tree
        astPanel = new JPanel(new BorderLayout());
        astPanel.setBorder(new TitledBorder("AST Viewer"));
        astTree = new JTree();
        astTree.setFont(new Font("Monospaced", Font.PLAIN, 12));
        astScrollPane = new JScrollPane(astTree);
        astPanel.add(astScrollPane, BorderLayout.CENTER);

        // Tabs: Output and AST viewer
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Output", new JScrollPane(outputArea));
        tabbedPane.addTab("AST", astPanel);
        tabbedPane.setPreferredSize(new Dimension(800, 400));  // Let it grow tall enough

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel with control buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        String[] labels = {
                "Load File", "Show Tokens", "Show AST", "Semantic Analysis",
                "Generate Code", "Full Pipeline", "Save Output", "Toggle Dark Mode", "Exit"
        };
        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.addActionListener(new ButtonClickListener());
            buttonPanel.add(btn);
        }

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }


    // Handles button actions
    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            try {
                switch (cmd) {
                    case "Load File":   outputArea.setText(""); loadInputFile();  break;
                    case "Show Tokens": outputArea.setText("");  showTokens();  break;
                    case "Show AST":
                        outputArea.setText("");
                        showAST();
                        tabbedPane.setSelectedComponent(astPanel); // Switch tab to AST
                        break;
                    case "Semantic Analysis":   outputArea.setText(""); semanticAnalysis(); break;
                    case "Generate Code":outputArea.setText("");  generateCode();  break;
                    case "Full Pipeline": outputArea.setText(""); fullPipeline();  break;
                    case "Save Output": saveOutput(); break;
                    case "Toggle Dark Mode": toggleDarkMode(); break;
                    case "Exit": System.exit(0); break;
                }
            } catch (Exception ex) {
                outputArea.append("Error: " + ex.getMessage() + "\n");
            }
        }
    }


    // Opens and loads a source file into the input area
     private void loadInputFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                inputCode = Files.readString(fc.getSelectedFile().toPath());
                inputArea.setText(inputCode);
                updateLineNumbers();
                outputArea.append("Loaded: " + fc.getSelectedFile().getName() + "\n");
            } catch (Exception e) {
                outputArea.append("Failed to load file: " + e.getMessage() + "\n");
            }
        }
    }


    // Updates line numbers alongside the input area
    private void updateLineNumbers() {
        String[] lines = inputArea.getText().split("\n");
        StringBuilder lineNum = new StringBuilder();
        for (int i = 1; i <= lines.length; i++) {
            lineNum.append(i).append("\n");
        }
        lineNumberArea.setText(lineNum.toString());
    }


    private void saveOutput() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            String text = outputArea.getText();
            System.out.println("Saving text:\n" + text); // Debug
            if (!text.isEmpty()) {
                try {
                    Files.writeString(fc.getSelectedFile().toPath(), text);
                    outputArea.append("Saved to: " + fc.getSelectedFile().getName() + "\n");
                } catch (IOException ex) {
                    outputArea.append("Failed to save: " + ex.getMessage() + "\n");
                }
            } else {
                outputArea.append("Nothing to save — output is empty.\n");
            }
        }
    }


    // Switches UI theme between light and dark mode
    private void toggleDarkMode() {
        Color bg = darkMode ? Color.WHITE : Color.DARK_GRAY;
        Color fg = darkMode ? Color.BLACK : Color.WHITE;
        inputArea.setBackground(bg);
        inputArea.setForeground(fg);
        outputArea.setBackground(bg);
        outputArea.setForeground(fg);
        lineNumberArea.setBackground(darkMode ? Color.LIGHT_GRAY : Color.GRAY);
        lineNumberArea.setForeground(fg);
        darkMode = !darkMode;
    }

    // Tokenizes the input using ManualLexer and displays tokens
    private void showTokens() {
        ManualLexer lexer = new ManualLexer(inputCode);
        Token token;
        outputArea.append(String.format("%-20s %-10s\n", "Token Type", "Value"));
        outputArea.append("-----------------------------------\n");
        do {
            token = lexer.getNextToken();
            outputArea.append(String.format("%-20s %-10s\n", token.type, token.value));
        } while (token.type != TokenType.EOF);
    }


    // Parses the code into an AST and shows it as a JTree
    private void showAST() {
        ManualLexer lexer = new ManualLexer(inputCode);
        ManualParser parser = new ManualParser(lexer);
        List<ASTNode> ast = parser.parseProgram();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Program");
        for (ASTNode node : ast) {
            root.add(node.buildTreeNode(node));
        }

        TreeModel model = new DefaultTreeModel(root);
        astTree.setModel(model);
        tabbedPane.setSelectedComponent(astPanel);
    }


    // Performs semantic analysis on the parsed AST
    private void semanticAnalysis() {
        ManualLexer lexer = new ManualLexer(inputCode);
        ManualParser parser = new ManualParser(lexer);
        List<ASTNode> prog = parser.parseProgram();
        SemanticAnalyzer sem = new SemanticAnalyzer();
        for (ASTNode node : prog){
            sem.analyze(node);
        }
        outputArea.append("Semantic analysis done.\n");
    }


    // Generates intermediate code from optimized AST
    private void generateCode() {
        ManualLexer lexer = new ManualLexer(inputCode);
        ManualParser parser = new ManualParser(lexer);
        List<ASTNode> prog = parser.parseProgram();
        ConstantFolder folder = new ConstantFolder();
        IntermediateCodeGenerator icg = new IntermediateCodeGenerator();

        for (ASTNode node : prog) {
            ASTNode optimized = folder.fold(node);
            for (String line : icg.generate(optimized)) {
                outputArea.append(line + "\n");
            }
        }
    }

    // Runs all stages: semantic analysis, optimization, code generation
    private void fullPipeline() {
        ManualLexer lexer = new ManualLexer(inputCode);
        ManualParser parser = new ManualParser(lexer);
        List<ASTNode> prog = parser.parseProgram();

        outputArea.append("=== Semantic Analysis ===\n");
        SemanticAnalyzer sem = new SemanticAnalyzer();
        for (ASTNode node : prog) sem.analyze(node);

        outputArea.append("=== Optimized AST ===\n");
        ConstantFolder folder = new ConstantFolder();
        List<ASTNode> optimized = new ArrayList<>();
        for (ASTNode node : prog) {
            ASTNode o = folder.fold(node);
            outputArea.append(o.toString() + "\n");
            optimized.add(o);
        }

        outputArea.append("=== Intermediate Code ===\n");
        IntermediateCodeGenerator icg = new IntermediateCodeGenerator();
        for (ASTNode node : optimized) {
            for (String line : icg.generate(node)) {
                outputArea.append(line + "\n");
            }
        }
    }


    //  Main method to launch the compiler GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
