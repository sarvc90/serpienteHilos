package com.hilos;

class Nodo {
    int x, y;
    Nodo siguienteNodo;

    public Nodo(int x, int y) {
        this.x = x;
        this.y = y;
        this.siguienteNodo = null;
    }

    public Nodo getCola() {
        Nodo actual = this;
        while (actual.siguienteNodo != null) {
            actual = actual.siguienteNodo;
        }
        return actual;
    }

    public void mover(int nuevoX, int nuevoY) {
        int anteriorX = x;
        int anteriorY = y;
        
        x = nuevoX;
        y = nuevoY;
        Nodo actual = siguienteNodo;
        while (actual != null) {
            int tempX = actual.x;
            int tempY = actual.y;
            
            actual.x = anteriorX;
            actual.y = anteriorY;
            
            anteriorX = tempX;
            anteriorY = tempY;
            
            actual = actual.siguienteNodo;
        }
    }
}