package gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Fondo3 {

    private Image fondo;
    private int ancho, alto;
    private int camaraX = 0; // It's better to store camaraX here

    public Fondo3(String rutaImagen, int ancho, int alto) {
        this.fondo = new ImageIcon(rutaImagen).getImage();
        this.ancho = ancho;
        this.alto = alto;
    }

    // This is the updated method that SuperPablo.java expects.
    public int update(int desplazamientoJugador, int camaraX, int anchoVentana) {
        int nuevaCamaraX = camaraX + desplazamientoJugador;

        // Clamp the camera position to prevent it from going out of bounds
        if (nuevaCamaraX < 0) {
            nuevaCamaraX = 0;
        }

        if (nuevaCamaraX > this.ancho - anchoVentana) {
            nuevaCamaraX = this.ancho - anchoVentana;
        }

        return nuevaCamaraX;
    }

    public void draw(Graphics g, int camaraX) {
        g.drawImage(fondo, -camaraX, 0, this.ancho, this.alto, null);
    }
}
