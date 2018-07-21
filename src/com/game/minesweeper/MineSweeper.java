/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.game.minesweeper;

import java.util.HashSet;
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
    private static final int NUM_OF_MINES = 35;
    private static int MAX_REVEAL_SQUARES = 7;
    private static int POINTS_PER_CORRECT_PATH = 50;
    private int gameGrid[][];
    private int mineGrid[][];
    private int visitedGrid[][];
    private int points;
    private int revealSquareCount;
    public MineSweeper() {
        this.gameGrid = new int[GRID_CELL_ROWS][GRID_CELL_COLS];
        this.mineGrid = new int[GRID_CELL_ROWS][GRID_CELL_COLS];
        this.points = 0;
        this.visitedGrid = new int[GRID_CELL_ROWS][GRID_CELL_COLS];
        this.revealSquareCount = 0;
        
    }
    private void setup() {
        /**
         * Randomly set some squares to hold mines subject to max of 
         * NUM_OF_MINES
         */ 
        Random rowRandomizer = new Random();
        Random colRandomizer = new Random();
        
        int randrow, randcol;
        for(int k = 0; k < NUM_OF_MINES;) {
            randrow = rowRandomizer.nextInt(GRID_CELL_ROWS);
            randcol = colRandomizer.nextInt(GRID_CELL_COLS);
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
        print(" %2d  |", GRID_CELL_COLS, true);

        for(int i = 0; i < GRID_CELL_ROWS; i++) {
           System.out.printf("    |");
           print("-----|", GRID_CELL_COLS, false);
           System.out.printf("%2d->|", i + 1);
           for(int j = 0; j < GRID_CELL_COLS; j++) {
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
       print("-----|", GRID_CELL_COLS, false);
       
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
                System.out.printf("Enter a row # (-1 -> Exit): ");
                row = inScan.nextInt();
                if (row == -1) {
                    break gameOn;
                }
                System.out.printf("Enter a col # (-1 -> Exit): ");
                col = inScan.nextInt();
                if (col == -1) {
                    break gameOn;
                }
                row--;
                col--;
                if (this.isValidElement(this.gameGrid, row, col) && 
                    this.gameGrid[row][col] == 0
                ) {
                    validInput = true;
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
        if ( row < 0 || row > GRID_CELL_ROWS - 1) {
            return;
        }
        if (col < 0 || col > GRID_CELL_COLS - 1) {
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
        MineSweeper mine = new MineSweeper();
        mine.setup();
        mine.playGame();
    }
}
