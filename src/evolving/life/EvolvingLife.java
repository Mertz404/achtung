
package evolving.life; 

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Marcelo Augusto Mertz
 * 
 */
public class EvolvingLife extends JFrame {
    JPanel world;
    private EvolvingLife () {
        // Setup the main window
        this.setTitle("Evolving Life"); //Title window
        this.setResizable(false); //not resizable
        this.setUndecorated(true);
        this.setLayout(null);// null layout as this will not be responsive
        this.setSize(604,502); // window size
        this.getContentPane().setBackground(Color.green);
        this.setLocationRelativeTo(null);// após referenciar o tamanho, centraliza a janela
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ao fechar a janela encerra o app
        
        World world = new World();
        world.setLocation(1, 1);
        this.add(world);
        
        this.setVisible(true);
    }
    public static void main(String[] args) {
        new EvolvingLife();
    }
    
    
}
