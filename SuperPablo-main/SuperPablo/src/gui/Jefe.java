package gui;

import java.awt.*;
import javax.swing.ImageIcon;

public class Jefe {
    private int x, y;
    private int ancho = 120, alto = 100;
    private int vidas = 2; // más vida que enemigos normales
    private boolean vivo = true;
    private Image imagen;
    private Image explosion;
    private boolean explotando = false;
    private int tiempoExplosion = 0;

    private int velocidad = 1; // se mueve lentamente

    public Jefe(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            imagen = new ImageIcon(getClass().getResource("/media/jefe.png")).getImage();
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
                g.setColor(Color.BLACK);
                g.fillRect(x - offsetX, y, ancho, alto);
            }
        } else if (explotando) {
            if (explosion != null) g.drawImage(explosion, x - offsetX, y, ancho, alto, null);
            tiempoExplosion--;
            if (tiempoExplosion <= 0) explotando = false;
        }
    }


    public void actualizar() {
        x += velocidad;
        if (x < 800) velocidad = Math.abs(velocidad);   // no ir demasiado a la izquierda
        if (x > 2000) velocidad = -Math.abs(velocidad); // no ir demasiado a la derecha
    }

    public void recibirDano(int d) {
        if (vivo) {
            vidas -= d;
            if (vidas <= 0) {
                vivo = false;
                explotando = true;
                tiempoExplosion = 30; // duracion animacion
            }
        }
    }



    public void morirPorContacto(Jugador jugador) {
        if (vivo && getBounds().intersects(jugador.getBounds())) {
            recibirDano(1);
        }
    }



    public boolean estaVivo() { return vivo; }
    public Rectangle getBounds() { return new Rectangle(x, y, ancho, alto); }
    public int getVidas() { return vidas; }
    public void setVelocidad(int v) {
        this.velocidad = v;
    }

    public void setVidas(int v) {
        this.vidas = v;
    }

}
