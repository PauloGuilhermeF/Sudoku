// ============= SudokuBoard.java =============
package br.com.dio.model;

import java.util.Random;

/**
 * Classe responsável pela lógica do tabuleiro de Sudoku
 */
public class SudokuBoard {
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    private int[][] solution;
    private int[][] playBoard;
    private boolean[][] isOriginal;

    public SudokuBoard() {
        solution = new int[GRID_SIZE][GRID_SIZE];
        playBoard = new int[GRID_SIZE][GRID_SIZE];
        isOriginal = new boolean[GRID_SIZE][GRID_SIZE];
    }

    public int getGridSize() {
        return GRID_SIZE;
    }

    public int getSubgridSize() {
        return SUBGRID_SIZE;
    }

    public int getSolution(int row, int col) {
        return solution[row][col];
    }

    public int getPlayBoard(int row, int col) {
        return playBoard[row][col];
    }

    public boolean isOriginal(int row, int col) {
        return isOriginal[row][col];
    }

    public void setPlayBoard(int row, int col, int value) {
        if (!isOriginal[row][col]) {
            playBoard[row][col] = value;
        }
    }

    public void generateNewBoard(int difficulty) {
        // Limpa o tabuleiro
        clearBoard();

        // Gera nova solução
        fillDiagonalBlocks();
        solveSudoku(0, 0);

        // Copia solução para o tabuleiro de jogo
        copyToPlayBoard();

        // Remove números baseado na dificuldade
        removeNumbers(difficulty);

        // Marca células originais
        markOriginalCells();
    }

    public void restartBoard() {
        // Limpa apenas as células não originais
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!isOriginal[i][j]) {
                    playBoard[i][j] = 0;
                }
            }
        }
    }

    public boolean isComplete() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (playBoard[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int countErrors() {
        int errorCount = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (playBoard[i][j] != 0 && playBoard[i][j] != solution[i][j]) {
                    errorCount++;
                }
            }
        }
        return errorCount;
    }

    public void showSolution() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                playBoard[i][j] = solution[i][j];
            }
        }
    }

    // Métodos privados de geração
    private void clearBoard() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                solution[i][j] = 0;
                playBoard[i][j] = 0;
                isOriginal[i][j] = false;
            }
        }
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
        if (row == GRID_SIZE - 1 && col == GRID_SIZE) {
            return true;
        }

        if (col == GRID_SIZE) {
            row++;
            col = 0;
        }

        if (solution[row][col] != 0) {
            return solveSudoku(row, col + 1);
        }

        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isValidPlacement(row, col, num)) {
                solution[row][col] = num;

                if (solveSudoku(row, col + 1)) {
                    return true;
                }

                solution[row][col] = 0;
            }
        }
        return false;
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

    private void copyToPlayBoard() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                playBoard[i][j] = solution[i][j];
            }
        }
    }

    private void removeNumbers(int difficulty) {
        Random random = new Random();
        int numbersToRemove;

        switch (difficulty) {
            case 1: // Fácil
                numbersToRemove = 40;
                break;
            case 2: // Médio
                numbersToRemove = 50;
                break;
            case 3: // Difícil
                numbersToRemove = 60;
                break;
            default:
                numbersToRemove = 40;
        }

        while (numbersToRemove > 0) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (playBoard[row][col] != 0) {
                playBoard[row][col] = 0;
                numbersToRemove--;
            }
        }
    }

    private void markOriginalCells() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                isOriginal[i][j] = (playBoard[i][j] != 0);
            }
        }
    }
}