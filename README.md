# Sudoku 
This is an implementation of SUDOKU Logic in Java.  
It comprises of 3 functionalities:
  1. Solving the given sudoku state
  2. Adding file from CSV 
  3. Clearing the Sudoko space

Sudoku is solved using constraints satisfaction and backtracking. Three constraints are needed for Sudoku: 
  1. Row Constraint: All numbers in the row of a particular cell must be unique(1-9)
  2. Column Constraint: All numbers in the column of a particular cell must be unique(1-9) 
  3. Box Constraint: All numbers in the 3*3 matrix of the cell must be unique(1-9)
  
The Sudoku iterates row wise and returns the first empty position, we look through numbers 1 to 9 to identify the possible solutions that satisfy the constraints at this position, the first number from 1-9 satisfying the constraints is put at this position, this procedure continues in the same manner. Whenever a state of no constraint satisfaction is reached the program backtracks to the previous cell and increases the number at that position and again starts with constraint satisfaction. Whenever a particular row is filled. the solver moves onto the next column. After completion of the process, the user is told if the it was able to solve the sudoku or if the solution does not exist. 

The functionality to add the initial Sudoku state is also given. Any CSV file can be utilized to do the same, the format of the CSV file should be the following: 
  1. All filled positions must be given an input between 1-9 
  2. All empty spaces must be filled as 0. 
  
The sudoku matrix can also be cleared using the clear button, providing a fresh new table to begin the computation with. */
