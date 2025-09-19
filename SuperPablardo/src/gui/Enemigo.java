package gui;

import java.awt.*;

public class Enemigo {
    private int x, y;
    private int ancho = 50, alto = 80;
    private int velocidad = 2;
    private int limiteIzq, limiteDer;
    private int direccion = 1;

    public Enemigo(int x, int y, int limiteIzq, int limiteDer) {
        this.x = x;
        this.y = y;
        this.limiteIzq = limiteIzq;
        this.limiteDer = limiteDer;
    }

    public void update() {
        x += direccion * velocidad;
        if (x <= limiteIzq || x >= limiteDer) direccion *= -1;
    }

    public void draw(Graphics g, int camaraX) {
        g.setColor(Color.RED);
        g.fillRect(x - camaraX, y, ancho, alto);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }
}
