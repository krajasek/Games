/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.game.minesweeper;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author karthikr
 */
public class MineSweeper {
    private static final int GRID_CELL_ROWS = 15;
    private static final int GRID_CELL_COLS = 15;
    private static final float NUM_OF_MINES_PERCENT = 0.2f;
    private static int MAX_REVEAL_SQUARES = 7;
    private static int POINTS_PER_CORRECT_PATH = 50;
    private int gameGrid[][];
    private int mineGrid[][];
    private int visitedGrid[][];
    private int points;
    private int revealSquareCount;
    private int numGridRows;
    private int numGridCols;
    
    public MineSweeper() {
        this(GRID_CELL_ROWS, GRID_CELL_COLS);
    }
    
    public MineSweeper(int gridRows, int gridCols) {
        this.setupGame(gridRows, gridCols);
    }
    public void setupGame(int gridRows, int gridCols) {
        this.numGridRows = gridRows;
        this.numGridCols = gridCols;
        this.gameGrid = new int[gridRows][gridCols];
        this.mineGrid = new int[gridRows][gridCols];
        this.points = 0;
        this.visitedGrid = new int[gridRows][gridCols];
        this.revealSquareCount = 0;
        this.setupMines();
    }
    public void setupMines() {
        /**
         * Randomly set some squares to hold mines subject to max number of 
         * mines
         */ 
        Random rowRandomizer = new Random();
        Random colRandomizer = new Random();
        int randrow, randcol;
        int numOfMines 
            = (int) (NUM_OF_MINES_PERCENT*this.numGridRows*this.numGridCols);
        
        for(int k = 0; k < numOfMines;) {
            randrow = rowRandomizer.nextInt(this.numGridRows);
            randcol = colRandomizer.nextInt(this.numGridCols);
            if (this.mineGrid[randrow][randcol] == 1) {
                continue;
            }
            this.mineGrid[randrow][randcol] = 1;
            k++;
        }
    }
    public void printGameGrid() {
        System.out.println();
        _printGrid(this.gameGrid, false);
    }
    
    public void printMineGrid() {
        System.out.println();
        _printGrid(this.mineGrid, false);
    }
    
    private void _printGrid(int grid[][], boolean showMines) {
        System.out.printf("    |");           
        print(" %2d  |", this.numGridCols, true);

        for(int i = 0; i < this.numGridRows; i++) {
           System.out.printf("    |");
           print("-----|", this.numGridCols, false);
           System.out.printf("%2d->|", i + 1);
           for(int j = 0; j < this.numGridCols; j++) {
               if (showMines && this.mineGrid[i][j] == 1) {
                   System.out.printf(" %2s  |", "M");
                   continue;
               }
               if (grid[i][j] == 0) {
                   System.out.printf(" %2s  |", " ");
               } else if (grid[i][j] == -1) {
                   System.out.printf(" %2s  |", "X");
                } else {
                   System.out.printf(" %2d  |", grid[i][j]);
               }
              
           }
           System.out.println();
       }
       System.out.printf("    |");
       print("-----|", this.numGridCols, false);
       
    }
    
    public void playGame() {
        Scanner inScan = new Scanner(System.in);
        int row = 0;
        int col = 0;
        boolean validInput = false;
        this.printGameGrid();
        gameOn:
        while (true) {
            validInput = false;
            while (!validInput) {
                try {
                    System.out.printf("Enter a row # (-1 = Exit): ");
                    row = inScan.nextInt();
                    if (row == -1) {
                        break gameOn;
                    } else if (row <= 0 || row > this.numGridRows) {
                        throw new InputMismatchException();
                    }
                    System.out.printf("Enter a col # (-1 = Exit): ");
                    col = inScan.nextInt();
                    if (col == -1) {
                        break gameOn;
                    } else if (col <= 0 || col > this.numGridCols) {
                        throw new InputMismatchException();
                    }
                    row--;
                    col--;
                    if (this.isValidElement(this.gameGrid, row, col) && 
                        this.gameGrid[row][col] == 0
                    ) {
                        validInput = true;
                    }
                } catch(InputMismatchException mex) {
                    /**
                     * If the input was a string instead of row or col #, then
                     * try to read it off as a string and ask user input again
                     */
                    if (inScan != null && inScan.hasNextLine()) {
                        inScan.nextLine();
                    }
                    validInput = false;
                } catch(Exception ex) {
                    break gameOn;
                }
            }

            if (this.mineGrid[row][col] == 1) {
                System.out.println("Sorry, you tripped over a mine :-(");
                this.showMinesInGameGrid();
                break;
            } else {
                this.points += POINTS_PER_CORRECT_PATH;
                this.gameGrid[row][col] = -1;
                System.out.printf("Your score: %d \r\n", this.points);
            }
            this.resetVisitedGrid();
            this.revealSquareCount = 0;
            // Recursively reveal adjacent mines if the mine count is 0
            revealAdjacentMineCount(row, col);
            this.printGameGrid();
            
        }
        System.out.printf("Your final score: %d \r\n", this.points);
    }
    
