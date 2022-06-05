/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package achtung;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author marce
 */
public class IntField extends JTextField implements CaretListener {
    private String lastValidValue = "";
    private int errorCount = 0;
    private Color defColor;
    public IntField(){
        defColor = this.getForeground();
        this.addCaretListener((CaretListener) this);
    }
    public void restore(){
        this.setForeground(defColor);
    }

    public void caretUpdate(CaretEvent e) {
        trace(e);
        String newValue = this.getText();
        boolean foundErr = false;
        try {
            if (newValue.length()>0){
                int newValueToInt = Integer.parseInt(newValue);
                lastValidValue = newValue;
                restore();
            }
        } catch (Exception err){
            this.setForeground(Color.RED);
            errorCount++;
            trace("Invalid value detected, returning to previous valid value... irregularity " + errorCount);
            
        }
        
    }
    public void trace(Object o){
        System.out.println(o);
    }
}
