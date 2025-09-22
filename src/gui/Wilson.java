package gui; 

import java.util.*;

import javax.swing.ImageIcon;

import java.awt.*;

public class Wilson {
    private int x, y;
    private int ancho, alto;
    private int limiteIzq, limiteDer;
    private int direccion = 1;
    private int velocidad = 2;

    private int vida = 5; // necesita 5 golpes para morir
    private boolean vivo = true;
    private Image imagen;

    private ArrayList<Proyectil> proyectiles;
    private int contadorDisparo = 0; // para controlar tiempo entre disparos
    private int intervaloDisparo = 100; // frames entre disparos

    public Wilson(int x, int y, int limiteIzq, int limiteDer, int ancho, int alto) {
        this.x = x;
        this.y = y - alto;
        this.limiteIzq = limiteIzq;
        this.limiteDer = limiteDer;
        this.ancho = ancho;
        this.alto = alto;
        imagen = new ImageIcon("src/media/wilson.png").getImage();
        proyectiles = new ArrayList<>();
    }

    public void update() {
        if (!vivo) return;

        // Movimiento de Wilson
        x += velocidad * direccion;
        if (x <= limiteIzq || x + ancho >= limiteDer) direccion *= -1;

        // Disparo de proyectiles
        contadorDisparo++;
        if (contadorDisparo >= intervaloDisparo) {
            disparar();
            contadorDisparo = 0;
        }

        // Actualizar proyectiles
        for (Proyectil p : proyectiles) {
            p.update();
        }
        // Eliminar proyectiles inactivos
        proyectiles.removeIf(p -> !p.isActivo());
    }

    public void draw(Graphics g, int camaraX) {
        if (!vivo) return;
        g.drawImage(imagen, x - camaraX, y, ancho, alto, null);

        // Dibujar proyectiles
        for (Proyectil p : proyectiles) {
            p.draw(g, camaraX);
        }
    }

    private void disparar() {
        // Lanza proyectil desde el centro de Wilson
        proyectiles.add(new Proyectil(x, y + alto / 2));
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public ArrayList<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public void recibirDanio() {
        vida--;
        if (vida <= 0) vivo = false;
    }

    public boolean estaVivo() {
        return vivo;
    }

    public int getVida() {
        return vida;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
