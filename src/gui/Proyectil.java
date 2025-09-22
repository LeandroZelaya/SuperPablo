package gui;

import java.awt.*;

public class Proyectil {
    private int x, y;
    private int ancho = 20, alto = 20;
    private int velocidad = 5;
    private boolean activo = true;

    public Proyectil(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x -= velocidad; // se mueve hacia la izquierda (hacia el jugador)
        if (x < 0) activo = false;
    }

    public void draw(Graphics g, int camaraX) {
        if (!activo) return;
        g.setColor(Color.RED); // bola de fuego roja
        g.fillOval(x - camaraX, y, ancho, alto);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public boolean isActivo() {
        return activo;
    }

    public void destruir() {
        activo = false;
    }
}
