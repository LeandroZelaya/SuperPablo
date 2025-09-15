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

    public void draw(Graphics g) {
        int yMontanas = 250;
        int yBosque = 350;
        int yArbustos = 580;
        int yArboles = 450;
        int yPiso = 650;

        double velMontanas = 0.1;
        double velBosque = 0.4;
        double velArb = 0.9;
        double velPiso = 1.0;

        // Cielo
        Image cielo = new ImageIcon("src/media/cielo.png").getImage();
        g.drawImage(cielo, 0, 0, anchoVentana, 720, null);

        // --- Montañas ---
        for (int x = 0; x <= 50000; x += 688) {
            int dibX = (int)(x - desplazamiento * velMontanas);
            g.drawImage(new ImageIcon("src/media/montana.png").getImage(), dibX, yMontanas, null);
        }

        // --- Bosque ---
        for (int x = 0; x <= 50000; x += 650) {
            int dibX = (int)(x - desplazamiento * velBosque);
            g.drawImage(new ImageIcon("src/media/bosque.png").getImage(), dibX, yBosque, null);
        }

        // --- Arbustos2 ---
        for (int x = 0; x <= 50000; x += 200) {
            int dibX = (int)(x + 100 - desplazamiento * velArb);
            g.drawImage(new ImageIcon("src/media/arbusto2.png").getImage(), dibX, yArbustos, null);
        }

        // --- Arboles2 ---
        for (int x = 0; x <= 50000; x += 220) {
            int dibX = (int)(x + 110 - desplazamiento * velArb);
            g.drawImage(new ImageIcon("src/media/arbol2.png").getImage(), dibX, yArboles, null);
        }

        // --- Arbustos1 ---
        for (int x = 0; x <= 50000; x += 200) {
            int dibX = (int)(x - desplazamiento * velArb);
            g.drawImage(new ImageIcon("src/media/arbusto1.png").getImage(), dibX, yArbustos, null);
        }
        
        // --- Arboles1 ---
        for (int x = 0; x <= 50000; x += 220) {
            int dibX = (int)(x - desplazamiento * velArb);
            g.drawImage(new ImageIcon("src/media/arbol1.png").getImage(), dibX, yArboles, null);
        }

        // --- Piso ---
        for (int x = 0; x <= 50000; x += 48) {
            int dibX = (int)(x - desplazamiento * velPiso);
            g.drawImage(new ImageIcon("src/media/pasto.png").getImage(), dibX, yPiso, null);
        }
    }
}