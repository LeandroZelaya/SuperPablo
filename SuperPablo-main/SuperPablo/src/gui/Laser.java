package gui;

import java.awt.*;

public class Laser {
    private int x, y, ancho = 15, alto = 5;
    private int velocidad = 7;
    private boolean activo = true;

    public Laser(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void mover() {
        x += velocidad;
        if (x > 640) activo = false;
    }

    public void dibujar(Graphics g, int offsetX) {
        g.setColor(Color.YELLOW);
        g.fillRect(x - offsetX, y, ancho, alto);
    }

    public void colisiona(Enemigo e) {
        if (activo && e.estaVivo() && getBounds().intersects(e.getBounds())) {
            e.recibirDano(1);
            activo = false;
        }
    }
    public void colisionaJefe(Jefe j) {
        if (activo && j.estaVivo() && getBounds().intersects(j.getBounds())) {
            j.recibirDano(1);
            activo = false;
        }
    }
    public void setActivo(boolean a) { activo = a; }


    public boolean estaActivo() { return activo; }
    public Rectangle getBounds() { return new Rectangle(x, y, ancho, alto); }
}
