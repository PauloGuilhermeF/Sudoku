package br.com.dio;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SudokuGUI extends JFrame {
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int CELL_SIZE = 60;
    private static final int WIDTH = GRID_SIZE * CELL_SIZE + 80;
    private static final int HEIGHT = GRID_SIZE * CELL_SIZE + 150;
    
    private JButton[][] cells;
    private int[][] solution;
    private int[][] playBoard;
    private boolean[][] isOriginal;
    private JLabel statusLabel;
    private JPanel gridPanel;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int difficulty;
    private long startTime;
    private Timer timer;
    private JLabel timerLabel;
    
    public SudokuGUI() {
        setTitle("Jogo de Sudoku");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        showDifficultyDialog();
    }
    
    private void initComponents() {
        solution = new int[GRID_SIZE][GRID_SIZE];
        playBoard = new int[GRID_SIZE][GRID_SIZE];
        isOriginal = new boolean[GRID_SIZE][GRID_SIZE];
        cells = new JButton[GRID_SIZE][GRID_SIZE];
        
        // Painel superior
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
        
        // Grid de Sudoku
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int r = row;
                final int c = col;
                
                cells[row][col] = new JButton();
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setFocusPainted(false);
                cells[row][col].setMargin(new Insets(0, 0, 0, 0));
                
                // Borda mais grossa para separar os subgrids
                Border border = BorderFactory.createMatteBorder(
                    (row % SUBGRID_SIZE == 0) ? 2 : 1, 
                    (col % SUBGRID_SIZE == 0) ? 2 : 1, 
                    (row == GRID_SIZE - 1) ? 2 : 1, 
                    (col == GRID_SIZE - 1) ? 2 : 1, 
                    Color.BLACK
                );
                cells[row][col].setBorder(border);
                
                cells[row][col].addActionListener(e -> selectCell(r, c));
                gridPanel.add(cells[row][col]);
            }
        }
        
        add(gridPanel, BorderLayout.CENTER);
        
        // Painel inferior com botões de controle
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        JButton newGameButton = new JButton("Novo Jogo");
        newGameButton.addActionListener(e -> showDifficultyDialog());
        
        JButton solutionButton = new JButton("Ver Solução");
        solutionButton.addActionListener(e -> showSolution());
        
        JButton checkButton = new JButton("Verificar");
        checkButton.addActionListener(e -> checkSolution());
        
        buttonPanel.add(newGameButton);
        buttonPanel.add(solutionButton);
        buttonPanel.add(checkButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // KeyListener para números
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (selectedRow >= 0 && selectedCol >= 0) {
                    char key = e.getKeyChar();
                    if (key >= '1' && key <= '9') {
                        int num = key - '0';
                        if (!isOriginal[selectedRow][selectedCol]) {
                            playBoard[selectedRow][selectedCol] = num;
                            updateCellDisplay(selectedRow, selectedCol);
                        } else {
                            statusLabel.setText("Esta célula não pode ser alterada!");
                        }
                    } else if (key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_DELETE || key == '0') {
                        if (!isOriginal[selectedRow][selectedCol]) {
                            playBoard[selectedRow][selectedCol] = 0;
                            updateCellDisplay(selectedRow, selectedCol);
                        }
                    }
                }
            }
        });
        
        setFocusable(true);
    }
    
    private void showDifficultyDialog() {
        String[] options = {"Fácil", "Médio", "Difícil"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Escolha a dificuldade:",
            "Nova Partida",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != JOptionPane.CLOSED_OPTION) {
            difficulty = choice + 1;
            startNewGame();
        } else if (solution[0][0] == 0) {
            // Se é a primeira execução e o usuário fechou o diálogo
            difficulty = 1; // Default para fácil
            startNewGame();
        }
    }
    
    private void startNewGame() {
        // Reseta o tabuleiro
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                solution[i][j] = 0;
                playBoard[i][j] = 0;
                isOriginal[i][j] = false;
            }
        }
        
        generateSudoku();
        displayBoard();
        
        // Inicia o cronômetro
        startTime = System.currentTimeMillis();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, e -> updateTimer());
        timer.start();
    }
    
    private void updateTimer() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;
        timerLabel.setText(String.format("Tempo: %02d:%02d", minutes, seconds));
    }
    
    private void selectCell(int row, int col) {
        // Deseleciona a célula anterior
        if (selectedRow >= 0 && selectedCol >= 0) {
            updateCellColor(selectedRow, selectedCol, false);
        }
        
        selectedRow = row;
        selectedCol = col;
        updateCellColor(row, col, true);
        
        if (isOriginal[row][col]) {
            statusLabel.setText("Esta célula é fixa e não pode ser alterada");
        } else {
            statusLabel.setText("Digite um número de 1 a 9");
        }
        
        requestFocus(); // Para garantir que os eventos de teclado sejam capturados
    }
    
    private void updateCellColor(int row, int col, boolean selected) {
        if (isOriginal[row][col]) {
            cells[row][col].setBackground(selected ? new Color(200, 220, 250) : new Color(230, 230, 250));
            cells[row][col].setForeground(Color.BLACK);
        } else {
            cells[row][col].setBackground(selected ? new Color(255, 255, 200) : Color.WHITE);
            cells[row][col].setForeground(Color.BLUE);
        }
    }
    
    private void updateCellDisplay(int row, int col) {
        int value = playBoard[row][col];
        cells[row][col].setText(value == 0 ? "" : String.valueOf(value));
        
        // Verifica se a célula está correta
        if (value != 0) {
            if (value == solution[row][col]) {
                cells[row][col].setForeground(isOriginal[row][col] ? Color.BLACK : new Color(0, 128, 0));
            } else {
                cells[row][col].setForeground(Color.RED);
            }
        }
        
        // Verifica se o jogo foi concluído
        checkWin();
    }
    
    private void checkWin() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (playBoard[i][j] != solution[i][j]) {
                    return;
                }
            }
        }
        
        timer.stop();
        JOptionPane.showMessageDialog(
            this,
            "Parabéns! Você completou o Sudoku!\nTempo: " + timerLabel.getText().substring(7),
            "Vitória!",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showSolution() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja ver a solução? Isso encerrará o jogo atual.",
            "Ver Solução",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            timer.stop();
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    playBoard[i][j] = solution[i][j];
                    cells[i][j].setText(String.valueOf(solution[i][j]));
                    cells[i][j].setForeground(isOriginal[i][j] ? Color.BLACK : new Color(100, 100, 100));
                }
            }
            statusLabel.setText("Solução exibida. Inicie um novo jogo para jogar novamente.");
        }
    }
    
    private void checkSolution() {
        boolean hasErrors = false;
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (playBoard[i][j] != 0 && playBoard[i][j] != solution[i][j]) {
                    hasErrors = true;
                    break;
                }
            }
            if (hasErrors) break;
        }
        
        if (hasErrors) {
            statusLabel.setText("Há erros no tabuleiro!");
        } else {
            statusLabel.setText("Até agora, tudo correto! Continue jogando.");
        }
    }
    
    private void generateSudoku() {
        // Preenchemos os blocos diagonais principais primeiro
        fillDiagonalBlocks();
        
        // Resolvemos o resto do tabuleiro
        solveSudoku(0, 0);
        
        // Copiamos a solução para o tabuleiro de jogo
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                playBoard[i][j] = solution[i][j];
            }
        }
        
        // Removemos números com base na dificuldade
        removeNumbers();
    }
    
    private void fillDiagonalBlocks() {
        for (int block = 0; block < GRID_SIZE; block += SUBGRID_SIZE) {
            fillBlock(block, block);
        }
    }
    
    private void fillBlock(int row, int col) {
        Random random = new Random();
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        
        // Embaralha os números
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
        
        int index = 0;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                solution[row + i][col + j] = nums[index++];
            }
        }
    }
    
    private boolean solveSudoku(int row, int col) {
        // Se chegamos ao final do tabuleiro, terminamos
        if (row == GRID_SIZE - 1 && col == GRID_SIZE) {
            return true;
        }
        
        // Se chegarmos ao final de uma linha, vá para a próxima
        if (col == GRID_SIZE) {
            row++;
            col = 0;
        }
        
        // Se a célula já está preenchida, passe para a próxima
        if (solution[row][col] != 0) {
            return solveSudoku(row, col + 1);
        }
        
        // Tente colocar cada número de 1 a 9
        for (int num = 1; num <= GRID_SIZE; num++) {
            // Verifique se é válido colocar "num" nesta posição
            if (isValidPlacement(row, col, num)) {
                solution[row][col] = num;
                
                // Recursão: continue para a próxima célula
                if (solveSudoku(row, col + 1)) {
                    return true;
                }
                
                // Se não funcionar, desfaça e tente o próximo número
                solution[row][col] = 0;
            }
        }
        return false; // Se nenhum número funcionar, há um problema anteriormente
    }
    
    private boolean isValidPlacement(int row, int col, int num) {
        // Verifica linha
        for (int i = 0; i < GRID_SIZE; i++) {
            if (solution[row][i] == num) {
                return false;
            }
        }
        
        // Verifica coluna
        for (int i = 0; i < GRID_SIZE; i++) {
            if (solution[i][col] == num) {
                return false;
            }
        }
        
        // Verifica bloco 3x3
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (solution[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void removeNumbers() {
        Random random = new Random();
        int numbersToRemove;
        
        // Define quantos números serão removidos baseado na dificuldade
        switch (difficulty) {
            case 1: // Fácil
                numbersToRemove = 40; // ~45% removido
                break;
            case 2: // Médio
                numbersToRemove = 50; // ~55% removido
                break;
            case 3: // Difícil
                numbersToRemove = 60; // ~66% removido
                break;
            default:
                numbersToRemove = 40;
        }
        
        // Remove números aleatoriamente
        while (numbersToRemove > 0) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            
            if (playBoard[row][col] != 0) {
                playBoard[row][col] = 0;
                numbersToRemove--;
            }
        }
        
        // Marca as células originais (que não podem ser alteradas)
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                isOriginal[i][j] = (playBoard[i][j] != 0);
            }
        }
    }
    
    private void displayBoard() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int value = playBoard[i][j];
                cells[i][j].setText(value == 0 ? "" : String.valueOf(value));
                
                if (value != 0) {
                    isOriginal[i][j] = true;
                    cells[i][j].setBackground(new Color(230, 230, 250)); // Cor para números fixos
                    cells[i][j].setForeground(Color.BLACK);
                } else {
                    isOriginal[i][j] = false;
                    cells[i][j].setBackground(Color.WHITE);
                }
            }
        }
        
        statusLabel.setText("Boa sorte! Selecione uma célula vazia e digite um número.");
    }

    public static void main(String[] args) {
        try {
            // Define um visual mais nativo para a interface
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