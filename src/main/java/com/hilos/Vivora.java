package com.hilos;

import java.util.Random;

class Vivora implements Runnable {
    Nodo cabeza;
    String direccion;
    boolean controladaPorUsuario;
    boolean enMovimiento = true;
    private int velocidad = 200;
    public static final int NODO_TAMANO = 30;
    private Random random = new Random();

    public Vivora(int startX, int startY, boolean controladaPorUsuario) {
        cabeza = new Nodo(startX, startY);
        this.controladaPorUsuario = controladaPorUsuario;
        
        this.direccion = generarDireccionInicial(controladaPorUsuario);
    }

    private String generarDireccionInicial(boolean controladaPorUsuario) {
        if (controladaPorUsuario) {
            return "derecha"; 
        }
        return obtenerDireccionAleatoria();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String nuevaDireccion) {

        if (nuevaDireccion == null) {
            return;
        }

        if (controladaPorUsuario) {
            if (!esMovimientoOpuesto(direccion, nuevaDireccion)) {
                direccion = nuevaDireccion;
            }
        } else {
            direccion = obtenerDireccionAleatoria();
        }
    }

    private String obtenerDireccionAleatoria() {
        String[] direcciones = {"arriba", "abajo", "izquierda", "derecha"};
        return direcciones[random.nextInt(direcciones.length)];
    }
    
    private boolean esMovimientoOpuesto(String dir1, String dir2) {
        if (dir1 == null || dir2 == null) {
            return false;
        }
        
        return (dir1.equals("arriba") && dir2.equals("abajo")) ||
               (dir1.equals("abajo") && dir2.equals("arriba")) ||
               (dir1.equals("izquierda") && dir2.equals("derecha")) ||
               (dir1.equals("derecha") && dir2.equals("izquierda"));
    }

    public void agregarNodo() {
        Nodo cola = cabeza.getCola();
        
        int nuevoX = cola.x;
        int nuevoY = cola.y;
        
        switch (direccion) {
            case "derecha":
                nuevoX -= NODO_TAMANO;
                break;
            case "izquierda":
                nuevoX += NODO_TAMANO;
                break;
            case "arriba":
                nuevoY += NODO_TAMANO;
                break;
            case "abajo":
                nuevoY -= NODO_TAMANO;
                break;
        }
        
        Nodo nuevoNodo = new Nodo(nuevoX, nuevoY);
        cola.siguienteNodo = nuevoNodo;
        
        velocidad = Math.max(50, velocidad - 10);
    }

    public void mover() {
        if (!enMovimiento) return;

        int nuevoX = cabeza.x;
        int nuevoY = cabeza.y;

        switch (direccion) {
            case "derecha":
                nuevoX += NODO_TAMANO;
                break;
            case "izquierda":
                nuevoX -= NODO_TAMANO;
                break;
            case "arriba":
                nuevoY -= NODO_TAMANO;
                break;
            case "abajo":
                nuevoY += NODO_TAMANO;
                break;
        }

        cabeza.mover(nuevoX, nuevoY);
    }

    public boolean colisionaConComida(int comidaX, int comidaY) {
        return Math.abs(cabeza.x - comidaX) < NODO_TAMANO &&
               Math.abs(cabeza.y - comidaY) < NODO_TAMANO;
    }

    public boolean colisionaConBorde(int width, int height) {
        return cabeza.x < 0 || 
               cabeza.x + NODO_TAMANO > width || 
               cabeza.y < 0 || 
               cabeza.y + NODO_TAMANO > height;
    }

    public boolean colisionaConCuerpo() {
        Nodo actual = cabeza.siguienteNodo;
        while (actual != null) {
            if (cabeza.x == actual.x && cabeza.y == actual.y) {
                return true;
            }
            actual = actual.siguienteNodo;
        }
        return false;
    }

    public boolean colisionaConOtraVivora(Vivora otraVivora) {
        Nodo cabezaActual = this.cabeza;
        
        Nodo nodoOtraVivora = otraVivora.cabeza.siguienteNodo; 
        while (nodoOtraVivora != null) {
            if (cabezaActual.x == nodoOtraVivora.x && cabezaActual.y == nodoOtraVivora.y) {
                return true;
            }
            nodoOtraVivora = nodoOtraVivora.siguienteNodo;
        }
        
        return false;
    }

    @Override
    public void run() {
        while (enMovimiento) {
            mover();
            
            if (!controladaPorUsuario) {
                if (random.nextInt(10) == 0) { 
                    setDireccion(obtenerDireccionAleatoria());
                }
            }
            
            try {
                Thread.sleep(velocidad);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break; 
            }
        }
    }


    public void detener() {
        enMovimiento = false;
    }
}