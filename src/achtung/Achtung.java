package achtung;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 *
 * @author marce
 * Sometimes the win condition bugs, didnt pay attention to it yet,
 * want to have AI done first (worst case AI will stop before finish).
 */
public class Achtung extends JFrame implements MouseListener, ActionListener{
    
    ArrayList<Dimension> mainPanesDimensions = new ArrayList();
    ArrayList<String> mainPanesNames = new ArrayList();
    ArrayList<Color> mainPanesColors = new ArrayList();
    ArrayList<LayoutManager> mainPanesLayoutMng = new ArrayList();
    Landmine[][] landmines;
    Boolean [][] revealedArea;
    Timer AITimer;
    Boolean AIisON = false;
    
    /**
     * This will be the new AI solving variable
     * It must be reinitialized when the grid is remade
     * Each value correspond to one cell of the MineField
     * it will not follow the order of the grid, but the open order
     * each value will cointain:
     * index 0 will be the line of the cell
     * index 1 will be the column of the cell
     * index 2 will be the danger of the cell
     * index 3 will be the status of the cell (solved or not)
     * when a cell uncovered, it must be recorded into this ArrayList
     * WHen the AI is started, she will:
     * 1.Look if the lenght of this ArrayList is greater than 0
     * 1.1 If the lenght is 0, it will open a random cell
     * 2.will verify every element is solved or not and try to solve it
     * 2.1 if a bomb is found, it will be placed in this ArrayList as a cell to be solved
     * when solving 
     * 
     * will be another array list with 
     */
    ArrayList AIgrid = new ArrayList(); // this will retain the clickable cells
    ArrayList AIcontrol = new ArrayList(); //this will have the order of clicked cells
    
    
    /**
     * Those are the ML variables.
     */
    private final int INPUT_NEURONS = 25;
    private final int INTERNEURONS_A = 8;
    private final int INTERNEURONS_B = 8;
    private final int MOTORNEURONS = 4;
    private static Neuron[] inputs;
    private static Neuron[] inA;
    private static Neuron[] inB;
    private static Neuron[] mN;
    
    
    Boolean youWin = false, gameOver = false, safeMove = false;
    int lin = 20, col = 20 , mines = 65, mines_left = mines;
    private final int MINE_GAP = 1;
    int clearedAreas = 0;
    
