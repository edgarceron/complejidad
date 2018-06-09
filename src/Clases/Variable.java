/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

/**
 *
 * @author trifo
 */
public class Variable {
    int posI;
    int posJ;
    int factor;

    public Variable(int posI, int posJ, int factor) {
        this.posI = posI;
        this.posJ = posJ;
        this.factor = factor;
    }

    public int getPosI() {
        return posI;
    }

    public int getPosJ() {
        return posJ;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }
    
    public int getPosTabla(int numParcelas){
        return (posI * numParcelas) + posJ + 1;
    }
}
