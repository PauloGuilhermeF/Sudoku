// ============= SudokuGridPanel.java =============
package br.com.dio.view;

import br.com.dio.model.SudokuBoard;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Classe responsável pela exibição do grid de Sudoku
 */
public class SudokuGridPanel extends JPanel {
    private static final int CELL_SIZE = 60;
    private JButton[][] cells;
    private SudokuBoard board;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private ActionListener cellClickListener;

    public SudokuGridPanel(SudokuBoard board, ActionListener cellClickListener) {
        this.board = board;
        this.cellClickListener = cellClickListener;
        initializeGrid();
    }

    private void initializeGrid() {
        setLayout(new GridLayout(3, 3, 4, 4));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // Mudança: fundo cinza claro ao invés de preto
        setBackground(new Color(240, 240, 240));

        cells = new JButton[board.getGridSize()][board.getGridSize()];

        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                JPanel subGrid = createSubGrid(blockRow, blockCol);
                add(subGrid);
            }
        }
    }

    private JPanel createSubGrid(int blockRow, int blockCol) {
        JPanel subGrid = new JPanel(new GridLayout(3, 3, 1, 1));
        // Borda mais visível
        subGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        subGrid.setBackground(new Color(240, 240, 240));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int actualRow = blockRow * 3 + row;
                int actualCol = blockCol * 3 + col;

                JButton cell = createCell(actualRow, actualCol);
                cells[actualRow][actualCol] = cell;
                subGrid.add(cell);
            }
        }

        return subGrid;
    }

    private JButton createCell(int row, int col) {
        JButton cell = new JButton();
        cell.setFont(new Font("Arial", Font.BOLD, 20));
        cell.setFocusPainted(false);
        cell.setMargin(new Insets(0, 0, 0, 0));
        cell.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));

        // Garantir que a célula seja opaca para mostrar a cor de fundo
        cell.setOpaque(true);

        cell.addActionListener(e -> {
            selectCell(row, col);
            if (cellClickListener != null) {
                cellClickListener.actionPerformed(e);
            }
        });

        return cell;
    }

    public void selectCell(int row, int col) {
        // Deseleciona célula anterior
        if (selectedRow >= 0 && selectedCol >= 0) {
            updateCellColor(selectedRow, selectedCol, false);
        }

        selectedRow = row;
        selectedCol = col;
        updateCellColor(row, col, true);
    }

    public void updateCellColor(int row, int col, boolean selected) {
        JButton cell = cells[row][col];

        if (board.isOriginal(row, col)) {
            // Células originais: fundo azul claro, texto preto
            cell.setBackground(selected ? new Color(180, 200, 255) : new Color(220, 230, 255));
            cell.setForeground(Color.BLACK);
        } else {
            // Células editáveis: fundo branco/amarelo claro, texto azul escuro
            cell.setBackground(selected ? new Color(255, 255, 180) : Color.WHITE);
            cell.setForeground(new Color(0, 0, 150)); // Azul escuro para melhor contraste
        }

        // Força a atualização visual
        cell.repaint();
    }

    public void updateDisplay() {
        for (int i = 0; i < board.getGridSize(); i++) {
            for (int j = 0; j < board.getGridSize(); j++) {
                updateCellDisplay(i, j);
            }
        }
    }

    public void updateCellDisplay(int row, int col) {
        int value = board.getPlayBoard(row, col);
        JButton cell = cells[row][col];

        cell.setText(value == 0 ? "" : String.valueOf(value));

        if (board.isOriginal(row, col)) {
            // Células originais (não editáveis): fundo azul claro, texto preto
            cell.setBackground(new Color(220, 230, 255));
            cell.setForeground(Color.BLACK);
            cell.setFont(new Font("Arial", Font.BOLD, 20));
        } else {
            // Células editáveis pelo jogador: fundo branco, texto azul escuro
            cell.setBackground(Color.WHITE);
            cell.setForeground(new Color(0, 0, 150));
            cell.setFont(new Font("Arial", Font.PLAIN, 20));
        }

        // Garantir que as cores sejam aplicadas
        cell.setOpaque(true);
        cell.repaint();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void clearSelection() {
        if (selectedRow >= 0 && selectedCol >= 0) {
            updateCellColor(selectedRow, selectedCol, false);
        }
        selectedRow = -1;
        selectedCol = -1;
    }
}