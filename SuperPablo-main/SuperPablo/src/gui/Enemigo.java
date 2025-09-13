package gui;

import java.awt.*;
import javax.swing.ImageIcon;

public class Enemigo {
    private int x, y;
    private int ancho = 80, alto = 60;
    private boolean vivo = true;
    private Image imagen;
    private Image explosion;
    private boolean explotando = false;
    private int tiempoExplosion = 0;

    private int velocidad = 2;

    public Enemigo(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            imagen = new ImageIcon(getClass().getResource("/media/enemigo.png")).getImage();
            explosion = new ImageIcon(getClass().getResource("/media/explosion.png")).getImage();
        } catch(Exception e) {
            imagen = null;
            explosion = null;
        }
    }

    public void dibujar(Graphics g, int offsetX) {
        if (vivo) {
            if (imagen != null) g.drawImage(imagen, x - offsetX, y, ancho, alto, null);
            else {
                g.setColor(Color.RED);
                g.fillRect(x - offsetX, y, ancho, alto);
            }
        } else if (explotando) {
            if (explosion != null) g.drawImage(explosion, x - offsetX, y, ancho, alto, null);
            tiempoExplosion--;
            if (tiempoExplosion <= 0) explotando = false;
        }
    }

    public void actualizar() {
        x -= velocidad;
        if (x < -ancho) x = 640 + ancho;
    }

    public void recibirDano(int d) {
        if (vivo) {
            vivo = false;
            explotando = true;
            tiempoExplosion = 20;
        }
    }

    public void morirPorContacto(Jugador jugador) {
        if (vivo && getBounds().intersects(jugador.getBounds())) {
            recibirDano(1);
        }
    }

    public boolean estaVivo() { return vivo; }
    public Rectangle getBounds() { return new Rectangle(x, y, ancho, alto); }
}
