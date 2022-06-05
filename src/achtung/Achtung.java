package achtung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

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
    int [][] aiLandField;
    Boolean [][] solvedCell;
    Point [] RevealedPosition;
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
    
    Boolean youWin = false, gameOver = false, safeMove = false;
    int lin = 20, col = 20 , mines = 40;
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
        
        RevealedPosition = new Point[lin*col];
        
        
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
        mainPanesLayoutMng.add(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainPanesLayoutMng.add(new GridLayout(lin, col, 0, 0));
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
        
        getJPanelByName(this.getContentPane(), mainPanesNames.get(1)).add(pnl);
        
        
    }
    
    private void createTopMenu(){
        JButton btn = new JButton("New Game");
        btn.setName("new");
        btn.addMouseListener(this);
        getJPanelByName(this.getContentPane(), mainPanesNames.get(0)).add(btn);
        btn = new JButton("AI - Off");
        btn.setName("AI");
        btn.addMouseListener(this);
        getJPanelByName(this.getContentPane(), mainPanesNames.get(0)).add(btn);
        
    }
    
    /**
     * Clean up every object in the center JPanel and 
     * Generate a new mine field
     * implementing here...
     */
    private void createMineField (){
        //refresh the layout type to match the amount of rows and columns
        getJPanelByName(this.getContentPane(), mainPanesNames.get(2)).setLayout(new GridLayout(lin, col, 0, 0));
        landmines = new Landmine[lin][col];     
        revealedArea = new Boolean[lin][col];
        solvedCell = new Boolean[lin][col];
        //AI 0.9 use those variables
        AIgrid = new ArrayList(); // this will retain the clickable cells
        AIcontrol = new ArrayList(); //this will have the order of clicked cells
    
        clearedAreas = 0;
        for (int l = 0; l < lin; l++){
            for (int c = 0; c <col; c++){
                intToString(l, 3);
                landmines[l][c] = new Landmine(intToString(l, 2)+intToString(c, 2));
                landmines[l][c].addMouseListener(this);
                revealedArea[l][c] = false;
                solvedCell[l][c] = false;
                gameOver = false;
                youWin = false;
                
                AIgrid.add(new Dimension (l, c));
            
                getJPanelByName(this.getContentPane(), mainPanesNames.get(2)).add(landmines[l][c]);
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
        int pos = 0;
        for (int i = 0; i<mines; i++){
            pos = (int)(Math.random()*(col*lin));
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
                if (cont > 0){
                landmines[cL][cC].setLabel(""+cont);
                }
            }}
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
            if (landmines[l][c].getLabel()=="" && !revealedArea[l][c]){
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
            if (lbl != ""){
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
        getJPanelByName(this.getContentPane(), mainPanesNames.get(2)).removeAll();
        landmines = null;
        getJPanelByName(this.getContentPane(), mainPanesNames.get(2)).repaint();
    }
    
    /**
     * Durchsuche den Container nach JPanel mit dem angegebenen Namen
     * @param con Container mit Elementen
     * @param name JPanel name
     **/
    public JPanel getJPanelByName(Container con, String name){
        JPanel jp = new JPanel();
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
        if (!lm.isDisabled()){
        revealArea(lin, col);
        if (lm.getLabel()==""){
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
        
    }
    private void cellRightClick (int lin, int col){
        landmines[lin][col].togleDisable();
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
            if (b.getName().equals("new")){
                removeMineField();
                createMineField();
            } else if (b.getName().equals("AI")){
                toggleAI(b);
            } else if (b.getName().equals("changeMineSettings")) {
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
                trace(problemFound);
                if (!problemFound){
                    this.lin = lin;
                    this.col = col;
                    this.mines = mines;
                    removeMineField();
                    createMineField();
                }
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
    /**
     * 
     * 
     * 
     *  Boolean [][] revealedArea;
        Point [] RevealedPosition;
        int [][] aiLandField;
        Boolean [][] solvedCell;
    * 
     */
    
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
                                if (landmines[y][x].getLabel() != ""){
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
        // In case the AI is turned ON with no uncovered cells");
        if (AIcontrol.size() > 0){
            for (int cont = 0; cont < AIcontrol.size();cont++){
                //get the know atributes of the current cell.");
                int linY = (int)((Dimension)((ArrayList)AIcontrol.get(cont)).get(0)).getWidth();
                int colX = (int)((Dimension)((ArrayList)AIcontrol.get(cont)).get(0)).getHeight();
                int dangerValue = (int)((ArrayList)AIcontrol.get(cont)).get(1);
                Boolean solved = (Boolean)((ArrayList)AIcontrol.get(cont)).get(2);
                trace("Coord: ["+linY+"]["+colX+"] Danger: ["+dangerValue+"] Solved: ["+solved+"].");
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
trace("here the ai will add a item to AIcontrol who mayble can cause a "
        + "error IF the same cell is already disabled manually - function aiAction()");                                    
                                    AIcontrol.add(item);
                                    knowBombs++;
                                }
                            }
                        }
                        solved = true;
                        ((ArrayList)AIcontrol.get(cont)).set(2, solved);
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
                    }
                    
                }
                
                int uncoveredCells = 0;
            }
        } else {
            // open a random cell
            int linY = (int) Math.random()*lin;
            int colX = (int) Math.random()*col;
            //cellLeftClick(linY, colX);
            AiMadeSomething = true;
        }
        
        
        /**
         * First try to make AI, can only mark bombs it can find in the field.
         *
        
        for (int linY = 0; linY < lin; linY++){
            for (int colX = 0; colX < col; colX++){
                if (!landmines[linY][colX].isCovered()){
                    if (!solvedCell[linY][colX]){
                        if (landmines[linY][colX].getLabel().equals("")){
                            solvedCell[linY][colX] = true;
                        } else {
                            int thisDangerLevel = Integer.parseInt(landmines[linY][colX].getLabel());
                            trace("Checking coord["+linY+"]["+colX+"] | DangerLevel = "+thisDangerLevel);
                            ArrayList list = neighborhodLabels(linY, colX);
                            int totalVisibleCells = 0;
                            
                            for (int c = 0; c < list.size();c++){
                                
                                trace(list.get(c));
                                if ((Boolean)((ArrayList)list.get(c)).get(1)){
                                    totalVisibleCells++;
                                }
                            }
                            //find and disable cells with bombs
                            //this code pass, it work - but not optimized
                            trace("totalVisibleCells = " + totalVisibleCells + "thisDangerLevel = "+ thisDangerLevel);
                            if (totalVisibleCells+thisDangerLevel == 8){
                                trace("totalVisibleCells+thisDangerLevel == 8");
                                for (int c = 0; c <list.size();c++){
                                    if (!((Boolean)((ArrayList)list.get(c)).get(1))){
                                        Dimension d =((Dimension)((ArrayList)list.get(c)).get(0));
                                        int li = (int)d.getWidth();
                                        int co = (int)d.getHeight();
                                        trace("verify if landmines["+li+"]["+co+"] is disabled");
                                        if (!landmines[li][co].isDisabled()){
                                            trace("disabling it");
                                            cellRightClick (li, co);
                                        }
                                        
                                    }    
                                }
                                trace("set cell["+linY+"]["+colX+"] as solved");
                                solvedCell[linY][colX] = true;
                            }
                            
                            //verify neighboars who are also uncovered
                            //if thisDangerLevel + uncoveredNeighbour == 7 the only covered is a bomb
                            // mark the cell as a bomb
                            //if thisDangerLevel + uncoveredNeighbour + markedBomb == 7 the covered is a safe cell to open
                            
                        }
                    } else {
                        trace("The position: ["+linY+", "+colX+"] is solved, and the AI does nothing (delete this else)" );
                    }
                }
            }
        }
        * */
        
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


    
}
