package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SuperPablo extends JPanel implements ActionListener, KeyListener {

    private final int ANCHO_PANTALLA = 640;
    private final int ALTO_PANTALLA = 480;
    private final int POSICION_SUELO_Y = 325;

    private Jugador jugador;
    private FondoParallax cielo;
    private FondoParallax tierra;

    private boolean izquierda = false;
    private boolean derecha = false;

    private Timer timer;

    // Imagen y posición de Walter
    private Image walter;
    private int walterX = 3000; // posición en el mundo
    private int walterY = POSICION_SUELO_Y - 150;
    private boolean mostrarMensaje = false; // controla si se dibuja el cuadro de diálogo
    private boolean primerEncuentroWalter = true; // para saber si es la primera vez


    public SuperPablo() {
        setFocusable(true);
        addKeyListener(this);

        jugador = new Jugador(ANCHO_PANTALLA / 2, POSICION_SUELO_Y);

        Image imgCielo = new ImageIcon("media/cielo.png").getImage();
        Image imgTierra = new ImageIcon("media/tierra.png").getImage();
        cielo = new FondoParallax(imgCielo, 3376, ALTO_PANTALLA, 2);
        tierra = new FondoParallax(imgTierra, 3376, ALTO_PANTALLA, 5);

        walter = new ImageIcon("media/walterElMago.png").getImage();
        walterX = jugador.getX() + 150; // al principio, cerca de Pablo
        walterY = POSICION_SUELO_Y - 100;

        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        cielo.dibujar(g, 0);
        tierra.dibujar(g, 0);

        // Dibujar Walter
        int walterPantallaX = walterX - tierra.getX(); // posición relativa a la pantalla
        g.drawImage(walter, walterPantallaX, walterY, 100, 150, null);

        // Dibujar jugador
        jugador.dibujar(g);

        // Mostrar mensaje si colisionaron
     // Mostrar mensaje si colisionaron
        if (mostrarMensaje) {
            int rectX = 80;
            int rectY = 80;
            int rectAncho = 480;
            int rectAlto = 160;

            g.setColor(Color.WHITE);
            g.fillRect(rectX, rectY, rectAncho, rectAlto);
            g.setColor(Color.BLACK);
            g.drawRect(rectX, rectY, rectAncho, rectAlto);

            String[] lineas;
            if (primerEncuentroWalter) {
                lineas = new String[] {
                    "Ah, por fin llegas, Pablo, caballero perdido.",
                    "Soy Walter, el Hechicero Errante.",
                    "Hace siglos que los antiguos guardianes del reino desaparecieron,",
                    "y ahora peligros acechan en cada senda olvidada.",
                    "Los secretos del bosque y las ruinas de los viejos castillos esperan ser descubiertos…",
                    "pero confío en ti para recorrerlos y cumplir con tu destino.",
                    "Tu misión ahora es ir al castillo.", 
                    "Presiona Enter para continuar"
                };
            } else {
                lineas = new String[] {
                    "¿Ya llegaste al castillo, Pablo? Jeje…",
                    "Presiona Enter para continuar"
                };
            }

            int margenSuperior = 20;          // margen dentro del rectángulo
            int espacioEntreLineas = 18;      // espacio entre cada línea
            int yTexto = rectY + margenSuperior;

            for (String linea : lineas) {
                g.drawString(linea, rectX + 10, yTexto); // 10 px margen izquierdo
                yTexto += espacioEntreLineas;
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jugador.actualizar(POSICION_SUELO_Y);

        // Movimiento y parallax
        if (derecha) {
            if (jugador.getX() < ANCHO_PANTALLA / 2) jugador.moverDerecha();
            else { tierra.moverDerecha(); cielo.moverDerecha(); }
        }
        if (izquierda) {
            if (jugador.getX() > ANCHO_PANTALLA / 2) jugador.moverIzquierda();
            else { tierra.moverIzquierda(); cielo.moverIzquierda(); }
        }

        // Detectar colisión con Walter
        int walterPantallaX = walterX - tierra.getX();
        int distancia = Math.abs(jugador.getX() - walterPantallaX);
        if (distancia < 50 && !mostrarMensaje) { // solo si no se estaba mostrando
            mostrarMensaje = true;
        }


        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> izquierda = true;
            case KeyEvent.VK_RIGHT -> derecha = true;
            case KeyEvent.VK_UP -> jugador.saltar(); // saltar con flecha arriba
            case KeyEvent.VK_ENTER -> {
                if (mostrarMensaje) {
                    mostrarMensaje = false;       // cerramos el mensaje
                    primerEncuentroWalter = false; // ahora se considera que ya habló
                }
            }

        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> izquierda = false;
            case KeyEvent.VK_RIGHT -> derecha = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("SuperPablo");
        SuperPablo game = new SuperPablo();
        frame.add(game);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
