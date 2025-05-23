

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
        setBackground(Color.BLACK);

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
        subGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

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
            cell.setBackground(selected ? new Color(200, 220, 250) : new Color(230, 230, 250));
            cell.setForeground(Color.BLACK);
        } else {
            cell.setBackground(selected ? new Color(255, 255, 200) : Color.WHITE);
            cell.setForeground(Color.BLUE);
        }
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
            cell.setBackground(new Color(230, 230, 250));
            cell.setForeground(Color.BLACK);
        } else {
            cell.setBackground(Color.WHITE);
            cell.setForeground(Color.BLUE);
        }
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
