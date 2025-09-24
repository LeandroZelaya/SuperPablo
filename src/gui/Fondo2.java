package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import javax.swing.ImageIcon;

public class Fondo2 {

    private Image fondo;
    private int ancho, alto;  // ancho y alto del fondo
    private double desplazamiento = 0;

    // Coordenadas del marcador
    private int xMarcador = 13200; // cambiar según quieras
    private int yMarcador = 650;  // cambiar según quieras
    private int tamañoMarcador = 60; // ancho y alto del cuadrado
    
    private int xMarcadorCastillo = 14500; // cambiar según quieras
    private int yMarcadorCastillo = 650;  // cambiar según quieras
    private int tamañoMarcadorCastillo = 60; // ancho y alto del cuadrado

    public Fondo2(String rutaImagen, int ancho, int alto) {
        this.fondo = new ImageIcon(rutaImagen).getImage();
        this.ancho = ancho;
        this.alto = alto;
    }

    public void update(int desplazamientoJugador, int anchoDeseado) {
        desplazamiento += desplazamientoJugador;

        if (desplazamiento < 0) 
            desplazamiento = 0;

        int anchoVentana = 1280; // ancho de tu ventana
        if (desplazamiento > anchoDeseado - anchoVentana)
            desplazamiento = anchoDeseado - anchoVentana;
    }

    public void draw(Graphics g, int anchoDeseado, int altoDeseado) {
        // Dibujar fondo
        g.drawImage(fondo, (int)-desplazamiento, 0, anchoDeseado, altoDeseado, null);

        // Dibujar cuadrado rojo marcador
       /* g.setColor(Color.RED);
        g.fillRect(xMarcador - (int)desplazamiento, yMarcador, tamañoMarcador, tamañoMarcador);*/
        
         /*g.setColor(Color.RED);
        g.fillRect(xMarcadorCastillo - (int)desplazamiento, yMarcadorCastillo, tamañoMarcadorCastillo, tamañoMarcadorCastillo);*/
    }

    // Getters para colisión
    public int getXMarcador() {
        return xMarcador;
    }

    public int getYMarcador() {
        return yMarcador;
    }

    public int getTamañoMarcador() {
        return tamañoMarcador;
    }
    public int getXMarcadorCastillo() {
        return xMarcadorCastillo;
    }

    public int getYMarcadorCastillo() {
        return yMarcadorCastillo;
    }

    public int getTamañoMarcadorCastillo() {
        return tamañoMarcadorCastillo;
    }

    // Métodos para actualizar posición del marcador
    public void setMarcadorPos(int x, int y) {
        this.xMarcador = x;
        this.yMarcador = y;
    }

    public void setTamañoMarcador(int tamaño) {
        this.tamañoMarcador = tamaño;
    }
    
    
}
