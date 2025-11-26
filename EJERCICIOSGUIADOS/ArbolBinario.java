/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ArbolesBinarioEjG;

/**
 * EJERCICIO GUIADO IMPLEMENTACION DE UN ARBOL BINARIO 
 * @author Alondra Vianney Hernandez Torres // GTID141 // 25 DE NOVIEMBRE DEL 2025
 * Árbol Binario de Búsqueda genérico.
 * T debe ser Comparable para poder comparar elementos.
 */
public class ArbolBinario<T extends Comparable<T>> {

    // La raíz del árbol siempre es privada para aplicar encapsulamiento.
    // Desde aquí comienzan todas las operaciones del BST.
    private NodoArbol<T> raiz;

    public ArbolBinario() {
        // Al iniciar el árbol, todavía no hay nodos, por eso la raíz es null.
        this.raiz = null;
    }

    public void insertar(T valor) {
        // Método público que recibe el valor que quiero agregar al árbol.
        // Llama al método recursivo para buscar la posición correcta.
        this.raiz = insertarRecursivo(this.raiz, valor);
    }

    private NodoArbol<T> insertarRecursivo(NodoArbol<T> actual, T valor) {
        // Si el nodo actual es null significa que encontré el lugar donde debe ir el nuevo valor.
        if (actual == null) {
            // Creo un nuevo nodo con ese valor.
            return new NodoArbol<>(valor);
        }

        // Uso compareTo para comparar valores porque T es genérico pero comparable.
        int cmp = valor.compareTo(actual.getDato());

        // Si el valor es menor, debe ir hacia el lado izquierdo del árbol.
        if (cmp < 0) {
            actual.hijoIzquierdo = insertarRecursivo(actual.hijoIzquierdo, valor);
        } 
        // Si el valor es mayor, va hacia el lado derecho del árbol.
        else if (cmp > 0) {
            actual.hijoDerecho = insertarRecursivo(actual.hijoDerecho, valor);
        }
        // Si es igual, no hago nada porque este árbol no permite duplicados.

        // Regreso el nodo actual para reconstruir el árbol correctamente mientras vuelve la recursión.
        return actual;
    }

    public void recorrerInorden() {
        // Llamo al método recursivo que imprime los valores en orden ascendente.
        recorrerInordenRecursivo(raiz);
        // Salto de línea al final para que la salida se vea ordenada.
        System.out.println();
    }

    private void recorrerInordenRecursivo(NodoArbol<T> nodo) {
        // El recorrido inorden siempre es: izquierda → raíz → derecha.
        if (nodo != null) {
            // Primero recorro todo el subárbol izquierdo.
            recorrerInordenRecursivo(nodo.hijoIzquierdo);
            // Luego imprimo el valor del nodo actual.
            System.out.print(nodo.getDato() + " ");
            // Al final recorro todo el subárbol derecho.
            recorrerInordenRecursivo(nodo.hijoDerecho);
        }
    }
}
