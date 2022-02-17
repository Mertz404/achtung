package evolving.life;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Clock;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Marcelo Augusto Mertz
 */
public class WorldCell extends JPanel{
    Color chosenOne;
    int posX, posY;
    private int sqSize;
    private TerrainType tt;
    
    public WorldCell( int posX, int posY, int sqSize){
        this.posX = posX;
        this.posY = posY;
        this.sqSize = sqSize;
        this.tt = new TerrainType(0);
        this.setBackground(tt.getBkgColor());
        this.setLayout(null);
        this.setSize(sqSize, sqSize);
    }
    /**
     * 
     * @param terrainIndex possible values:
     * 0 for deep-water
     * 1 for water
     * 2 for sand
     * 3 for dirt
     * 4 for stone
     */
    public void setTerrainType(int terrainIndex){
        tt.changeTerrain(terrainIndex);
        redrawTerrain();
    }
    public int getTerrainType(){
        return tt.getId();
    }
    public void redrawTerrain(){
        this.setBackground(tt.getBkgColor());
    }
    /** change the cell size 
    * @param sqSize int value for square size of this cell
    **/
    public void setSqSize (int sqSize){
        this.sqSize = sqSize;
        this.setSize(sqSize, sqSize);
    }
    /** get the current cell size
     * @return the sqSize of the cell
    **/
    public int getSqSize(){
        return sqSize;
    }
    /** Enlarge the cell by 2 pixel both in widht and height
    **/
    public void enlargeCell(){
        sqSize+=2;
        setSqSize(sqSize);
        Point loc = this.getLocation();
        loc.x = loc.x-1; loc.y = loc.y-1;
        this.setLocation(loc);
    }
    /** Schrink the cell by 1 pixel both in widht and height
    **/
    public void schrinkCell(){
        sqSize-=2;
        setSqSize(sqSize);
        Point loc = this.getLocation();
        loc.x = loc.x+1; loc.y = loc.y+1;
        this.setLocation(loc);
    }
    
    
}
