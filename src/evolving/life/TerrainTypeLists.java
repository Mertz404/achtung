
package evolving.life;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author marce
 */
public class TerrainTypeLists {
    public static final List<String> TERRAIN_TYPE_NAME_LIST = new ArrayList<String>() {{
        add("DEEP_WATER");
        add("SHALLOW_WATER");
        add("SAND");
        add("DIRT");
        add("STONE");
    }};
        public static final List<Color> TERRAIN_TYPE_COLOR_LIST = new ArrayList<Color>() {{
        add(Color.BLUE);
        add(Color.CYAN);
        add(Color.YELLOW);
        add(new Color(150, 75, 0));
        add(Color.GRAY);
    }};
    
}
