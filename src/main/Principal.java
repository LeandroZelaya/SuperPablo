package main;

import javax.swing.JFrame;
import gui.SuperPablo;

public class Principal {
    public static void main(String[] args) {
        JFrame ventana = new JFrame("Super Pablo");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1280, 720);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);

        SuperPablo juego = new SuperPablo();
        ventana.add(juego);

        ventana.setVisible(true);
        juego.startGameLoop();
    }
}