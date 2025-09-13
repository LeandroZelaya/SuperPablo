package gui;

import java.awt.Graphics;
import java.awt.Image;

public class FondoParallax {
    private Image imagen;
    private int x;
    private int velocidad;
    private int ancho;
    private int alto;

    public FondoParallax(Image imagen, int ancho, int alto, int velocidad) {
        this.imagen = imagen;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
        this.x = 0;
    }

    public void moverDerecha() {
        x += velocidad;
        if (x >= ancho) x = 0;
    }

    public void moverIzquierda() {
        x -= velocidad;
        if (x <= -ancho) x = 0;
    }

    public void dibujar(Graphics g, int y) {
        int x1 = -x;
        int x2 = x1 + ancho;
        g.drawImage(imagen, x1, y, ancho, alto, null);
        g.drawImage(imagen, x2, y, ancho, alto, null);
    }

    public int getX() { return x; }
}