    JTextArea debugPannel = new JTextArea("Starting debug: ", 4, 64);
    JScrollPane debugScrollPane = new JScrollPane(debugPannel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    Boolean debugActive = true;
    
    Object tempClickTarget = new Object();
    
    public Achtung(){
                
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension (900,600));
        
        //initialize ML brain
        createBrain();
        
        mainPanesDimensions.add(new Dimension(800,50));
        mainPanesDimensions.add(new Dimension (100, 500));
        mainPanesDimensions.add(new Dimension(400,400));
        mainPanesDimensions.add(new Dimension (50, 500));
        mainPanesDimensions.add(new Dimension(800,50));
        
        mainPanesNames.add("North");
        mainPanesNames.add("West");
        mainPanesNames.add("Center");
        mainPanesNames.add("East");
        mainPanesNames.add("South");
        
        mainPanesColors.add(Color.blue);
        mainPanesColors.add(Color.red);
        mainPanesColors.add(Color.green);
        mainPanesColors.add(Color.orange);
        mainPanesColors.add(Color.LIGHT_GRAY);

        mainPanesLayoutMng.add(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainPanesLayoutMng.add(new GridLayout(5,1,5,5));//(FlowLayout.CENTER, 5, 5));
        mainPanesLayoutMng.add(new GridLayout(lin, col, MINE_GAP, MINE_GAP));
        mainPanesLayoutMng.add(new BorderLayout());
        mainPanesLayoutMng.add(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        
        debugPannel.setForeground(new Color(31, 234, 0));
        debugPannel.setBackground(Color.black);
        debugPannel.setEditable(false);
        
        
        JPanel pnl;
        for (int i = 0; i<5; i++){
            
        
            pnl = new JPanel();
            pnl.setName(mainPanesNames.get(i));
            //pnl.setPreferredSize(mainPanesDimensions.get(i));
            pnl.setBackground(mainPanesColors.get(i));
            pnl.setLayout(mainPanesLayoutMng.get(i));
            if (i == 4){
                pnl.add(debugScrollPane);
            }
            this.add(pnl, mainPanesNames.get(i));
        }
        
        
        
        createTopMenu();
        setWestPanel();
        createMineField();
        
        
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);     
        
    }
    
    private void setWestPanel(){
      JPanel pnl = new JPanel();
      pnl.setName("pnlConfigMineField");
      pnl.setLayout(new GridLayout(4,2));
      JLabel lbl = new JLabel("Columns");
      pnl.add(lbl);
      IntField intF = new IntField();
      intF.setText(""+col);
      intF.setName("setCol");
      intF.setPreferredSize(new Dimension(50, 27));
      pnl.add(intF);
      
      lbl = new JLabel("Lines");
      pnl.add(lbl);
      intF = new IntField();
      intF.setText(""+lin);
      intF.setName("setLin");
      intF.setPreferredSize(new Dimension(50, 27));
      pnl.add(intF);
      
      lbl = new JLabel("Mines");
      pnl.add(lbl);
      intF = new IntField();
      intF.setText(""+mines);
      intF.setName("setMines");
      intF.setPreferredSize(new Dimension(50, 27));
      pnl.add(intF);
      
      JButton btn = new JButton("Apply!");
      btn.addMouseListener(this);
      btn.setName("changeMineSettings");
      pnl.add(btn);
      
      getJContainerByName(this.getContentPane(), mainPanesNames.get(1)).add(pnl);
      
      pnl = new JPanel();
      pnl.setName("pnlMinesLeft");
      pnl.setPreferredSize(new Dimension(29, 29));
      
      pnl.setLayout(new FlowLayout(FlowLayout.CENTER));
      lbl = new JLabel("Mines left: ["+mines_left+"/"+mines+"]");
      lbl.setName("lblMinesLeft");
      lbl.setForeground(Color.red);
      pnl.add(lbl);
      
      getJContainerByName(this.getContentPane(), mainPanesNames.get(1)).add(pnl);
      
    }
    
    private void createTopMenu(){
      JButton btn = new JButton("New Game");
      btn.setName("new");
      btn.addMouseListener(this);
      getJContainerByName(this.getContentPane(), mainPanesNames.get(0)).add(btn);
      btn = new JButton("AI - Off");
      btn.setName("AI");
      btn.addMouseListener(this);
      getJContainerByName(this.getContentPane(), mainPanesNames.get(0)).add(btn);
      btn = new JButton("M.L. - ON");
      btn.setName("ML");
      btn.addMouseListener(this);
      getJContainerByName(this.getContentPane(), mainPanesNames.get(0)).add(btn);
    }
    
    /**
     * Clean up every object in the center JPanel and 
     * Generate a new mine field
     * implementing here...
     */
    private void createMineField (){
      //refresh the layout type to match the amount of rows and columns
      getJContainerByName(this.getContentPane(), mainPanesNames.get(2)).setLayout(new GridLayout(lin, col, MINE_GAP, MINE_GAP));
      landmines = new Landmine[lin][col];     
      revealedArea = new Boolean[lin][col];
      //AI 0.9 use those variables
      AIgrid = new ArrayList(); // this will retain the clickable cells
      AIcontrol = new ArrayList(); //this will have the order of clicked cells
      mines_left = mines;
      adjustMineCounter();
      clearedAreas = 0;
      for (int l = 0; l < lin; l++){
        for (int c = 0; c <col; c++){
          intToString(l, 3);
          landmines[l][c] = new Landmine(intToString(l, 2)+intToString(c, 2));
          landmines[l][c].addMouseListener(this);
          revealedArea[l][c] = false;
          gameOver = false;
          youWin = false;
          
          AIgrid.add(new Dimension (l, c));
          
          getJContainerByName(this.getContentPane(), mainPanesNames.get(2)).add(landmines[l][c]);
        }
      }
      placeMines(); 
      this.pack();
    }
    
    //Function to create String from a int value with '0' to the left
    private String intToString(int val, int lenght){
      String temp = Integer.toString(val);
      while (temp.length() < lenght){
        temp = "0"+temp;
      }
      return temp;
    }
    
    //function to (re)generate the mine field. It uses global variables as 
    //values for amount of lines, columns and mines
    private void placeMines(){
      ArrayList<Integer> minepos = new ArrayList();
      for (int i = 0; i<mines; i++){
        int pos = (int)(Math.random()*(col*lin));
        int pl = (int)pos/col;
        int pc = pos - (pl*col);
        if (!minepos.contains(pos)){
          landmines[pl][pc].setMine();
          landmines[pl][pc].setLabel("9");
          minepos.add(pos);
        } else {
          i--;
        }
      }
      setWarnings();
    }
    
    //Function to verify the amount of surounding mines.
    private void setWarnings(){
      int cont = 0;
      for (int cL = 0; cL < lin; cL++){
        for (int cC = 0; cC <col;cC++){
          cont = 0;
          if (!landmines[cL][cC].isMine()){
            cont += isMined(cL-1,cC-1);
            cont += isMined(cL,cC-1);
            cont += isMined(cL+1,cC-1);
            cont += isMined(cL-1,cC);
            cont += isMined(cL+1,cC);
            cont += isMined(cL-1,cC+1);
            cont += isMined(cL,cC+1);
            cont += isMined(cL+1,cC+1);
            landmines[cL][cC].setLabel(""+cont);
          }
        }
      }
    }
    
    //function to verify if the specified coordinate have a mine
    private int isMined(int l, int c){
      if (l < 0 || l >= lin || c < 0 || c >= col){ // if out of bounds
        return 0; 
      } else {
        if (landmines[l][c].isMine()){
          return 1;
        } else {
          return 0;
        }
      }
    }
    
    private void verifyNeighborhod (int l, int c){
      revealLandMine(l-1, c-1);
      revealLandMine(l, c-1);
      revealLandMine(l+1, c-1);
      revealLandMine(l-1, c);
      revealLandMine(l+1, c);
      revealLandMine(l-1, c+1);
      revealLandMine(l, c+1);
      revealLandMine(l+1, c+1);        
    }
    
    private void revealLandMine(int l, int c){
      //if out of bounds, do nothing
      if (l < 0 || l >= lin || c < 0 || c >= col){
        //do nothing, coords are out of bounds.
      } else {
        // if the cell have no warnings and isnt revealed, do reveal it and search neighboards
        if (landmines[l][c].getLabel().equals("0") && !revealedArea[l][c]){
          revealArea(l,c);
          verifyNeighborhod(l, c);
        } else if (!revealedArea[l][c]) {                
          revealArea(l,c);
        }
      }
    }
    
    //reveal área, if every cell (except the mines) are revealed, the output 'you win' in the console
    private void revealArea(int l, int c){
      landmines[l][c].removeCover();
      revealedArea[l][c] = true;
      clearedAreas++;
      trace("areas to clear: "+((lin*col)-mines-clearedAreas));
      if ((clearedAreas == ((lin*col)-mines))&&!landmines[l][c].isMine()){
        youWin = true;
        gameOver = true;
        trace("Game Over, you win " + youWin);
      }
      try {
        AIgrid.remove(new Dimension(l,c));// this will retain the clickable cells
        ArrayList list = new ArrayList();
        list.add(new Dimension(l,c));
        String lbl = landmines[l][c].getLabel();
        if (lbl != "0"){
          list.add(Integer.parseInt(landmines[l][c].getLabel()));
        } else {
          list.add(0);
        }
        list.add(false);
        AIcontrol.add(list);//this will have the order of clicked cells
      } catch (Exception aiEx){
        trace("Something went wrong when handling AI at RevealArea function. " +aiEx);
      }
    }
    
    /**
     * Remove every content of the central JPanel
     */
    private void removeMineField(){
      getJContainerByName(this.getContentPane(), mainPanesNames.get(2)).removeAll();
      landmines = null;
      getJContainerByName(this.getContentPane(), mainPanesNames.get(2)).repaint();
    }
    
    /**
     * Durchsuche den Container nach JPanel mit dem angegebenen Namen
     * @param con Container mit Elementen
     * @param name JPanel name
     **/
    public Container getJContainerByName(Container con, String name){
      JPanel jp = new JPanel();
      Container cont = con;
      
      Boolean found = false;
      try {
        for (int i = 0; i < con.getComponentCount(); i++){
          if (con.getComponent(i)instanceof JPanel){
            JPanel iftst = (JPanel)con.getComponent(i);
            if (iftst.getName().equals(name)){
              found = true;
              jp = iftst;
            }
          }
        }
        if (found) {
          return jp;
        } else {
          trace("Not found a JPanel with name:"+name+"MP");
          return null;
        }
      } catch (Exception err){
        return null;
      }        
    }
    
    /**
     * Easy way to output/debug in console
     * @param o Object to be printed on output console
     */
    public void trace(Object o){
      System.out.println(o);
      if (debugActive){
        debugPannel.append("\n"+o);
        debugPannel.setCaretPosition(debugPannel.getDocument().getLength());
      }
    }
    public static void main(String[] args) {
        Achtung ach = new Achtung();
    }
    
    private void cellLeftClick(int lin, int col){
      Landmine lm = landmines[lin][col];
      if (!lm.isDisabled() && lm.isCovered()){
        revealArea(lin, col);
        if (lm.getLabel().equals("0")){
          verifyNeighborhod(lin, col);
        }
        if (lm.isMine() && !lm.isDisabled()){
          youWin = false;
          gameOver = true;
          trace("Game over, you win " + youWin);
          for (int l=0;l<this.lin;l++){
            for (int c=0;c<this.col;c++){
              landmines[l][c].revealLandMine();
              landmines[l][c].removeMouseListener(this);
            }
          }
        }    
      }
      turnMLon();
    }
    
    private void cellRightClick (int lin, int col){
      if (landmines[lin][col].isDisabled()){
        mines_left++;
      } else {
        mines_left--;
      }
      adjustMineCounter();
      landmines[lin][col].togleDisable();
      turnMLon();
    }
    
    private void adjustMineCounter(){
      JPanel pnl = (JPanel) getJContainerByName(this.getContentPane(), mainPanesNames.get(1));
      JLabel lbl = new JLabel("Mines left: ["+mines_left+"/"+mines+"]");
      for (int i = 0; i <pnl.getComponentCount();i++){
        try {
          if (pnl.getComponent(i).getName().equals("pnlMinesLeft")){
            for (int j = 0; j < ((Container)pnl.getComponent(i)).getComponentCount(); j++){
              if (((Container)pnl.getComponent(i)).getComponent(j).getName().equals("lblMinesLeft")){
                ((JLabel)((Container)pnl.getComponent(i)).getComponent(j)).
                        setText("Mines left: ["+mines_left+"/"+mines+"]");
              }
            }
          }
        } catch (Exception asd){
        }
      }
    }

    @Override
    public void mouseReleased (MouseEvent e) {
      if (e.getSource()==tempClickTarget){
        if(e.getSource()instanceof Landmine){
          Landmine lm = (Landmine)e.getSource();
          int lin = Integer.parseInt(lm.getName().substring(0, 2));
          int col = Integer.parseInt(lm.getName().substring(2, 4));
          if (e.getButton()==1){
            cellLeftClick(lin, col);
          } else if (e.getButton()==3){
            cellRightClick(lin, col);
          }
        } else if (e.getSource()instanceof JButton){
          JButton b = (JButton)e.getSource();
          switch (b.getName()){
            case "new":
              removeMineField();
              createMineField();
              trace("################# NEW GAME #################");
              break;
            case "AI":
              toggleAI(b);
              break;
            case "ML":
              break;
            case "changeMineSettings":
              int cont = 0, lin = 0, col = 0, mines = 0;
            boolean problemFound = false;
            try{
              JPanel pnl = (JPanel) b.getParent();
              int cc = pnl.getComponentCount();
              for (int a = 0; a < cc; a++){
                try {
                  if (pnl.getComponent(a).getName().equals("setLin")){
                    lin = Integer.parseInt(((IntField) pnl.getComponent(a)).getText());
                    cont++;
                  } else if (pnl.getComponent(a).getName().equals("setCol")){
                    col = Integer.parseInt(((IntField) pnl.getComponent(a)).getText());
                    cont++;
                  } else if (pnl.getComponent(a).getName().equals("setMines")){
                    mines = Integer.parseInt(((IntField) pnl.getComponent(a)).getText());
                    cont++;
                  }
                } catch (Exception expp){
                }
              }
            } catch (Exception exp){
              trace("Something went wrong on JButton changeMineSettings");
              problemFound = true;
            }
            if (cont != 3){
              problemFound = true;
            }
            if (lin > 50 || lin < 1){
              trace("invalid amount of lines");
              problemFound = true;
            }
            if (col > 50 || col < 1){
              trace("invalid amount of columns");
              problemFound = true;
            }
            if (mines >= (col*lin)){
              problemFound = true;
            }
            if (!problemFound){
              this.lin = lin;
              this.col = col;
              this.mines = mines;
              removeMineField();
              createMineField();
            }
            break;
            default:
              trace("mas oi?"+b.getName()+"foi pressionado...");
              break;
          }
          if (b.getName().equals("new")){
            
          } else if (b.getName().equals("AI")){
            
          } else if (b.getName().equals("changeMineSettings")) {
            
          }
        }
      } else {
        trace("you dragged the mouse out of the target.");
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseClicked(MouseEvent e) {        
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
        tempClickTarget = e.getSource();
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//#################################################################################

    private void toggleAI(JButton b){
        if (gameOver){
            AITimer.stop();
        }
        if (AIisON){
            AIisON = false;
            b.setText("AI - Off");
            AITimer.stop();
            trace("Stop AI");
        } else {
            AIisON = true;
            b.setText("AI - ON");
            AITimer = new Timer(500, this);
            AITimer.start();
            trace("Start AI");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        if (AIisON && (e.getSource()instanceof Timer)){
            trace("action performed is instance of Timer - " + e.getSource());
            aiAction();
        }
    }
    
    public ArrayList neighborhodLabels(int linY, int colX){
        ArrayList list = new ArrayList();
        Dimension d = new Dimension(-1,-1);
        ArrayList item = new ArrayList();
        for (int y = linY-1; y <= linY +1; y++){
            for (int x = colX-1; x <= colX+1; x++){
                if ((y < 0 || y >= lin || x < 0 || x >= col)){
                //if out of bounds, still have to count as a cell, set as 
                //discovered, and just for safety, will carry the value out of 
                //bounds into the list aswell.
                    item = new ArrayList();
                    item.add(new Dimension(y, x));
                    item.add(true);
                    item.add(0);
                    list.add(item);
                } else {
                    if (!(y == linY && x == colX)){
                    //if not the cell itself coordinate, try:    
                        try {
                            if (landmines[y][x].isCovered()){
                            // if the coordinate is covered, will carry false value, as 'not visible'
                                item = new ArrayList();
                                item.add(new Dimension(y, x));
                                item.add(false);
                                if (landmines[y][x].isDisabled()){
                                    item.add(9);
                                } else {
                                    item.add(0);
                                }
                                list.add(item);
                            } else {
                            //if the coordinate is uncovered, will carry true value, as 'visible'
                                item = new ArrayList();
                                item.add(new Dimension(y, x));
                                item.add(true);
                                if (landmines[y][x].getLabel() != "0"){
                                    item.add(Integer.parseInt(landmines[y][x].getLabel()));
                                } else {
                                    item.add(0);
                                }
                                list.add(item);                                
                            }
                        } catch (Exception er){
                            trace("Something went wrong in function neighborhodLabels:\n"+er);
                        }
                    }
                }
            }
        }
        return list;
    }
        
    public void aiAction(){
        // just to control if the AI made something");
        Boolean AiMadeSomething = false;
        // force to slow depurate
        Boolean beSlow = true;
        // In case the AI is turned ON with no uncovered cells");
        if (AIcontrol.size() > 0 ){
            for (int cont = 0; cont < AIcontrol.size();cont++){
                //get the know atributes of the current cell.");
                int linY = (int)((Dimension)((ArrayList)AIcontrol.get(cont)).get(0)).getWidth();
                int colX = (int)((Dimension)((ArrayList)AIcontrol.get(cont)).get(0)).getHeight();
                int dangerValue = (int)((ArrayList)AIcontrol.get(cont)).get(1);
                Boolean solved = (Boolean)((ArrayList)AIcontrol.get(cont)).get(2);
                //trace("Coord: ["+linY+"]["+colX+"] Danger: ["+dangerValue+"] Solved: ["+solved+"].");
                //if the current cell is not solved, check if it can be solved!"+
                // the first step is to try to disable neighborhod bombs");
                if (!solved){
                    //verify the neighborhod cell, if the are uncovered");
                    ArrayList list = neighborhodLabels(linY, colX);
                    int visibleCells = 0, knowBombs = 0;
                    for (int aa = 0; aa < list.size();aa++){
                        if ((Boolean)((ArrayList)list.get(aa)).get(1)){
                            visibleCells++;
                        }
                        if ((int)((ArrayList)list.get(aa)).get(2)==9){
                            knowBombs++;
                        }
                    }
                    // if the sum of the dangerValue plus visibleCells equals 8"+
                    // every covered cell is a bomb!");
                    if (visibleCells+dangerValue == 8){
                        for (int aa = 0; aa< list.size();aa++){
                            if (!(Boolean)((ArrayList)list.get(aa)).get(1)){
                                Dimension d =((Dimension)((ArrayList)list.get(aa)).get(0));
                                int li = (int)d.getWidth();
                                int co = (int)d.getHeight();
                                trace("verify if landmines["+li+"]["+co+"] is disabled");
                                if (!landmines[li][co].isDisabled()){
                                    trace("disabling it");
                                    cellRightClick (li, co);
                                    ArrayList item = new ArrayList();
                                    item.add(new Dimension(li, co));
                                    item.add(9);
                                    item.add(false);
                                    AIgrid.remove(new Dimension(li,co));// this will retain the clickable cells

                                    AIcontrol.add(item);
                                    knowBombs++;
                                }
                            }
                        }
                        solved = true;
                        ((ArrayList)AIcontrol.get(cont)).set(2, solved);
                        AiMadeSomething = true;
                    }
                    // after marking all possible bombs, if still not solved
                    // verify if the dangerValue correspond the amount of know surounding mines
                    // and if so, uncover every other panel.
                    if (dangerValue == knowBombs){
                        trace("dangerValue equals knowBombs");
                        for (int aa = 0; aa< list.size();aa++){
                            Dimension d = (Dimension)((ArrayList)list.get(aa)).get(0);
                            if (!(Boolean)((ArrayList)list.get(aa)).get(1) && !landmines[(int)d.getWidth()][(int)d.getHeight()].isDisabled()){
                                cellLeftClick((int)d.getWidth(), (int)d.getHeight());
                            }
                        }
                        AiMadeSomething = true;
                    }
                    
                }
                
                int uncoveredCells = 0;
                if(AiMadeSomething && !beSlow){
                    AiMadeSomething = false;
                    break;
                } else {
                    //must suround with try catch
                    //Dimension dim = (Dimension) AIgrid.get((int)(Math.random()*AIgrid.size()));
                    //cellLeftClick(dim.height, dim.width);
                    //AiMadeSomething = true;
                }
            }
        } else {
            //must surround with try catch
            // open a random cell
            //Dimension dim = (Dimension) AIgrid.get((int)(Math.random()*AIgrid.size()));
            //cellLeftClick(dim.height, dim.width);
            //AiMadeSomething = true;
        }
    }
    
    public void turnMLon(){
        //this will load every area of 
        for (int li = 0; li < lin; li++){
            for (int co = 0; co < col; co++){
                if (landmines[li][co].isCovered()){
                    newValues(getAreaAroundTarget(li, co));
                    think(li, co);
                }
            }
        }
    }
    
    public ArrayList getAreaAroundTarget(int li, int co){
        ArrayList list = new ArrayList();
        int minLine = li - 2, maxLine = li + 2;
        int minColumn = co - 2, maxColumn = co + 2;
        for (int y = minLine; y <= maxLine; y++){
            for (int x = minColumn; x <= maxColumn; x++){
                if (y < 0 || y >= lin || x < 0 || x >= col){ //if out of bounds
                    list.add(-1.0);
                } else {
                    if (landmines[y][x].isCovered()){
                        list.add(-1.0);
                    } else {
                        list.add(Double.parseDouble(landmines[y][x].getLabel())/2);
                    }
                }
            }
        }
        //trace(list);
        return list;
    }
    
    /**
     * @param val - refer to the value to be calculated
     * @param type - refer to the type of formula to be used<BR>
     * type 0 gives a value between -1/+1 and the bigger the value stray to 0 it goes<BR>
     * type 1 gives a value between 0/1 and it can compress from -200(0) to +200(1)
     */
    private Double sigmoid(Double val, int type){
      switch (type) {
        case 0:
          return val/(1+Math.abs(val));
        case 1:
          return (1 / (1+Math.exp(-val/10)));
        default:
          return 0.0;
      }
    }
    
    public void think(int l, int c){
        for (int ina = 0; ina < inA.length; ina++){
            Double val = 0.0;
            for (int x = 0; x < INPUT_NEURONS;x++){
                val += inputs[x].value() * inA[ina].getAncestor(x);
            }
            inA[ina].value(sigmoid(val,0));
        }
        for (int inb = 0; inb < inB.length; inb++){
            Double val = 0.0;
            for (int x = 0; x < INTERNEURONS_A; x++){
                val += inA[x].value() * inB[inb].getAncestor(x);
            }
            inB[inb].value(sigmoid(val,0));            
        }
        //trace(inAVal);
        for (int mn = 0; mn < mN.length; mn++){
            Double val = 0.0;
            for (int x = 0; x < INTERNEURONS_B; x++){
                val += inB[x].value() * mN[mn].getAncestor(x);
            }
            mN[mn].value(sigmoid(val,1));
        }
        Double max = 0.0;
        int maxPos = 0;
        for (int x = 0; x < MOTORNEURONS;x++){
            if (mN[x].value() > max){
                maxPos = x;
            }
        }
        String result= "";
        maxPos = checkRealSituation(l, c);
        switch (maxPos) {
            case 0:
                result = "Unknow";
                landmines[l][c].setBkg(Color.gray);
                break;
            case 1:
                result = "Uncertain";
                landmines[l][c].setBkg(Color.yellow);
                break;
            case 2:
                result = "Bomb";
                landmines[l][c].setBkg(Color.red);
                break;
            case 3:
                result = "Safe";
                landmines[l][c].setBkg(Color.green);
                break;
        }
        adjustWeights(maxPos, checkRealSituation(l, c));
        //trace ("I think pos("+l+", "+c+") is " + result);
        
        
    }
    
    private void adjustWeights(int mlAnswer, int correctAnswer){
      
      //adjust motor weights
      for (int mn = 0; mn < mN.length; mn++){
        if (mn == correctAnswer){
          Double highest=0.0, second=0.0, third=0.0;
          int posH = 0, posS = 0, posT = 0;
          for (int anc = 0; anc < mN[mn].getAncestorCount();anc++){
            if (mN[mn].getAncestor(anc) >= highest){
              third = second; posT = posS;
              second = highest; posS = posH;
              highest = mN[mn].getAncestor(anc); posH = anc;
            }
          }
          for (int anc = 0; anc < mN[mn].getAncestorCount();anc++){
            String as = "Anc."+anc+" from: ";
            as += mN[mn].getAncestor(anc) +" to: ";
            if (anc == posH || anc == posS || anc == posT){
              mN[mn].setAncestor(anc, mN[mn].getAncestor(anc)+((Math.sqrt(Math.abs(mN[mn].getAncestor(anc))-1))/2)  );
            } else {
              mN[mn].setAncestor(anc, mN[mn].getAncestor(anc)-((Math.sqrt(Math.abs(mN[mn].getAncestor(anc))-0))/2)  );              
            }
            as += mN[mn].getAncestor(anc);
            trace(as);
          }
        }
      }
      //adjust second layer
      //adjust first layer
    }
    
    public int checkRealSituation(int lin, int col){
      /*mN[0] seek for unknow - gray
        mN[1] seek for uncertain - yellow
        mN[2] seek for bomb - red
        mN[3] seek for safe - green */
        ArrayList[] realSit = new ArrayList[4];
        for (int i = 0; i < realSit.length; i++){
          realSit[i] = new ArrayList();
        }
        int neighborCovered = 0;
        for (int li = (lin-1); li <=(lin+1);li++){
          for (int co = (col-1); co <= (col+1); co++){
            if (li < 0 || li >= this.lin || co < 0 || co >= this.col){
                neighborCovered++;
            } else {
              if (landmines[li][co].isCovered()){
                neighborCovered++;
              } else {
                int dl = Integer.parseInt(landmines[li][co].getLabel());
                int outerNeighborhoodUncovered = 0;
                int outerNeighborhoodDisabled = 0;
                for (int l = (li-1); l <= (li+1);l++){
                  for (int c = (co-1); c <= (co+1);c++){
                    if (l < 0 || l >= this.lin || c < 0 || c >= this.col){
                      outerNeighborhoodUncovered++;
                    } else {
                      if (!landmines[l][c].isCovered()){
                        outerNeighborhoodUncovered++;
                      }
                      if (landmines[l][c].isDisabled()){
                        outerNeighborhoodDisabled++;
                      }
                    }
                  }
                }
                if (dl == outerNeighborhoodDisabled && !landmines[lin][col].isDisabled()){
                  realSit[3].add(new Point(lin, col));
                  return 3; // safe
                }
                if (dl + outerNeighborhoodUncovered == 9){
                  realSit[2].add(new Point(lin, col));
                  return 2; // bomb
                }
                
              }
            }
          }
        }
        if (neighborCovered == 9){
          realSit[0].add(new Point(lin, col));
          return 0;
        }
        realSit[1].add(new Point(lin, col));
        return 1;
    }
    
     public void newValues(ArrayList list){
        if (list.size() == inputs.length){
            for (int ind = 0; ind < inputs.length;ind++){
                inputs[ind].value((Double)list.get(ind));
            }
        }
    }
    
    public void createBrain (){
        inputs = new Neuron[INPUT_NEURONS];
        for (int in = 0; in < INPUT_NEURONS; in++){
            inputs[in] =  new Neuron();
        }
        inA = new Neuron[INTERNEURONS_A];
        for (int ina = 0; ina < INTERNEURONS_A;ina++){
            inA[ina] = new Neuron();
            for (int in = 0; in < INPUT_NEURONS; in++){
                inA[ina].addAncestor(1.0);
            }
        }
        inB = new Neuron[INTERNEURONS_B];
        for (int inb = 0; inb < INTERNEURONS_B; inb++){
            inB[inb] = new Neuron();
            for (int ina = 0; ina < INTERNEURONS_A;ina++){
                inB[inb].addAncestor(1.0);
            }
        }
        mN = new Neuron[MOTORNEURONS];
        for (int mn = 0; mn < MOTORNEURONS; mn++){
            mN[mn] = new Neuron();
            for (int inb = 0; inb < INTERNEURONS_B; inb++){
                mN[mn].addAncestor(1.0);
            }
        }
    }

}



/**
 * To make a AI solve the mine field, first I need to make 2 globals
 * um list with the order of last oppened cell List<point>
 * and a second to say for each of the first one if it was troughtly cheked List<Boolean>
 * Mayble a Matrix with the same size as the minefield to record every index of those before
 * 
 * The first click must be random. Depending of the result the analize start
 * The goal is to Find a Mine and neutralize it.
 * To do so, check if the oppened cell is a 1 warning with 7 surounding oppened cells,
 * the only who isnt, is the mine (if warning is 2, 6 oppened, warning 3, 5 oppened and so on)
 * 
 * after finding a mine, disable it and check it neigboards, it there is a cell 
 * with warning '1' then the AI can open every cell but the mine (who should be 
 * disabled by now), and then check again if there is a 'visible' mine using the
 * algorithm before
 * 
 * 1. Random Click;
 * 2. Search for Mine;
 * 3. Disable cell with Mine;
 * 4. Open surounding cells of the Mine when possible;
 * 5. Repeat from step 2;
 */

    
    /**
     * Machine Learning project
     * 
     * The ML will have 25 inputs, 2 neural layers of at least 8 cells and 
     * 4 possibles outputs
     * 
     * Step 1:
     * The machine learning will analize a area of 5x5 around the target cell
     * in other words, the imput area will be of 25 squares (including the target)
     * The parameters will be the 'label' containing the danger of the tile.
     * A covered tile will have the value of 10, a disabled 9.
     * In the case of the analized area exceds the bounds of the minefield
     * The cells out of bounds will be trated as covered.
     * 
     * The weights will be random. and whould be rearanged latter.
     * 
     * Step 2:
     * Processing the input in the 1st layer.
     * Each neural layer will receive the (parameter * weight) of each input and
     * sum it, after add a *bias* (random initialized from the neuron itself)
     * after it, the result will be 'flattened' via a sigmoid function to a 
     * value between 0 ~ 1. This value will be then passed forward.
     * 
     * The outcome could be "0 = Unknow", "1 = Uncertain", "2 = Bomb" or "3 = Good to go"
     * The outcome will be displayed to the user in the minefield with the
     * cell background color atribbute.
     * "0 = Unknow" -> Gray (no changes)
     * "1 = Uncertain" -> Yellow
     * "2 = Bomb" -> Red
     * "3 = Good to go" -> Green
     * 
     * 1. Open or not:
     * The neural must decide when to open or not, a cell.
     * it will analize an area of 5x5 around the desired cell.
     * The values for inputs should be equal to the visible label, 
     * For that, the analises should be just looking the neighborhood of chosen
     * cell. The visible labels should have weight 
     * 
     */


