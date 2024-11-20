package com.hilos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List; 

public class Juego extends JPanel {
    private ArrayList<Vivora> vivoras;
    private Comida comida;
    private Vivora vivoraActiva;
    private JPanel listaVivoras; 
    private int vivorasCreadas = 0;
    private JPanel panelJuego;

    public static final int ANCHO_PANEL = 800;
    public static final int ALTO_PANEL = 600;

    public Juego() {
        setLayout(new BorderLayout());
        vivoras = new ArrayList<>();
        comida = new Comida(200, 200);

        panelJuego = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (Vivora v : vivoras) {
                    g.setColor(v == vivoraActiva ? Color.BLACK : Color.blue);
                    Nodo actual = v.cabeza;
                    while (actual != null) {
                        g.fillOval(actual.x, actual.y, Vivora.NODO_TAMANO, Vivora.NODO_TAMANO);
                        actual = actual.siguienteNodo;
                    }
                }

                comida.paint(g);
            }
        };
        
        panelJuego.setPreferredSize(new Dimension(ANCHO_PANEL, ALTO_PANEL));


        JPanel panelDerecho = new JPanel(new BorderLayout());
        listaVivoras = new JPanel();
        listaVivoras.setLayout(new BoxLayout(listaVivoras, BoxLayout.Y_AXIS));

        JButton btnAgregarVivora = new JButton("Agregar Víbora");
        btnAgregarVivora.addActionListener(e -> agregarVivora());

        panelDerecho.add(new JScrollPane(listaVivoras), BorderLayout.CENTER);
        panelDerecho.add(btnAgregarVivora, BorderLayout.SOUTH);

        add(panelJuego, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);


        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (vivoraActiva != null) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            vivoraActiva.setDireccion("arriba");
                            break;
                        case KeyEvent.VK_DOWN:
                            vivoraActiva.setDireccion("abajo");
                            break;
                        case KeyEvent.VK_LEFT:
                            vivoraActiva.setDireccion("izquierda");
                            break;
                        case KeyEvent.VK_RIGHT:
                            vivoraActiva.setDireccion("derecha");
                            break;
                    }
                }
            }
        });


        iniciarBucleDeJuego();
    }

    private void iniciarBucleDeJuego() {
        new Thread(() -> {
            while (true) {
                try {
                    List<Vivora> vivorasAEliminar = new ArrayList<>();
                    
                    for (Vivora v : vivoras) {
                        
                        if (v.colisionaConBorde(panelJuego.getWidth(), panelJuego.getHeight())) {
                            vivorasAEliminar.add(v);
                            continue;
                        }
    
                        if (v.colisionaConCuerpo()) {
                            vivorasAEliminar.add(v);
                            continue;
                        }
    
                        for (Vivora otraVivora : vivoras) {
                            if (otraVivora != v && v.colisionaConOtraVivora(otraVivora)) {
                                vivorasAEliminar.add(v);
                                break;
                            }
                        }
    
                        if (v.colisionaConComida(comida.getX(), comida.getY())) {
                            v.agregarNodo();
                            comida.generarNuevaPosicion(
                                panelJuego.getWidth(), 
                                panelJuego.getHeight(), 
                                vivoras
                            );
                        }
                    }
    
                    for (Vivora v : vivorasAEliminar) {
                        eliminarVivora(v);
                    }
    
                    if (vivoraActiva == null && !vivoras.isEmpty()) {
                        vivoraActiva = vivoras.get(vivoras.size() - 1);
                        vivoraActiva.controladaPorUsuario = true;
                    }
    
                    SwingUtilities.invokeLater(this::repaint);
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void eliminarVivora(Vivora vivora) {
        vivora.detener();
        
        vivoras.remove(vivora);
        
        actualizarListaVivoras();
        
        if (vivora == vivoraActiva) {
            if (!vivoras.isEmpty()) {
                vivoraActiva = vivoras.get(vivoras.size() - 1);
                vivoraActiva.controladaPorUsuario = true;
            } else {
                vivoraActiva = null;
            }
            
            actualizarListaVivoras();
        }
    }

    private void actualizarListaVivoras() {

        listaVivoras.removeAll();
        
        for (int i = 0; i < vivoras.size(); i++) {
            Vivora vivora = vivoras.get(i);
            JButton botonVivora = new JButton("Víbora " + (i + 1));
            
            if (vivora == vivoraActiva) {
                botonVivora.setBackground(Color.lightGray);
            }
            
            final int index = i;
            botonVivora.addActionListener(e -> {
                if (vivoraActiva != null) {
                    vivoraActiva.controladaPorUsuario = false;
                }
                
                vivoraActiva = vivoras.get(index);
                vivoraActiva.controladaPorUsuario = true;
                
                actualizarListaVivoras();
                
                requestFocusInWindow();
            });
            
            listaVivoras.add(botonVivora);
        }
        
        listaVivoras.revalidate();
        listaVivoras.repaint();
    }

    private void agregarVivora() {
        int x = (int) (Math.random() * (panelJuego.getWidth() - 200)) + 100;
        int y = (int) (Math.random() * (panelJuego.getHeight() - 200)) + 100;
    
        for (Vivora v : vivoras) {
            v.controladaPorUsuario = false;
        }
    
        Vivora nuevaVivora = new Vivora(x, y, vivoras.isEmpty());
        vivoras.add(nuevaVivora);
        vivorasCreadas++;
    
        vivoraActiva = nuevaVivora;
        nuevaVivora.controladaPorUsuario = true;
    
        new Thread(nuevaVivora).start();
        
        actualizarListaVivoras();
        
        requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Juego de la Víbora");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Juego juego = new Juego();
            frame.add(juego);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}