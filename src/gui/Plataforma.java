package gui;

import java.awt.*;
import javax.swing.ImageIcon;

public class Plataforma {
    public int x, y;
    public int width = 48;   // ancho de cada plataforma
    public int height = 450;   // alto de la plataforma
    public int cantidad;      // cuántas se repiten horizontalmente
    private Image sprite;
    private String tipo;

    // Constructor por defecto (1 plataforma)
    public Plataforma(String tipo, int x, int y) {
        this(tipo, x, y, 1);
    }

    // Constructor con cantidad de plataformas
    public Plataforma(String tipo, int x, int y, int cantidad) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.cantidad = cantidad;
        cargarSprite();
    }

    private void cargarSprite() {
        switch (tipo.toLowerCase()) {
            case "pasto":
                sprite = new ImageIcon("src/media/plataforma_pasto.png").getImage();
                break;
        }
    }

    public void draw(Graphics g, int camaraX) {
        for (int i = 0; i < cantidad; i++) {
            int px = x + i * width - camaraX;
            if (sprite != null) {
                g.drawImage(sprite, px, y, width, height, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(px, y, width, height);
            }
        }
    }

    public Rectangle getBounds(int i) {
        return new Rectangle(x + i * width, y, width, height);
    }
}