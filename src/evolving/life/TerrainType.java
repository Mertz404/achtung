
package evolving.life;

import java.awt.Color;
import java.util.ArrayList;
import org.w3c.dom.NameList;

/**
 *
 * @author marce
 *
 */
public class TerrainType {
    /**
     * Default terrain is:
     * name: DEEP_WATER;
     * id 0
     */
    private String name = TerrainTypeLists.TERRAIN_TYPE_NAME_LIST.get(0);
    private int id = 0;
    private String thisTypeString;
    private Color bkg;
    
    public TerrainType (int id){
        changeTerrain(id);
    }
    public TerrainType (String name){
        changeTerrain(name);
    }    
    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
    public Color getBkgColor (){
        return bkg;
    }
    public void changeTerrain(int id){
        this.id = id;
        this.name = TerrainTypeLists.TERRAIN_TYPE_NAME_LIST.get(id);
        this.bkg = TerrainTypeLists.TERRAIN_TYPE_COLOR_LIST.get(id);
    }
    public void changeTerrain(String name){
        this.name = "name";
        this.id = TerrainTypeLists.TERRAIN_TYPE_NAME_LIST.indexOf(name);
        this.bkg = TerrainTypeLists.TERRAIN_TYPE_COLOR_LIST.get(id);
    }

}
