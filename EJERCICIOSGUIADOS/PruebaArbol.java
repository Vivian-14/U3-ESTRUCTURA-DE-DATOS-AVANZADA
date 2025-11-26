/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ArbolesBinarioEjG;

/**
 * EJERCICIO GUIADO IMPLEMENTACION DE UN ARBOL BINARIO
 * @author Alondra Vianney Hernandez Torres // GTID141 // 25 DE NOVIEMBRE DEL 2025
*/

public class PruebaArbol {
    public static void main(String[] args) {

        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        arbol.insertar(50);
        arbol.insertar(30);
        arbol.insertar(70);
        arbol.insertar(20);
        arbol.insertar(40);
        arbol.recorrerInorden();

        ArbolBinario<String> arbolStr = new ArbolBinario<>();
        arbolStr.insertar("mango");
        arbolStr.insertar("manzana");
        arbolStr.insertar("pera");
        arbolStr.recorrerInorden();
    }
}
