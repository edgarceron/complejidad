/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author Edgar Mauricion Ceron
 */
public class InfoParcelas {
    
    /*
    * Numero de parcelas a ingresar
    */
    int numParcelas;
    
    /*
    * Indica el tiempo de duración de la cosecha en cada una de las parcelas
    * Considere que duracionCosecha[0] corresponde a la parcela 1, 
    * duracionCosecha[1] corresponde a la parcela 2 y así sucesivamente.
    */
    int[] duracionCosecha;
    
    /*
    * Corresponde a un entero positivo B que corresponde a la suma de la 
    * duración de cosecha de todas las parselas, debe ser igual a la suma de 
    * todos los elementos de duracionCosecha.
    */
    int sumaDuracion;
    
    /*
    * Indicando la utilidad esperada en un tiempo de cosecha dado para una
    * parcela dada, representada por un numero entero. Por ejemplo:
    * utilidadEsperada[2][5] Indica la utilidad para la parcela 3 en el 
    * tiempo 6.
    */
    int[][] utilidadEsperada; 
    int numVariables;
    int numRestricciones;
    
    public final int VARIABLE_MAXIMIZAR = 1;
    public final int VARIABLES_BASE;
    public final int VARIABLES_HOLGURA;
    public final int RESULTADO;

    public InfoParcelas(int numParcelas, int[] duracionCosecha, int sumaDuracion, int[][] utilidadEsperada) {
        this.numParcelas = numParcelas;
        this.duracionCosecha = duracionCosecha;
        this.sumaDuracion = sumaDuracion;
        this.utilidadEsperada = utilidadEsperada;
        this.VARIABLES_BASE = this.numParcelas * this.sumaDuracion + VARIABLE_MAXIMIZAR;
        /**
         * Se incluye este numero de variables de holgura debido a las
         * restricciones temporales manejan una igualación <= 
         */
        this.VARIABLES_HOLGURA = this.sumaDuracion * 2;

        this.numVariables = this.VARIABLES_BASE + this.VARIABLES_HOLGURA;
        this.numRestricciones = 1 + 2 * numParcelas + sumaDuracion; 
        this.RESULTADO = this.numVariables - 1;
    }

    public int getNumParcelas() {
        return numParcelas;
    }

    public int[] getDuracionCosecha() {
        return duracionCosecha;
    }

    public int getSumaDuracion() {
        return sumaDuracion;
    }

    public int[][] getUtilidadEsperada() {
        return utilidadEsperada;
    }
    
    public float[][] tablaSimplex(){
        
        int numRestricciones = this.numParcelas + this.sumaDuracion * 2;
        float[][] tablaSimplex = new float[numVariables][numRestricciones + 1];
        tablaSimplex[0][0] = 1;
        int auxPosHolgura = this.VARIABLES_BASE - 1;
        int fila = 0;
        ArrayList<Variable> funcionObjetivo = this.funcionObjetivo();
        
        for (int i = 0; i < funcionObjetivo.size(); i++) {
            Variable get = funcionObjetivo.get(i);
            System.out.println("Var"+i+" i:" + get.getPosI() + ", j: " + get.getPosJ() + ", Factor: " + get.getFactor());
            tablaSimplex[get.getPosTabla(this.numParcelas)][0] = get.getFactor();
        }
        
        fila = 1;
        for (int i = fila; i < this.numParcelas + fila; i++) {
            for (Variable variable : this.restriccionCosecha(i - fila)) {
                tablaSimplex[variable.getPosTabla(this.numParcelas)][i] = variable.getFactor();
            }
            tablaSimplex[RESULTADO][i] = 1;
        }
        fila = this.numParcelas + fila;
        for (int i = fila; i < this.sumaDuracion + fila; i++) {
            for (Variable variable : restriccionTiempos(i - fila)) {
                tablaSimplex[variable.getPosTabla(this.numParcelas)][i] = variable.getFactor();
            }
            tablaSimplex[RESULTADO][i] = 1;
            tablaSimplex[auxPosHolgura][i] = 1;
            auxPosHolgura++; 
        }
        fila = this.sumaDuracion + fila;
        for (int i = fila; i < fila +  this.numParcelas; i++) {
            for (Variable variable : restriccionSeguidas(i - fila)) {
                tablaSimplex[variable.getPosTabla(this.numParcelas)][i] = variable.getFactor();
            }
            tablaSimplex[RESULTADO][i] = this.sumaDuracion;
            tablaSimplex[auxPosHolgura][i] = 1;
            auxPosHolgura++; 
        }
        return tablaSimplex;
    }
    
    public ArrayList<Variable> funcionObjetivo(){
        ArrayList<Variable> funcionObjetivo = new ArrayList<>();
        for (int i = 0; i < this.numParcelas; i++) {
            for (int j = 0; j < this.sumaDuracion; j++) {
                funcionObjetivo.add(new Variable(i, j, utilidadEsperada[i][j] * -1));
            }
        }
        return funcionObjetivo;
    }
    
    public ArrayList<Variable> restriccionCosecha(int n){
        ArrayList<Variable> restriccionCosecha = new ArrayList<>();
        for (int i = 0; i < this.sumaDuracion; i++) {
            restriccionCosecha.add(new Variable(n, i, 1));
        }
        return restriccionCosecha;
    }
    
    public ArrayList<Variable> restriccionTiempos(int t){
        ArrayList<Variable> restriccionTiempos = new ArrayList<>();
        for (int i = 0; i < this.numParcelas; i++) {
            for (int j = t; j < t + this.getDuracionCosecha()[i]; j++) {
                int k = existeVariable(restriccionTiempos, i, j);
                if( k >= 0){
                    restriccionTiempos.get(k).setFactor(restriccionTiempos.get(k).getFactor()+ 1);
                }
                else{
                    restriccionTiempos.add(new Variable(i, j, 1));
                }
            }
        }
        return restriccionTiempos;
    }
    
    public ArrayList<Variable> restriccionSeguidas(int n){
        ArrayList<Variable> restriccionSeguidas = new ArrayList<>();
        for (int i = 0; i < this.sumaDuracion; i++) {
            restriccionSeguidas.add(new Variable(n, i, (duracionCosecha[n] + i - 1)));
        }
        return restriccionSeguidas;
    }
    
    public int existeVariable(ArrayList<Variable> lv, int i, int j){
        for (int k = 0; k < lv.size(); k++) {
            Variable get = lv.get(k);
            if(get.getPosI() == i && get.getPosJ() == j){
                return k;
            }
        }
        return -1;
    }

    public int getNumVariables() {
        return numVariables;
    }

    public int getNumRestricciones() {
        return numRestricciones;
    }
}