    private void showMinesInGameGrid() {
        this._printGrid(this.gameGrid, true);
    }
    private void revealAdjacentMineCount(int row, int col) {
        int counter = 0;
        if ( row < 0 || row > this.numGridRows - 1) {
            return;
        }
        if (col < 0 || col > this.numGridCols - 1) {
            return;
        }
        if (this.gameGrid[row][col] > 0) {
            return;
        }
        if (this.visitedGrid[row][col] == 1) {
            return;
        }
        if (this.revealSquareCount > MAX_REVEAL_SQUARES) {
            return;
        }
        counter = countAdjacentMines(row, col);
        if (counter > 0) {
            this.gameGrid[row][col] = counter;
        }
        this.visitedGrid[row][col] = 1;
        this.revealSquareCount++;
        revealAdjacentMineCount(row, col - 1);
        revealAdjacentMineCount(row, col + 1);
        revealAdjacentMineCount(row - 1, col);
        revealAdjacentMineCount(row + 1, col);            
        
    }
    private int countAdjacentMines(int row, int col) {
        int counter = 0;
        // Check left square
        if (isValidElement(this.mineGrid, row, col - 1)) {
            if (this.mineGrid[row][col - 1] == 1) {
                counter++;
            }
        }
        // Check right square
        if (isValidElement(this.mineGrid, row, col + 1)) {
            if (this.mineGrid[row][col + 1] == 1) {
                counter++;
            }
        }
        
        //Check top square
        if (isValidElement(this.mineGrid, row - 1, col)) {
            if (this.mineGrid[row - 1][col] == 1) {
                counter++;
            }
        }
        
        //Check bottom square
        if (isValidElement(this.mineGrid, row + 1, col)) {
            if (this.mineGrid[row + 1][col] == 1) {
                counter++;
            }
        }
        
        return counter;
        
    }
    
    private void resetVisitedGrid() {
        this._resetGrid(this.visitedGrid);
    }
    private boolean isValidElement(int grid[][], int row, int col) {
        boolean result = true;
        int v;
        try {
            v = grid[row][col];
        } catch(Exception ex) {
            return false;
        }
        return result;
    }
    
    public void clearGame() {
        this.points = 0;
        this.resetGameGrid();
        this.resetVisitedGrid();
        this.resetMineGrid();
        this.revealSquareCount = 0;
    }
    
    public void resetGameGrid() {
        this._resetGrid(this.gameGrid);
    }
    public void resetMineGrid() {
        this._resetGrid(this.mineGrid);
    }
    private void _resetGrid(int grid[][]) {
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0;j < grid[i].length; j++) {
                grid[i][j] = 0;
            }
        }
    }
    
    private static void print(String str, int occurs, boolean printNum) {
        String printStr = null;
        for(int i = 0; i < occurs; i++) {
            if (printNum) {
                printStr = String.format(str, i + 1);
                
            } else {
               printStr = str; 
            }
            System.out.printf(printStr);
        }
        System.out.println();
    }
    public static void main(String args[]) {
        MineSweeper mine = new MineSweeper(10, 10);
        mine.playGame();
    }
}
