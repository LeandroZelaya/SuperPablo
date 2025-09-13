package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private int x, y, ancho = 50, alto = 50;
    private int velocidadX = 5;
    private int velocidadY = 0;
    private final int gravedad = 2;
    private boolean enSuelo = true;

    private List<Laser> lasers = new ArrayList<>();

    public Jugador(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, ancho, alto);
        for (Laser l : lasers) l.dibujar(g, 0);
    }

    public void actualizar(int sueloY) {
        // Gravedad
        if (!enSuelo) {
            y += velocidadY;
            velocidadY += gravedad;
            if (y >= sueloY) {
                y = sueloY;
                enSuelo = true;
                velocidadY = 0;
            }
        }
    }

    public void moverDerecha() { x += velocidadX; }
    public void moverIzquierda() { x -= velocidadX; }

    public void saltar() {
        if (enSuelo) {
            velocidadY = -15; // fuerza del salto
            enSuelo = false;
        }
    }

    public void disparar() {
        lasers.add(new Laser(x + ancho, y + alto/2 - 2));
    }

    public List<Laser> getLasers() { return lasers; }

    public Rectangle getBounds() { return new Rectangle(x, y, ancho, alto); }

    public int getX() { return x; }
    public int getY() { return y; }
}
