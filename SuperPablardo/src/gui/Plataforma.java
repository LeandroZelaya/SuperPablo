package gui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Plataforma {
    private int x, y;
    private int ancho = 120, alto = 30;
    private Image imagen;
    private boolean movil = false;
    private int direccion = 1, velocidad = 1;
    private int limiteIzq, limiteDer;

    public Plataforma(int x, int y) {
        this.x = x;
        this.y = y;
        imagen = new ImageIcon("src/media/pasto.png").getImage();
    }

    public Plataforma(int x, int y, int limiteIzq, int limiteDer) {
        this(x, y);
        movil = true;
        this.limiteIzq = limiteIzq;
        this.limiteDer = limiteDer;
    }

    public void update() {
        if (movil) {
            x += direccion * velocidad;
            if (x <= limiteIzq || x >= limiteDer) direccion *= -1;
        }
    }

    public void draw(Graphics g, int camaraX) {
        g.drawImage(imagen, x - camaraX, y, ancho, alto, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
