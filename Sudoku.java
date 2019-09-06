/**
 *
 * @author Rohan Pandey
 * This is an implementation of SUDOKU Logic in Java.
 * This is an implementation of SUDOKU Logic in Java.
 * It comprises of 3 functionalities:
 * 1. Solving the given sudoku state
 * 2. Adding file from CSV
 * 3. Clearing the Sudoko space
 * 
 * Sudoku is solved using constraints satisfaction and backtracking. 
 * 
 *  * Three constraints are needed for Sudoku:
 * 1. Row Constraint: All numbers in the row of a particular cell must be unique(1-9)
 * 2. Column Constraint: All numbers in the column of a particular cell must be unique(1-9)
 * 3. Box Constraint: All numbers in the 3*3 matrix of the cell must be unique(1-9)
 * 
 * The Sudoku iterates row wise and returns the first empty position, we look through numbers 1 to 9 to identify 
 * the possible solutions that satisfy the constraints at this position, the first number from 1-9 satisfying the constraints
 * is put at this position, this procedure continues in the same manner. Whenever a state of no constraint satisfaction 
 * is reached the program backtracks to the previous cell and increases the number at that position and again starts 
 * with constraint satisfaction. Whenever a particular row is filled. the solver moves onto the next column. After completion of
 * the process, the user is told if the it was able to solve the sudoku or if the solution does not exist.
 * 
 * The functionality to add the initial Sudoku state is also given. Any CSV file can be utilized to do the same,
 * the format of the CSV file should be the following:
 * 1. All filled positions must be given an input between 1-9
 * 2. All empty spaces must be filled as 0.
 * 
 * The sudoku matrix can also be cleared using the clear button, providing a fresh new table to begin the computation with.
*/

package sudoku;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

//This class is created to manipulate the Jtable and to provide colour to specific coells
class TableCellRenderer extends DefaultTableCellRenderer {
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col) {
      Component c = super.getTableCellRendererComponent(table, value,
               isSelected, hasFocus, row, col);
      
      Color colo;
      if(((col>=3 &&col<6) && (row>=0&&row<3))||((col>=3 &&col<6) && (row>=6&&row<9))||((col>=0 &&col<3) && (row>=3&&row<6))||((col>=6 &&col<9) && (row>=3&&row<6)))
      {
        colo = Color.LIGHT_GRAY;//Setting the grey colour
      }
      else
          colo=new Color(30-60-255); //Rest cells have the new colour
      c.setBackground(colo); // Colour of the component is set
      return c; //The new formatted componenet is returned
   }
}

//This cass is used for the entire sudoku program
public class Sudoku extends JFrame{
    
    DefaultTableModel model = new DefaultTableModel(9,9) {
         @Override
         public Class<?> getColumnClass(int columnIndex) {
            return String.class;
         }
      };
    
    static int[][] board = new int[9][9];  //The 2-D array maintaining the board positions
    boolean executedonce=false; //Boolean checking if the computation in background has been completed once
    JPanel p1 = new JPanel();//Panel containing the buttons
    JPanel tablepanel = new JPanel(new BorderLayout());//Panel for the table
    JTable table = new JTable(9,9); //9*9 Sudoku board
    
//This function is used to verify if the String given as input is a numeric digit between 1 and 9
    public static boolean isNumeric(String number) {
    boolean num=false;
    try {
        int d = Integer.parseInt(number);//Parsing the string into integer
        num=true;
        if(num=true)
        {
            if(d>0 &&d<=9)//Is the number within legal range
            {
                return true;
            }
        }
    } catch (NumberFormatException | NullPointerException nfe) {
        return false;
    }
    return false;
}
    
