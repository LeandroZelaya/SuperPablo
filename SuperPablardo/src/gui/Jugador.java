package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

public class Jugador {

    private int x, y;
    private int width, height;
    private int velocidadY = 0;
    private boolean enSuelo = true;
    private boolean vivo = true;
    private boolean danio = false;

    private int velocidadX = 0; // velocidad horizontal actual

    private int animacionPaso = 0;
    private int contadorAnimacion = 0;

    private boolean mirandoDerecha = true;

    // Imágenes
    private Image imagenParado;
    private Image[] imagenCaminando = new Image[2];
    private Image imagenSaltando;
    private Image imagenDanio;
    private Image imagenMuerte;

    public Jugador(int x, int y) {
        this.x = x;
        this.y = y;

        // Cargar imágenes
        imagenParado = new ImageIcon("src/media/pablo.png").getImage();
        imagenCaminando[0] = new ImageIcon("src/media/pablo_camina1.png").getImage();
        imagenCaminando[1] = new ImageIcon("src/media/pablo_camina2.png").getImage();
        imagenSaltando = new ImageIcon("src/media/pablo_salto.png").getImage();
        imagenDanio = new ImageIcon("src/media/pablo_danio.png").getImage();
        imagenMuerte = new ImageIcon("src/media/pablo_muerte.png").getImage();

        width = imagenParado.getWidth(null);
        height = imagenParado.getHeight(null);
    }

    public void update(boolean moviendo) {
        // Gravedad
        if (!enSuelo) {
            velocidadY += 1;
            y += velocidadY;

            if (y >= 591) {
                y = 591;
                enSuelo = true;
                velocidadY = 0;
            }
        }

        if (enSuelo && moviendo) {
            contadorAnimacion++;
            if (contadorAnimacion >= 10) {
                animacionPaso = (animacionPaso + 1) % 2;
                contadorAnimacion = 0;
            }
        } else {
            animacionPaso = 0;
        }
    }

    public void mover(int dx) {
        x += dx;
        velocidadX = dx;

        if (x < 100) x = 100;
        if (x > 1280 - width) x = 1280 - width;

        if (dx > 0) mirandoDerecha = true;
        else if (dx < 0) mirandoDerecha = false;
    }

    public void saltar() {
        if (enSuelo) {
            velocidadY = -20;
            enSuelo = false;
        }
    }

    public void recibirDanio() {
        danio = true;
    }

    public void morir() {
        vivo = false;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Image imagenActual;

        if (!vivo) imagenActual = imagenMuerte;
        else if (danio) imagenActual = imagenDanio;
        else if (!enSuelo) imagenActual = imagenSaltando;
        else if (velocidadX != 0) imagenActual = (animacionPaso == 0) ? imagenCaminando[0] : imagenCaminando[1];
        else imagenActual = imagenParado;

        AffineTransform at = new AffineTransform();

        if (mirandoDerecha) {
            at.translate(x, y);
        } else {
            at.translate(x + width, y);
            at.scale(-1, 1);
        }

        g2d.drawImage(imagenActual, at, null);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHeight() { return height; }
    public int getVelocidadY() { return velocidadY; }
    public void setVelocidadY(int v) { velocidadY = v; }
    public void setEnSuelo(boolean b) { enSuelo = b; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}