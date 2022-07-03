/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package achtung;

import java.util.*;

/**
 *
 * @author marce
 */
public class Neuron {
    private Double value = 0.0;
    private Double weight = 1.0;
    private ArrayList ancValue = new ArrayList();
    
    public Neuron (){
        
    }
    public Neuron (Double val){
        this.value = val;
        
    }
    public Neuron (Double val, Double weight){
        this.value = val;
        this.weight = weight;
    }
    
    public Double value (){
        return this.value;
    }
    public Double value (Double val){
        this.value = val;
        return this.value;
    }
    
    public void addAncestor(Double n){
        ancValue.add(n);
    }
    public void setAncestor(int index, Double n){
        try {
            ancValue.set(index, n);
        } catch (Exception exc){
            ancValue.add(n);
        }
    }
    public Double getAncestor(int index){
        return (Double) ancValue.get(index);
    }
    public int getAncestorCount(){
        return ancValue.size();
    }
    
}
