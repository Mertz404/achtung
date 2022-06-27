/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package achtung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

/**
 *
 * @author marce
 */
public class Landmine extends JPanel{
    private int alt, lar;
    private JLabel lbl, lblDisabled;
    private JPanel cover;
    private Boolean mine = false, disabled = false;
    private Boolean covered = true;
    public Landmine(String name){
        alt = 10;
        lar = 10;
        this.setBackground(Color.GRAY);
        this.setBorder(BorderFactory.createBevelBorder(1, Color.darkGray, Color.lightGray));
        
        this.setName(name);
        
        this.setLayout(new OverlayLayout(this));
        
        lbl = new JLabel("");
        lbl.setPreferredSize(new Dimension(15,15));
        lbl.setVisible(false);
        this.add(lbl);
        
        lblDisabled = new JLabel("X");
        lblDisabled.setVisible(false);
                
        setCover();
        
    }
    
    
    
    public void setMine(){
        this.mine = true;
        this.lbl.setText("9");
    }
    
    public Boolean isMine(){
        return mine;
    }
    public void togleDisable(){
        if (covered && !disabled){
            disabled = true;
            lblDisabled.setVisible(true);
        } else if (covered && disabled){
            disabled = false;
            lblDisabled.setVisible(false);
        }
    }
    public Boolean isDisabled(){
        return disabled;
    }
    
    public void setCover(){
        cover = new JPanel();
        cover.setBackground(Color.blue);
        cover.setName("cover");
        cover.setLayout(new GridBagLayout());
        cover.add(lblDisabled);
        this.add(cover);
        covered = true;
    }
    
    public void setLabel(String name){
        lbl.setText(name);
    }
    public String getLabel(){
        return lbl.getText();
    }
    
    public Boolean isCovered(){
        return covered;
    }
    public void removeCover(){
        if (!disabled){
            removeJPanelByName(this, "cover");
            covered = false;
            this.setLayout(new GridBagLayout());
            this.setBorder(null);
        
            lbl.setVisible(true);
        }
    }
    
    public void revealLandMine(){
        removeJPanelByName(this, "cover");
            covered = false;
            this.setLayout(new GridBagLayout());
            this.setBorder(null);
        
            lbl.setVisible(true);
    }
    
    private void removeJPanelByName(Container con, String name){
        for (int i = 0; i < con.getComponentCount(); i++){
           try{
            if (con.getComponent(i).getName().equals(name)){             
                con.remove(i);
                con.repaint();
            }
            } catch (Exception er){
                
            }
        }
    }
    public void setBkg(){
        cover.setBackground(Color.red);
    }
}
