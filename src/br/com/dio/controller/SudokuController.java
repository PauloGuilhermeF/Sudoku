// ============= SudokuController.java =============
package br.com.dio.controller;

import br.com.dio.enums.DifficultyLevel;
import br.com.dio.model.SudokuBoard;
import br.com.dio.util.GameTimer;
import br.com.dio.view.SudokuGridPanel;
import javax.swing.*;

/**
 * Classe controladora principal do jogo
 */
public class SudokuController {
    private SudokuBoard board;
    private SudokuGridPanel gridPanel;
    private GameTimer gameTimer;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JFrame parentFrame;

    public SudokuController(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.board = new SudokuBoard();
        this.gameTimer = new GameTimer(e -> updateTimerDisplay());
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    public void setTimerLabel(JLabel timerLabel) {
        this.timerLabel = timerLabel;
    }

    public SudokuGridPanel createGridPanel() {
        gridPanel = new SudokuGridPanel(board, e -> handleCellSelection());
        return gridPanel;
    }

    public void showDifficultyDialog() {
        String[] options = DifficultyLevel.getDescriptions();
        int choice = JOptionPane.showOptionDialog(
                parentFrame,
                "Escolha a dificuldade:",
                "Nova Partida",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != JOptionPane.CLOSED_OPTION) {
            startNewGame(DifficultyLevel.fromIndex(choice));
        } else {
            startNewGame(DifficultyLevel.EASY);
        }
    }

    public void startNewGame(DifficultyLevel difficulty) {
        board.generateNewBoard(difficulty.getLevel());
        gridPanel.updateDisplay();
        gameTimer.start();
        updateStatus("Boa sorte! Selecione uma célula vazia e digite um número.");
    }

    public void restartCurrentGame() {
        int option = JOptionPane.showConfirmDialog(
                parentFrame,
                "Tem certeza que deseja reiniciar o jogo atual? Todo o progresso será perdido.",
                "Reiniciar Jogo",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            board.restartBoard();
            gridPanel.updateDisplay();
            gameTimer.start();
            updateStatus("Jogo reiniciado! Boa sorte!");
        }
    }

    public void showSolution() {
        int option = JOptionPane.showConfirmDialog(
                parentFrame,
                "Tem certeza que deseja ver a solução? Isso encerrará o jogo atual.",
                "Ver Solução",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            gameTimer.stop();
            board.showSolution();
            gridPanel.updateDisplay();
            updateStatus("Solução exibida. Inicie um novo jogo para jogar novamente.");
        }
    }

    public void checkSolution() {
        int errorCount = board.countErrors();

        if (errorCount > 0) {
            updateStatus("Há " + errorCount + " erro(s) no tabuleiro!");
        } else {
            updateStatus("Até agora, tudo correto! Continue jogando.");
        }
    }

    public void handleKeyInput(char key) {
        int selectedRow = gridPanel.getSelectedRow();
        int selectedCol = gridPanel.getSelectedCol();

        if (selectedRow >= 0 && selectedCol >= 0) {
            if (key >= '1' && key <= '9') {
                int num = key - '0';
                if (!board.isOriginal(selectedRow, selectedCol)) {
                    board.setPlayBoard(selectedRow, selectedCol, num);
                    gridPanel.updateCellDisplay(selectedRow, selectedCol);
                    checkWin();
                } else {
                    updateStatus("Esta célula não pode ser alterada!");
                }
            } else if (key == '\b' || key == '\u007F' || key == '0') { // Backspace, Delete, 0
                if (!board.isOriginal(selectedRow, selectedCol)) {
                    board.setPlayBoard(selectedRow, selectedCol, 0);
                    gridPanel.updateCellDisplay(selectedRow, selectedCol);
                }
            }
        }
    }

    private void handleCellSelection() {
        int selectedRow = gridPanel.getSelectedRow();
        int selectedCol = gridPanel.getSelectedCol();

        if (board.isOriginal(selectedRow, selectedCol)) {
            updateStatus("Esta célula é fixa e não pode ser alterada");
        } else {
            updateStatus("Digite um número de 1 a 9");
        }
    }

    private void checkWin() {
        if (board.isComplete()) {
            gameTimer.stop();
            JOptionPane.showMessageDialog(
                    parentFrame,
                    "Parabéns! Você completou o Sudoku!\nTempo: " + gameTimer.getElapsedTime(),
                    "Vitória!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void updateTimerDisplay() {
        if (timerLabel != null) {
            timerLabel.setText(gameTimer.getFormattedTime());
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
