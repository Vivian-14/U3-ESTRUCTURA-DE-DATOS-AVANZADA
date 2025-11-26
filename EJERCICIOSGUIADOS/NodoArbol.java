/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ArbolesBinarioEjG;

/**
 * EJERCICIO GUIADO IMPLEMENTACION DE UN ARBOL BINARIO
 * @author Alondra Vianney Hernandez Torres // GTID141 // 25 DE NOVIEMBRE DEL 2025
 * Nodo genérico para el árbol binario.
 * Los punteros a hijos se dejan públicos para facilitar la recursión 
 */
public class NodoArbol<T> {

    // El dato del nodo es privado para respetar el principio de encapsulamiento.
    // Esto evita que cualquier clase externa lo modifique directamente.
    private T dato;

    // Los hijos son públicos porque durante la recursión es más práctico 
    // acceder a ellos sin necesidad de getters/setters.
    public NodoArbol<T> hijoIzquierdo;
    public NodoArbol<T> hijoDerecho;

    public NodoArbol(T dato) {
        this.dato = dato;

        // Los hijos inician como null porque aún no tienen subárboles conectados.
        this.hijoIzquierdo = null;
        this.hijoDerecho = null;
    }

    public T getDato() {
        // Getter para devolver el valor almacenado en el nodo.
        // Se usa porque 'dato' es privado y no puede ser leído directamente desde fuera.
        return dato;
    }

    public void setDato(T dato) {
        // Setter para permitir cambiar el dato del nodo si se necesitara.
        // Esto mantiene el control sobre quién modifica el valor.
        this.dato = dato;
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
