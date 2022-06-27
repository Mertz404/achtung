/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package achtung;

/**
 *
 * @author marce
 */
public class inNeuron {
    //Define default values for Value and Weight
    private Double value = 0.0;
    private Double weight = 1.0;
    
    /**
     * Create a Input Neuron class with default values for 
     * Value as 0 and Weight as 1.
     */
    public inNeuron(){        
    }
    /**
     * Create a Input Neuron class with a defined value<br>
     * @param value - define the value of the inNeuron
     */
    public inNeuron(Double value){
        this.value = value;
    }
    /**
     * Create a Input Neuron class with a defined value and weight<br>
     * @param value - define the value of the inNeuron
     * @param weight - define the weight of the inNeuron
     */
    public inNeuron(Double value, Double weight){
        this.value = value;
        this.weight = weight;
    }
    
    
    public Double getValue(){
        return this.value;
    }
    public void setValue(Double value){
        this.value = value;
    }
    
    public Double getWeight(){
        return this.weight;
    }
    public void setWeight(Double weight){
        this.weight = weight;
    }
    
}
