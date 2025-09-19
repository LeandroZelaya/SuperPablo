package gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Fondo2 {

    private int anchoVentana;
    private double desplazamiento = 0;
    Image[] negocios = {
    	    new ImageIcon("src/media/negoco21.png").getImage(),
    	    new ImageIcon("src/media/negoco23.png").getImage(),
    	    new ImageIcon("src/media/negoco24.png").getImage()
    	};


    public Fondo2(int anchoVentana) {
        this.anchoVentana = anchoVentana;
    }

    public void update(int desplazamientoJugador) {
        desplazamiento += desplazamientoJugador;
    }

    public void draw(Graphics g) {
        int yCiudad = 0;
        int yBosque = 15;
        int yTerreno = 650;
        int yNegocio = 50;
        double velCasas = 0.8;


        double velCiudad = 0.2;
        double velBosque = 0.5;
        double velTerreno = 1.0;

        // --- Cielo plano ---
        g.setColor(java.awt.Color.CYAN);
        g.fillRect(0, 0, anchoVentana, 720);

        // --- Ciudad industrial (fondo más lejano) ---
        Image ciudad = new ImageIcon("src/media/Background_01.png").getImage();
        for (int x = 0; x <= 50000; x += ciudad.getWidth(null)) {
            int dibX = (int)(x - desplazamiento * velCiudad);
            g.drawImage(ciudad, dibX, yCiudad, null);
        }

        // --- Bosque estilizado (capa intermedia) ---
        Image bosque = new ImageIcon("src/media/Background_02.png").getImage();
        for (int x = 0; x <= 50000; x += bosque.getWidth(null)) {
            int dibX = (int)(x - desplazamiento * velBosque);
            g.drawImage(bosque, dibX, yBosque, null);
        }
        
        for (int x = 0; x <= 50000; x += 400) {
            int dibX = (int)(x - desplazamiento * velCasas);
            int index = (x / 400) % negocios.length; // alterna entre 0, 1, 2...
            g.drawImage(negocios[index], dibX, yNegocio, 1000, 900, null);
        }
        
        Image terreno = new ImageIcon("src/media/pasto2.png").getImage();
        for (int x = 0; x <= 50000; x += terreno.getWidth(null)) {
            int dibX = (int)(x - desplazamiento * velTerreno);
            g.drawImage(terreno, dibX, yTerreno, null);
        }
        
      }
        
        


        // --- Terreno pixelado (primer plano) ---
        
    }