    //This constructor initializes the entire GUI and performs all the functionalities by the click of the buttons by calling the defined functions.
    public Sudoku(){
     
        table = new JTable(model);  //Initializing the table model
        table.setDefaultRenderer(String.class, new TableCellRenderer());//Setting the cell colours using the renderer
        int ir,ic;
        for(ir=0;ir<9;ir++)
            for(ic=0;ic<9;ic++)
                board[ir][ic]=Integer.MIN_VALUE;//Initializing initial board with MIN values
        
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(null);
        this.setSize(600,625);
        this.setLocationRelativeTo(null);
        this.setTitle("Sudoku");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//Getting the screensize
        
        JButton b1 = new JButton("Solve The Sudoku");//Button to solve the sudoku
        JButton b2 = new JButton("Add from file");//Button to add the soduko state from a CSV file
        JButton b3 = new JButton("Clear");//Button to clear the board
                
        b1.setPreferredSize(new Dimension(175,30));
        b2.setPreferredSize(new Dimension(175,30));
        b3.setPreferredSize(new Dimension(175,30));
        
        p1.setBorder(new EtchedBorder());
        p1.add(b1);
        p1.add(b2);
        p1.add(b3);
        p1.setSize(screenSize.width/5, (int) ((Integer)screenSize.height/6.5));
        p1.setLocation(160,450);
        table.setFont(new Font("Serif", Font.BOLD, 20));
        table.setSelectionBackground(null);
        table.setRowHeight(46);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(String.class,new TableCellRenderer());
        tablepanel.add(table,BorderLayout.CENTER);
        tablepanel.setBorder(new EtchedBorder());
        tablepanel.setSize(560,417);
        tablepanel.setLocation(20,10);
        
        //Initializing the solving computation in background so that the computation thread can keep running
        SwingWorker s = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        if(solve())
                        {
                           JOptionPane.showMessageDialog(null,"Sudoku is solved");
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null,"No Solution Possible");
                        }
                    return null; 
                    }
                 
                };
        
        //Listener to the button which solves the sudoku
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                int i,j;
                boolean yes = Legal();//Check if the sudoku entries are legal
                if(yes==false)
                {
                    JOptionPane.showMessageDialog(null,"Incorrect Input");
                    
                }
                else{
                    for(i=0;i<9;i++)
                    {
                        for(j=0;j<9;j++)
                            if(isNumeric((String)table.getValueAt(i,j)))
                                if(table.getValueAt(i,j)!=null) //Checking if cell is not empty
                                    if((Integer.parseInt((String)table.getValueAt(i, j))>0)&&(Integer.parseInt((String) table.getValueAt(i, j))<10))
                                    {   
                                        board[i][j]=Integer.parseInt((String)table.getValueAt(i, j));//Passing the board content to the array
                                    }
                }
                s.execute();//Sudoku solver is invoked
                executedonce=true;//Solver has been executed once
            }
            }
        });
        
        //Listener to the button which solves the sudoku
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            if(s.isDone()||!executedonce){    
                JFileChooser fc = new JFileChooser();//Opens file chooser to select the CSV file
                File file = null;
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    file = fc.getSelectedFile();  //Get the selected file
                } 
                String entire = "";//String for all balues
                String [] data = null;//Array for storing the individual cell numbers
                BufferedReader readfile = null;//Reader to read the loaded file
                    try 
                    {
                        String row;
                        try
                        {
                            readfile = new BufferedReader(new FileReader(file));//Reading the file
                        }
                        catch(NullPointerException ex)
                        {
                            ;
                        }
                        try 
                        {   
                            try
                                {
                                    while ((row = readfile.readLine()) != null) 
                                    entire+=row+',';//Appending each row to the entire string
                                }
                                catch(NullPointerException ex)
                                {
                                    ;
                                }
                            } catch (IOException ex ) 
                            {
                                Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            } catch (FileNotFoundException ex) {
                    Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                }
                data = entire.split(",");//Splitting entire string to get individual values
                int count=0;//Count for the entire string
                int i,j;
                for(i=0;i<9;i++)
                {
                    for(j=0;j<9;j++)
                    {
                      try{  if(Integer.parseInt(data[count])!=0)
                            table.setValueAt((data[count++]),i,j);//Setting the values from the CSV to the Jtable/Board
                        else
                            count++;
                      }catch(NumberFormatException ex)
                      {
                          ;
                      }
                      }
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Solver running. Please wait to add file.");//To not allow selection of file when solver is running
            }
            }});
        
        //Listener to the button which clears the sudoku board
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            if(s.isDone()||!executedonce){
            DefaultTableModel dm = (DefaultTableModel) table.getModel();
            for (int i = 0; i < dm.getRowCount(); i++) {
                for (int j = 0; j < dm.getColumnCount(); j++) {
                    dm.setValueAt("", i, j);//Clearing all the cells in the board
                }
            }
            }
            else
                JOptionPane.showMessageDialog(null,"Solver Running. Please wait to clear.");//To not allow selection of file when solver is running
            }
        }); 
        this.add(tablepanel);
        this.add(p1); 
    }
    
    //This is the main function that calls the sudoku constructor
    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
    }
    
    //This function verifies the legality of the inputs in the sudoku by verifying that each row, column and box contains unique and valid entries(1-9)
    public boolean inputlegal(int row,int col,int num)
    {
        //Checking if the row input is legal
        int i,j;
        for(i=0;i<9;i++)
        {
            if(i!=col)
            {   
                if(table.getValueAt(row,i)!=null && isNumeric((String)table.getValueAt(row, i)))//Checking that the cell is not empty and is numeric between 1-9
                {
                    if(Integer.parseInt((String)table.getValueAt(row,i))==num)
                    {
                        return false;
                    }
                }
            }
        }
        
        //Checking if the column input is legal
        for(i=0;i<9;i++)
        {
            if(i!=row)
            { 
                if(table.getValueAt(i,col)!=null && isNumeric((String)table.getValueAt(i,col)))
                {
                    if(Integer.parseInt((String)table.getValueAt(i,col))==num )//Checking that the cell is not empty and is numeric between 1-9
                    {
                        return false;
                    }
                }
            }
        }
        
        //Checking if 3*3 box numbers are legal
        int startrow = ((int)(row/3))*3;//Starting row position for the box of that cell
        int startcol = ((int)(col/3))*3;//Starting column position for the box of that cell
        for(i=startrow;i<startrow+3;i++)
            for(j=startcol;j<startcol+3;j++)
                if(row!=i && col!=j)
                    if(table.getValueAt(i,j)!=null && isNumeric((String)table.getValueAt(i,j)))//Checking that the cell is not empty and is numeric between 1-9
                        if(Integer.parseInt((String)table.getValueAt(i,j))==num)
                            return false; 
        return true;    
    }
    
    //This function verifies the row constraint for the given row and number to be positioned
    public static boolean RowConstraint(int row,int num){
        int i;
        for(i=0;i<9;i++)
            if(board[row][i]==num)//Checking if number already exists in row
                return false;                   
        return true;
    }
    
    //This function verifies the column constraint for the given column and number to be positioned
    public static boolean ColumnConstraint(int col,int num){
        int i;
        for(i=0;i<9;i++)
            if(board[i][col]==num)//Checking if number already exists in column
                return false;
        return true;
    }
    
    
    //This function verifies the box constraint for the given 3*3 box and number to be positioned
    public static boolean BoxConstraint(int row,int col,int num){
        
        int startrow = ((int)(row/3))*3;//Starting row position for the box of that cell
        int startcol = ((int)(col/3))*3;//Starting column position for the box of that cell
        int i,j;
        for(i=startrow;i<startrow+3;i++)
            for(j=startcol;j<startcol+3;j++)
                if(board[i][j]==num)//Checking if number already exists
                    return false; 
        return true;
    }
   
    //This function verifies if the input in the sudoku as entered by the user is legal or not
    public boolean Legal()
    {
        int i,j,row,col,current;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {
                if(table.getValueAt(i,j)!=null && !"".equals((String)table.getValueAt(i,j)))//Checking if cell is not empty
                {         
                    if(isNumeric((String)table.getValueAt(i,j))==false) //If value is not a number between 1-9
                    {
                        return false;
                    }
                current = Integer.parseInt((String)table.getValueAt(i,j));//Fetching the value of the cel
                row=i;//Saving the row position
                col=j;//Saving the column position
                if(inputlegal(row, col, current)==false)
                    return false;
                }
            }
        }
        return true;
    }
    
    //The recursive function used to enable backtracking and using constraint satisfaction
    public boolean solve()
    {
        int i,j;
        int flag=0;
        int row=-1;
        int column=-1;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {
                if(board[i][j]==Integer.MIN_VALUE) //Identifying the first empty position
                {   
                    row=i;      //storing the row location of empty cell
                    column=j;   //storing the column location of empty cell
                    flag=1;     //flag to indicate the occurence of an empty position
                    break;
                }
            }
            if(flag==1)
                break;
        }
        if(flag==0)
        {
            return true;
        }
        
        //Setting the vaues from 1-10 by traversing rows
        for(i=1;i<10;i++)
        {
            if(RowConstraint(row,i)&&ColumnConstraint(column,i)&&BoxConstraint(row,column,i))//Checking if the number satisfies all constraints
            {   
                board[row][column]=i;//Setting the legal number in 2-D array
                setboard(row,column,Integer.toString(i));//Setting the board
                try {
                    Thread.sleep(5);//Adding sleep to display backtracking
                } catch (InterruptedException ex) {
                    Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(solve())
                {
                    return true;//If it solves then it returns true
                }
                else//If the constraint is not satisfied
                {
                    board[row][column]=Integer.MIN_VALUE;//Sets the board back to MIN value for that position
                    try {
                        Thread.sleep(5);//Adding sleep to display backtracking
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    setboard(row,column,"");//Clears the board position for the incorrect value and backtracks
                }
            }
        }
        return false;
    }
    
    //Setting the value of the board at specified row and column with the specified value
    public void setboard(int row, int column, String i){
        table.setValueAt(i,row,column);//Setting the value i at position row,column for the table
    }
} 