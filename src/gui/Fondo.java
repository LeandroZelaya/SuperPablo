package gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Fondo {

    private int anchoVentana;
    private double desplazamiento = 0;

    public Fondo(int anchoVentana) {
        this.anchoVentana = anchoVentana;
    }

    public void update(int desplazamientoJugador) {
        desplazamiento += desplazamientoJugador;
    }

    public void draw(Graphics g, int nivel) {

        // --- Layer 5 (Cielo) ---
        Image cielo = new ImageIcon("src/media/cielo.png").getImage();
        g.drawImage(cielo, 0, 0, anchoVentana, 720, null);

        // --- Layer 4 (Montañas) ---
        for (int x = 0; x <= 50000; x += 688) {
            int dibX = (int)(x - desplazamiento * 0.1 /* <- velocidad */);
            g.drawImage(new ImageIcon("src/media/montana.png").getImage(), dibX, 250 /* <- y */, null);
        }

        // --- Layer 3 (Bosque) ---
        for (int x = 0; x <= 50000; x += 650) {
            int dibX = (int)(x - desplazamiento * 0.4);
            g.drawImage(new ImageIcon("src/media/bosque.png").getImage(), dibX, 350, null);
        }

        // --- Layer 2 (Arbustos oscuros) ---
        for (int x = 0; x <= 50000; x += 200) {
            int dibX = (int)(x + 100 - desplazamiento * 0.9);
            g.drawImage(new ImageIcon("src/media/arbusto2.png").getImage(), dibX, 580, null);
        }

        // --- Layer 2 (Árboles oscuros) ---
        for (int x = 0; x <= 50000; x += 220) {
            int dibX = (int)(x + 110 - desplazamiento * 0.9);
            g.drawImage(new ImageIcon("src/media/arbol2.png").getImage(), dibX, 450, null);
        }

        // --- Layer 1 (Arbustos) ---
        for (int x = 0; x <= 50000; x += 200) {
            int dibX = (int)(x - desplazamiento * 0.9);
            g.drawImage(new ImageIcon("src/media/arbusto1.png").getImage(), dibX, 580, null);
        }
        
        // --- Layer 1 (Arboles) ---
        for (int x = 0; x <= 50000; x += 220) {
            int dibX = (int)(x - desplazamiento * 0.9);
            g.drawImage(new ImageIcon("src/media/arbol1.png").getImage(), dibX, 450, null);
        }

        // --- Layer 0 (Tierra) ---
        for (int x = 0; x <= 50000; x += 48) {
            int dibX = (int)(x - desplazamiento * 1.0);
            g.drawImage(new ImageIcon("src/media/tierra.png").getImage(), dibX, 650, null);
        }
    }
}