package com.hilos;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Comida {
    private int x;
    private int y;

    public Comida(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void paint(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, Vivora.NODO_TAMANO, Vivora.NODO_TAMANO); 
    }

    public void generarNuevaPosicion(int width, int height, ArrayList<Vivora> vivoras) {
        int margin = 10; 
        int newX, newY;
        boolean posicionValida;
        
        do {
            posicionValida = true;

            newX = (int) (Math.random() * (width - 2 * margin)) + margin;
            newY = (int) (Math.random() * (height - 2 * margin)) + margin;
            
            for (Vivora vivora : vivoras) {
                Nodo nodo = vivora.cabeza;
                while (nodo != null) {
                    int distancia = (int) Math.sqrt(
                        Math.pow(nodo.x - newX, 2) + 
                        Math.pow(nodo.y - newY, 2)
                    );
                    
                    if (distancia < Vivora.NODO_TAMANO * 2) {
                        posicionValida = false;
                        break;
                    }
                    nodo = nodo.siguienteNodo;
                }
                
                if (!posicionValida) break;
            }
        } while (!posicionValida);
    
        this.x = newX;
        this.y = newY;
    }

}