package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SuperPablo extends JPanel implements ActionListener, KeyListener {

    private final int ANCHO_PANTALLA = 640;
    private final int ALTO_PANTALLA = 480;
    private final int POSICION_SUELO_Y = 325;

    private Jugador jugador;
    private Enemigo[] enemigos;
    private Jefe jefe;

    private boolean izquierda = false;
    private boolean derecha = false;
    private boolean nivelCompletado = false;
    private int nivel = 1;

    private FondoParallax cielo;
    private FondoParallax tierra;

    private Timer timer;

    public SuperPablo() {
        setFocusable(true);
        addKeyListener(this);

        // Jugador
        jugador = new Jugador(ANCHO_PANTALLA / 2, POSICION_SUELO_Y - 50);

        // Iniciar primer nivel
        iniciarNivel(1);

        // Timer principal
        timer = new Timer(20, this);
        timer.start();
    }

    private void iniciarNivel(int n) {
        nivel = n;

        // Fondo (puedes cambiar según nivel)
        Image imgCielo = new ImageIcon("media/cielo.png").getImage();
        Image imgTierra = new ImageIcon("media/tierra.png").getImage();
        cielo = new FondoParallax(imgCielo, 3376, ALTO_PANTALLA, 2);
        tierra = new FondoParallax(imgTierra, 3376, ALTO_PANTALLA, 5);

        // Enemigos y jefe según nivel
        if (nivel == 1) {
            enemigos = new Enemigo[3];
            enemigos[0] = new Enemigo(800, POSICION_SUELO_Y - 50);
            enemigos[1] = new Enemigo(1200, POSICION_SUELO_Y - 50);
            enemigos[2] = new Enemigo(1600, POSICION_SUELO_Y - 50);
            jefe = new Jefe(1900, POSICION_SUELO_Y - 100);
        } else if (nivel == 2) {
            enemigos = new Enemigo[4];
            enemigos[0] = new Enemigo(700, POSICION_SUELO_Y - 50);
            enemigos[1] = new Enemigo(1100, POSICION_SUELO_Y - 50);
            enemigos[2] = new Enemigo(1500, POSICION_SUELO_Y - 50);
            enemigos[3] = new Enemigo(1900, POSICION_SUELO_Y - 50);
            jefe = new Jefe(200, POSICION_SUELO_Y - 100);
            jefe.setVelocidad(2);
            jefe.setVidas(3);
        }

        nivelCompletado = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar fondo
        cielo.dibujar(g, 0);
        tierra.dibujar(g, 0);

        // Dibujar jugador
        jugador.dibujar(g);

        // Dibujar enemigos
        for (Enemigo en : enemigos) {
            if (en != null) en.dibujar(g, tierra.getX());
        }

        // Dibujar jefe
        if (jefe != null) jefe.dibujar(g, tierra.getX());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jugador.actualizar(POSICION_SUELO_Y - 50);

        // Movimiento jugador y parallax
        if (derecha) {
            if (jugador.getX() < ANCHO_PANTALLA / 2) jugador.moverDerecha();
            else { tierra.moverDerecha(); cielo.moverDerecha(); }
        }
        if (izquierda) {
            if (jugador.getX() > ANCHO_PANTALLA / 2) jugador.moverIzquierda();
            else { tierra.moverIzquierda(); cielo.moverIzquierda(); }
        }

        // Actualizar enemigos
        for (Enemigo en : enemigos) {
            if (en != null) {
                en.actualizar();
                en.morirPorContacto(jugador);
            }
        }

        // Actualizar jefe
        if (jefe != null && jefe.estaVivo()) {
            jefe.actualizar();
            jefe.morirPorContacto(jugador);
        } else if (jefe != null && !jefe.estaVivo() && !nivelCompletado) {
            nivelCompletado = true;
            JOptionPane.showMessageDialog(this, "¡Nivel " + nivel + " completado!");
            if (nivel == 1) {
                iniciarNivel(2);
            } else {
                JOptionPane.showMessageDialog(this, "¡Ganaste el juego!");
            }
        }

        // Actualizar láseres y colisiones con enemigos y jefe
        for (Laser l : jugador.getLasers()) {
            l.mover();

            // Si el láser todavía está activo, verifica la colisión con el jefe
            if (l.estaActivo() && jefe != null) {
                l.colisionaJefe(jefe);
            }

            // Si el láser aún está activo (no golpeó al jefe), revisa las colisiones con los enemigos
            if (l.estaActivo()) {
                for (Enemigo en : enemigos) {
                    if (en != null) {
                        l.colisiona(en);
                        if (!l.estaActivo()) {
                            // Si el láser golpea a un enemigo y se desactiva,
                            // no necesitamos revisar más enemigos para este láser
                            break;
                        }
                    }
                }
            }
        }
        // Elimina los láseres que ya no están activos
        jugador.getLasers().removeIf(l -> !l.estaActivo());

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> izquierda = true;
            case KeyEvent.VK_RIGHT -> derecha = true;
            case KeyEvent.VK_UP -> jugador.saltar();
            case KeyEvent.VK_SPACE -> jugador.disparar();
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
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
