

// ============= SudokuGUI.java =============
package br.com.dio;

import br.com.dio.controller.SudokuController;
import javax.swing.*;
        import java.awt.*;
        import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Classe principal da interface gráfica
 */
public class SudokuGUI extends JFrame {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 720;

    private SudokuController controller;
    private JLabel statusLabel;
    private JLabel timerLabel;

    public SudokuGUI() {
        controller = new SudokuController(this);
        initComponents();
        controller.showDifficultyDialog();
    }

    private void initComponents() {
        setTitle("Jogo de Sudoku");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel superior
        createTopPanel();

        // Grid de Sudoku
        add(controller.createGridPanel(), BorderLayout.CENTER);

        // Painel inferior com botões
        createBottomPanel();

        // Configurar labels no controller
        controller.setStatusLabel(statusLabel);
        controller.setTimerLabel(timerLabel);

        // KeyListener para entrada de números
        setupKeyListener();

        setFocusable(true);
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        // Timer
        timerLabel = new JLabel("Tempo: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(timerLabel, BorderLayout.NORTH);

        // Status
        statusLabel = new JLabel("Selecione uma célula e digite um número (1-9)");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(statusLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createBottomPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton newGameButton = new JButton("Novo Jogo");
        newGameButton.addActionListener(e -> controller.showDifficultyDialog());

        JButton restartButton = new JButton("Reiniciar");
        restartButton.addActionListener(e -> controller.restartCurrentGame());

        JButton solutionButton = new JButton("Ver Solução");
        solutionButton.addActionListener(e -> controller.showSolution());

        JButton checkButton = new JButton("Verificar");
        checkButton.addActionListener(e -> controller.checkSolution());

        buttonPanel.add(newGameButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(solutionButton);
        buttonPanel.add(checkButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.handleKeyInput(e.getKeyChar());
                requestFocus(); // Mantém o foco na janela principal
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            SudokuGUI game = new SudokuGUI();
            game.setVisible(true);
        });
    }
}