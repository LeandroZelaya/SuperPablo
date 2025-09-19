package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;

public class SuperPablo extends JPanel implements Runnable, KeyListener {

    private Thread gameThread;
    private boolean running = false;

    private Jugador jugador;
    private int nivelActual = 1;
    private Fondo fondoNivel1;
    private Fondo2 fondoNivel2;
    private Fondo3 fondoNivel3;


    private boolean izquierda = false;
    private boolean derecha = false;
    private boolean saltando = false;

    private int camaraX = 0;

    private enum Estado { INICIO, DIALOGO, JUGANDO, PAUSA }
    private Estado estado = Estado.INICIO;

    private Image inicioFondo;
    private Image dialogoFondo;
    private Image mago;

    private Thread sonidosBosqueThread;
    private Thread sonidosCastilloThread;

    private Font miFuente;
    private Font miFuenteDialogo;

    private Clip musicaInicio;
    private Clip musicaBosque;

    private String[] dialogo = {
        "¡Ah, Pablo! Por fin llegaste, caballero perdido.",
        "Soy Walter, el Hechicero Errante.",
        "Hace siglos que los antiguos guardianes del reino desaparecieron.",
        "Ahora, peligros acechan en cada senda olvidada.",
        "Los secretos del bosque y ruinas de los viejos castillos",
        "Esperan ser descubiertos.",
        "Confío en ti para recorrerlos y cumplir con tu destino.",
        "Tu misión ahora es ir al castillo.",
        "¡Buena suerte!"
    };
    private int lineaActual = 0;

    private java.util.List<Enemigo> enemigos = new ArrayList<>();
    private java.util.List<Plataforma> plataformas = new ArrayList<>();
    private int sueloNivel3 = 535; // píxeles desde arriba hasta el suelo del nivel 3

    public SuperPablo() {
        setBackground(Color.CYAN);
        jugador = new Jugador(100, 591);
        setFocusable(true);
        addKeyListener(this);

        fondoNivel1 = new Fondo(1280);
        fondoNivel2 = new Fondo2(1280);
        fondoNivel3 = new Fondo3("src/media/lvl2.png", camaraX, camaraX);



        inicioFondo = new ImageIcon("src/media/PabloFondo.png").getImage();
        dialogoFondo = new ImageIcon("src/media/fondo_walter.png").getImage();
        mago = new ImageIcon("src/media/walter.png").getImage();

        try {
            miFuente = Font.createFont(Font.TRUETYPE_FONT, new File("src/media/MedievalSharp-Regular.ttf")).deriveFont(35f);
            miFuenteDialogo = Font.createFont(Font.TRUETYPE_FONT, new File("src/media/MedievalSharp-Regular.ttf")).deriveFont(35f);
        } catch (Exception e) {
            e.printStackTrace();
            miFuente = new Font("Arial", Font.BOLD, 40);
            miFuenteDialogo = new Font("Arial", Font.BOLD, 40);
        }

        reproducirMusicaInicio("/media/musicaInicio.wav");
        cargarNivel1();
    }

    private void cargarNivel1() {
        enemigos.clear();
        plataformas.clear();
        enemigos.add(new Enemigo(600, 591, 550, 800));

        // Detener sonidos de niveles previos
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) sonidosCastilloThread.interrupt();

