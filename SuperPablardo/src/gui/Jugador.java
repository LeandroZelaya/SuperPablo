package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip; 
import java.io.File;

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
    
    private Clip sonidoPisada; 
    private int contadorPisada = 0; 
    private int vida = 3;


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

    
    public void cargarSonidoPisada(String ruta) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(ruta));
            sonidoPisada = AudioSystem.getClip();
            sonidoPisada.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            contadorPisada++;

            // Animación de caminar
            if (contadorAnimacion >= 10) {
                animacionPaso = (animacionPaso + 1) % 2;
                contadorAnimacion = 0;
            }

            // Reproducir pisada cada cierto número de frames
            if (contadorPisada >= 10) { // ajustá según velocidad
                if (sonidoPisada != null) {
                    if (sonidoPisada.isRunning()) sonidoPisada.stop();
                    sonidoPisada.setFramePosition(0);
                    sonidoPisada.start();
                }
                contadorPisada = 0;
            }

        } else {
            animacionPaso = 0;
            contadorPisada = 0; // reiniciar al detenerse
        }
        
        if (danio) {
            // Podés usar un contador si querés que dure más
            danio = false;
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

    public int getVida() {
        return vida;
    }

    public void recibirDanio() {
        if (danio || !vivo) return; // evita daño repetido
        danio = true;
        vida--;
        System.out.println("¡Pablo recibió daño! Vida restante: " + vida);
        if (vida <= 0) {
            morir();
        }
    }

    public void curar() {
        if (vida < 3) vida++;
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
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void ajustarSobrePlataforma(int yPlataforma) {
        y = yPlataforma - height;
        velocidadY = 0;
        enSuelo = true;
    }



}