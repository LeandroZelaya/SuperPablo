package gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Fondo3 {

    private Image fondo;
    private int ancho, alto;  // ancho y alto que vos querés
    private double desplazamiento = 0;

    public Fondo3(String rutaImagen, int ancho, int alto) {
        this.fondo = new ImageIcon(rutaImagen).getImage();
        this.ancho = ancho;
        this.alto = alto;
    }

    public void update(int desplazamientoJugador, int anchoDeseado) {
        desplazamiento += desplazamientoJugador;

        if (desplazamiento < 0) 
            desplazamiento = 0;

        // Limitar el desplazamiento según el ancho que vas a dibujar y el ancho de la ventana
        int anchoVentana = 1280; // o el ancho de tu panel/juego
        if (desplazamiento > anchoDeseado - anchoVentana)
            desplazamiento = anchoDeseado - anchoVentana;
    }



    public void draw(Graphics g, int anchoDeseado, int altoDeseado) {
        g.drawImage(fondo, (int)-desplazamiento, 0, anchoDeseado, altoDeseado, null);
    }

}