        iniciarSonidosBosque();
    }

    private void cargarNivel2() {
        enemigos.clear();
        plataformas.clear();
        enemigos.add(new Enemigo(800, 591, 750, 1000));
        enemigos.add(new Enemigo(1200, 591, 1150, 1400));
       /* plataformas.add(new Plataforma(600, 500));
        plataformas.add(new Plataforma(900, 450, 850, 1050)); // móvil
*/
        jugador.setX(100);
        jugador.setY(591); // ajustar altura según suelo del nivel 2
        camaraX = 0; 
        // Detener sonidos de niveles previos
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) sonidosCastilloThread.interrupt();

        iniciarSonidosBosque();
    }

    private void cargarNivel3() {
        enemigos.clear();

        jugador.setX(100);
        jugador.setY(sueloNivel3 - jugador.getHeight());
        camaraX = 0;

        // Detener sonidos de bosque
        if (sonidosBosqueThread != null && sonidosBosqueThread.isAlive()) sonidosBosqueThread.interrupt();

        sonidosCastillo(); // iniciar sonidos del castillo
    }



    public void reproducirMusicaInicio(String ruta) {
        new Thread(() -> {
            try (InputStream is = getClass().getResourceAsStream(ruta)) {
                if (is == null) return;
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                musicaInicio = AudioSystem.getClip();
                musicaInicio.open(ais);
                musicaInicio.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public void reproducirMusicaBosque(String ruta) {
        new Thread(() -> {
            try (InputStream is = getClass().getResourceAsStream(ruta)) {
                if (is == null) return;
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                musicaBosque = AudioSystem.getClip();
                musicaBosque.open(ais);
                musicaBosque.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public void iniciarSonidosBosque() {
        // Si ya hay un hilo corriendo, no crear otro
        if (sonidosBosqueThread != null && sonidosBosqueThread.isAlive()) return;

        sonidosBosqueThread = new Thread(() -> {
            String[] efectos = {"/media/bird1.wav", "/media/bird2.wav", "/media/hojas.wav"};
            Random rand = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int i = rand.nextInt(efectos.length);
                    reproducirSonido(efectos[i]);
                    Thread.sleep(3000 + rand.nextInt(5000));
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        sonidosBosqueThread.start();
    }

    public void sonidosCastillo() {
        // Detener cualquier hilo previo
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) {
            sonidosCastilloThread.interrupt();
        }
        if (sonidosBosqueThread != null && sonidosBosqueThread.isAlive()) {
            sonidosBosqueThread.interrupt(); // asegurarse que no queden pájaros
        }

        sonidosCastilloThread = new Thread(() -> {
            String[] efectos = {"/media/fantasmaBostezo.wav", "/media/puertaCerrandose.wav", "/media/castilloNoche.wav"};
            Random rand = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int i = rand.nextInt(efectos.length);
                    reproducirSonido(efectos[i]);
                    Thread.sleep(5000 + rand.nextInt(5000));
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        sonidosCastilloThread.start();
    }

    public void reproducirSonido(String ruta) {
        new Thread(() -> {
            try (InputStream is = getClass().getResourceAsStream(ruta)) {
                if (is == null) return;
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public void startGameLoop() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (running) {
            update();   // actualizar jugador, cámara, enemigos y fondo
            repaint();  // dibujar todo
            try { 
                Thread.sleep(16); // ~60 FPS
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

            if (nivelActual == 1) {
                fondoNivel1.update(dx);
            } else if (nivelActual == 2) {
                fondoNivel2.update(dx);
            } else if (nivelActual == 3) {
                fondoNivel3.update(dx, 6000); // 5000 = ancho que le vas a dibujar
            }



            if (camaraX > 10000 && nivelActual == 1) {
                nivelActual = 2;
                cargarNivel2();
            }
            if (camaraX > 20000 && nivelActual == 2) {
                nivelActual = 3;
                cargarNivel3();
            }

        } else {
            jugador.mover(dx);
        }

        if (saltando) {
            jugador.saltar();
            saltando = false;
        }

        jugador.update(izquierda || derecha);
        
        if (nivelActual == 3) {
            if (jugador.getY() + jugador.getHeight() >= sueloNivel3) {
                jugador.setY(sueloNivel3 - jugador.getHeight());
                jugador.setVelocidadY(0); // detener caída
                jugador.setEnSuelo(true); // ya no está saltando
            }
        }


        for (Enemigo e : enemigos) {
            e.update();
            if (jugador.getBounds().intersects(e.getBounds())) {
                while (jugador.getVida() > 0) {
                    jugador.recibirDanio();
                }
                System.out.println("¡Pablo fue eliminado por un enemigo!");
            }
        }


        for (Plataforma p : plataformas) {
            p.update();
            Rectangle plataformaRect = p.getBounds();
            Rectangle jugadorRect = jugador.getBounds();

            boolean caeDesdeArriba = jugador.getVelocidadY() > 0 &&
                jugadorRect.y + jugadorRect.height <= plataformaRect.y + 10 &&
                jugadorRect.x + jugadorRect.width > plataformaRect.x &&
                jugadorRect.x < plataformaRect.x + plataformaRect.width;

            if (caeDesdeArriba) {
                jugador.ajustarSobrePlataforma(p.getY());
            }
        }

        if (jugador.getY() > 800 || jugador.getVida() <= 0) {
            reiniciarNivel();
        }
    }

    private void reiniciarNivel() {
        jugador = new Jugador(100, 591);
        camaraX = 0;
        nivelActual = 1;
        cargarNivel1();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (estado != Estado.PAUSA) super.paintComponent(g);

        switch (estado) {
            case INICIO:
                g.setFont(miFuente);
                g.drawImage(inicioFondo, 0, 0, getWidth(), getHeight(), null);
                g.setColor(Color.BLACK);
                g.drawString("Presiona cualquier tecla para comenzar", 27, 602);
                g.setColor(Color.WHITE);
                g.drawString("Presiona cualquier tecla para comenzar", 25, 600);
                break;

            case DIALOGO:
                g.drawImage(dialogoFondo, 0, 0, getWidth(), getHeight(), null);
                g.drawImage(mago, 50, 250, 337, 450, null);
                g.setColor(new Color(0,0,0,180));
                g.fillRoundRect(420, 50, 800, 150, 20, 20);
                g.setColor(Color.WHITE);
                g.drawRoundRect(420, 50, 800, 150, 20, 20);
                if (miFuenteDialogo != null) g.setFont(miFuenteDialogo);
                g.drawString(dialogo[lineaActual], 440, 100);
                g.setFont(new Font("Arial", Font.ITALIC, 16));
                g.drawString("Presiona ENTER para continuar", 440, 170);
                break;

            case JUGANDO:
                if (nivelActual == 1) fondoNivel1.draw(g);
                else if (nivelActual == 2) fondoNivel2.draw(g);
                else
                	fondoNivel3.draw(g, 5000, 700); // por ejemplo, ancho=2500, alto=1200



                jugador.draw(g);

                for (Enemigo e : enemigos) {
                    e.draw(g, camaraX);
                }

                for (Plataforma p : plataformas) {
                    p.draw(g, camaraX);
                }

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("Vida: " + jugador.getVida(), 30, 40);
                break;

            case PAUSA:
                if (miFuente != null) g.setFont(miFuente);
                g.setColor(Color.WHITE);
                g.drawString("PAUSA", getWidth() / 2 - 80, getHeight() / 2 - 50);
                g.drawString("ESC - Continuar", getWidth() / 2 - 120, getHeight() / 2 + 20);
                g.drawString("Q - Salir", getWidth() / 2 - 120, getHeight() / 2 + 60);
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (estado == Estado.JUGANDO && key == KeyEvent.VK_ESCAPE) {
            estado = Estado.PAUSA;
            return;
        } else if (estado == Estado.PAUSA && key == KeyEvent.VK_ESCAPE) {
            estado = Estado.JUGANDO;
            return;
        }

        if (estado == Estado.INICIO) {
            estado = Estado.DIALOGO;
            if (musicaInicio != null && musicaInicio.isRunning()) {
                musicaInicio.stop();
                musicaInicio.close();
            }
        } else if (estado == Estado.DIALOGO) {
            if (key == KeyEvent.VK_ENTER) {
                lineaActual++;
                if (lineaActual >= dialogo.length) {
                    estado = Estado.JUGANDO;
                    reproducirMusicaBosque("/media/wind.wav");
                    iniciarSonidosBosque();
                }
            }
        } else if (estado == Estado.JUGANDO) {
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) izquierda = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) derecha = true;
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) saltando = true;

            if (key == KeyEvent.VK_N) {
                nivelActual = 2;
                cargarNivel2();
                System.out.println("¡Nivel 2 activado con enemigos y plataformas!");
            }
            if (key == KeyEvent.VK_T) {
                nivelActual = 3;
                cargarNivel3();
                System.out.println("Nivel 3 activado manualmente con la tecla T");
            }


        } else if (estado == Estado.PAUSA) {
            if (key == KeyEvent.VK_Q) System.exit(0);
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
