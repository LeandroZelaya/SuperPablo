package gui;

import java.awt.Graphics;
import java.awt.Color;

public class Jugador {
    private int x, y;
    private int ancho, alto;
    private int velocidadX = 5;

    // Para salto
    private int velocidadY = 0;
    private final int gravedad = 1;
    private boolean enSuelo = true;

    public Jugador(int x, int y) {
        this.x = x;
        this.y = y;
        this.ancho = 50;
        this.alto = 50;
    }

    // Movimiento horizontal
    public void moverDerecha() { x += velocidadX; }
    public void moverIzquierda() { x -= velocidadX; }

    // Saltar
    public void saltar() {
        if (enSuelo) {
            velocidadY = -17;  // fuerza del salto
            enSuelo = false;
        }
    }

    // Actualizar posición vertical
    public void actualizar(int sueloY) {
        if (!enSuelo) {
            y += velocidadY;
            velocidadY += gravedad;  // aplica gravedad

            if (y >= sueloY) {
                y = sueloY;
                velocidadY = 0;
                enSuelo = true;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void dibujar(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, ancho, alto);
    }
    
}
