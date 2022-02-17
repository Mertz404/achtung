
package evolving.life;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Marcelo Augusto Mertz
 */
public class World extends JLayeredPane implements MouseListener {
    int row = 150;
    int column = 150;
    int cellSqSize = 5;
    WorldCell[][] wc = new WorldCell[column][row];
    int totalCell;
    int landmass, waterLevel, totalWater;
    
    int failedToPlotLand;
        
    public World(){
        totalCell = row*column;
        totalWater=row*column;
        waterLevel = 70;
        landmass = totalCell - (totalCell*waterLevel/100);

        this.setLayout(null);
        this.setBackground(Color.red);
        this.setSize(500, 500);
        for (int x = 0; x < column; x++){
            for (int y = 0; y < row; y++){
                this.wc[x][y] = new WorldCell(x, y, cellSqSize);
                this.wc[x][y].setLocation(x*cellSqSize, y*cellSqSize);
                this.wc[x][y].setName("wc"+addZero(x)+addZero(y));
                this.wc[x][y].addMouseListener(this);
                this.add(wc[x][y]);
            }
        }
        try {
            createLandMass();
        } catch (Exception e) {
        }
        
    }
    private void createLandMass (){
        int startingX = (int) (Math.random() * column);
        int startingY = (int) (Math.random() * row);
        expandLandMass(startingX, startingY);
    }
    private void expandLandMass(int x, int y){
        //adjust values
        if (x < 0){ x = column-1;}
        if (x > column-1){ x = 0;}
        if (y < 0){ y = row-1;}
        if (y > row-1){ y = 0;}
        //is enought of landmass
        if (landmass > 0){
            //verify if current cell is water, if so, fill it with land
            if (wc[x][y].getTerrainType() == 0){
                wc[x][y].setTerrainType(3);
                landmass--;
                System.out.println("Current position: x:"+x+", "+y+". Landmass remaining to plot "+landmass);
            } else {
                failedToPlotLand++;
            }
            switchDirection(x, y);
        } else {
            System.out.println("Total Land archieved - " + (totalCell - (totalCell*waterLevel/100))+ ". Failed to plot " +failedToPlotLand+" times.");
        }
    }
    private void switchDirection(int x, int y){
            //randomize next piece of land
            int direction = (int)(Math.random()*4);
            switch (direction){
                case 0: expandLandMass(x-1, y);
                    break;
                case 1: expandLandMass(x, y-1);
                    break;
                case 2: expandLandMass(x+1, y);
                    break;
                case 3: expandLandMass(x, y+1);
                    break;
            }
    }
    public String addZero(int val){
        if (val < 10){
            return "0"+val;
        } else {
            return ""+val;
        }
    }
    private Object getComponentByName (String name) throws Exception{
        int compCount = this.getComponentCount();
        for (int cont = 0; cont < compCount; cont++){
            if (this.getComponent(cont).getName().equals(name)){
                return this.getComponent(cont);
            }
        }
        throw new Exception("Object name not found");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent()instanceof WorldCell){
            WorldCell cell = (WorldCell) e.getComponent();
            System.out.println(cell.posX + ", "+ cell.posY);
        } else {
            System.out.println(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getComponent()instanceof WorldCell){
            WorldCell cell = (WorldCell) e.getComponent();
            this.setComponentZOrder(cell, 0);
            cell.enlargeCell();
            cell.setBorder(BorderFactory.createLineBorder(Color.red));
        } else {
            System.out.println(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getComponent()instanceof WorldCell){
            WorldCell cell = (WorldCell) e.getComponent();
            cell.schrinkCell();
            cell.setBorder(null);
        } else {
            System.out.println(e);
        }    
    }
    
}
