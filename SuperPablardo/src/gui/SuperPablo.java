package gui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class SuperPablo extends JPanel implements Runnable, KeyListener {

    private Thread gameThread;
    private boolean running = false;

    private Jugador jugador;
    private Fondo fondo;

    private boolean izquierda = false;
    private boolean derecha = false;
    private boolean saltando = false;

    private int camaraX = 0;

    // Estados del juego
    private enum Estado { INICIO, DIALOGO, JUGANDO }
    private Estado estado = Estado.INICIO;

    // Imagenes
    private Image inicioFondo;
    private Image dialogoFondo;
    private Image mago;

    // Dialogo
    private String[] dialogo = {
        "¡Ah, Pablo! Por fin llegaste, caballero perdido.",
        "Soy Walter, el Hechicero Errante.",
        "Hace siglos que los antiguos guardianes del reino desaparecieron.",
        "Ahora, peligros acechan en cada senda olvidada.",
        "Los secretos del bosque y ruinas de los viejos castillos esperan ser descubiertos.",
        "Confío en ti para recorrerlos y cumplir con tu destino.",
        "Tu misión ahora es ir al castillo.",
        "¡Buena suerte!"
    };
    private int lineaActual = 0;

    public SuperPablo() {
        setBackground(Color.CYAN);
        jugador = new Jugador(100, 591);
        setFocusable(true);
        addKeyListener(this);

        fondo = new Fondo(1280);

        // Cargar imágenes
        inicioFondo = new ImageIcon("src/media/PabloFondo.png").getImage();
        dialogoFondo = new ImageIcon("src/media/fondo_walter.png").getImage();
        mago = new ImageIcon("src/media/walter.png").getImage();
    }

    public void startGameLoop() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (running) {
            update();
            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (estado != Estado.JUGANDO) return;

        int dx = 0;
        if (izquierda) dx = -5;
        if (derecha) dx = 5;

        if ((jugador.getX() > 400 && dx > 0) || (jugador.getX() < 400 && dx < 0 && camaraX > 0)) {
            camaraX += dx;
            if (camaraX < 0) camaraX = 0;
            fondo.update(dx);
        } else {
            jugador.mover(dx);
        }

        if (saltando) {
            jugador.saltar();
            saltando = false;
        }

        jugador.update(izquierda || derecha);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (estado) {
            case INICIO:
                g.drawImage(inicioFondo, 0, 0, getWidth(), getHeight(), null);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("Presiona cualquier tecla para comenzar", 400, 300);
                break;

            case DIALOGO:
                g.drawImage(dialogoFondo, 0, 0, getWidth(), getHeight(), null);

                // Dibujar al mago
                g.drawImage(mago, 50, 250, 337, 450, null);

                // Dibujar globo de texto
                int globoX = 420;
                int globoY = 50;
                int globoAncho = 800;
                int globoAlto = 150;
                g.setColor(new Color(0, 0, 0, 180)); // fondo semitransparente
                g.fillRoundRect(globoX, globoY, globoAncho, globoAlto, 20, 20);
                g.setColor(Color.WHITE);
                g.drawRoundRect(globoX, globoY, globoAncho, globoAlto, 20, 20);

                // Escribir texto dentro del globo
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString(dialogo[lineaActual], globoX + 20, globoY + 50);

                // Indicador para continuar
                g.setFont(new Font("Arial", Font.ITALIC, 16));
                g.drawString("Presiona ENTER para continuar", globoX + 20, globoY + 120);
                break;

            case JUGANDO:
                fondo.draw(g);
                jugador.draw(g);
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (estado == Estado.INICIO) {
            estado = Estado.DIALOGO;
        } else if (estado == Estado.DIALOGO) {
            if (key == KeyEvent.VK_ENTER) {
                lineaActual++;
                if (lineaActual >= dialogo.length) {
                    estado = Estado.JUGANDO;
                }
            }
        } else if (estado == Estado.JUGANDO) {
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) izquierda = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) derecha = true;
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) saltando = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (estado != Estado.JUGANDO) return;

        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) izquierda = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) derecha = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
