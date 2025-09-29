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
    
    private int puntos;

    public static boolean izquierda = false;
    public static boolean derecha = false;
    private boolean saltando = false;
    public static boolean dobleSalto = false;
    public static boolean dobleDanio = false;
    private boolean poderElegir = false;
    private boolean elegido = false;
    
    private boolean wilsonCreado=false;
    private int fase=0;

    private int camaraX = 0;
    
    private int cont = 0;
    private int paso = 1;

    private enum Estado { INICIO, DIALOGO, JUGANDO, PAUSA, GAMEOVER, WIN }
    private Estado estado = Estado.INICIO;

    private Image inicioFondo;
    private Image dialogoFondo;
    private Image mago;
    private Image vendedor;

    public static Thread sonidosBosqueThread;
    public static Thread sonidosCastilloThread;

    private Font miFuente;
    private Font perderFuente;
    private Font miFuenteDialogo;

    public static Clip musicaInicio;
    private Clip musicaBosque;
    private Clip sonidoActual;

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

    private String[] dialogoNivel2 = {
    	    "Bienvenido a Bramavilla, Pablo.",
    	    "Este bullicioso mercado es hogar de comerciantes y viajeros.",
    	    "Mantente atento: algunos callejones esconden sorpresas.",
    	    "Sigue así Pablo, ya estás cerca del castillo."
    	};

    	private int lineaActualNivel2 = 0;

    	private boolean vendedorDialogoMostrado = false;
    	private String[] dialogoVendedor = {
    	    "¡Hola, Pablo! Bienvenido a mi tienda.",
    	    "Soy Pancrasio, escuché que te diriges al castillo.",
    	    "Debes de tener cuidado, cosas malas pasan ahí.",
    	    "Te dejaré tomar una de mis dos pociones.",
    	    "Presiona 1 si quieres mi poción de recuperar 5 de vida,",
    	    "Presiona 2 si quieres mi poción de doble de fuerza.",
    	    "¡Gracias por tu visita, y suerte en el castillo!"
    	};
    	private int lineaActualVendedor = 0;
    	
    	private String[] dialogoBatalla = {
    		    "Walter: ¡Por fin llegaste al castillo, Pablo!",
    		    "Wilson: ¡JAJA! No tan rápido, hermano...",
    		    "Walter: ¡Wilson, traidor! Nunca debiste volver.",
    		    "Wilson: Este reino será mío, y destruiré a tu aprendiz.",
    		    "Walter: Pablo, prepárate. Ésta será la batalla más difícil.",
    		    "Wilson: ¡Muere caballero inútil!",
    		    "Walter: ¡Resiste Pablo, juntos lo venceremos!"
    		};
    		private int lineaActualBatalla = 0;
    		private boolean batallaDialogoMostrado = false;



    private java.util.List<Enemigo> enemigos = new ArrayList<>();
    private java.util.List<Enemigo> enemigosJ = new ArrayList<>();
    private java.util.List<Plataforma> plataformas = new ArrayList<>();
    private int sueloNivel3 = 534; // píxeles desde arriba hasta el suelo del nivel 3
    private int sueloNivel2 = 650; 
    private int sueloNivel1 = 650;
    private Wilson wilson; 
    
    public SuperPablo() {
        setBackground(Color.CYAN);
        jugador = new Jugador(100, 591);
        setFocusable(true);
        addKeyListener(this);

        fondoNivel1 = new Fondo(1280);
        fondoNivel2 = new Fondo2("src/media/lvl2Aldea.png", camaraX, camaraX);
        fondoNivel3 = new Fondo3("src/media/lvl3121212.png", 7000, 700);
        inicioFondo = new ImageIcon("src/media/PabloFondo.png").getImage();
        dialogoFondo = new ImageIcon("src/media/fondo_walter.png").getImage();
        mago = new ImageIcon("src/media/walter.png").getImage();
        vendedor = new ImageIcon("src/media/vendedor.png").getImage();

        try {
            miFuente = Font.createFont(Font.TRUETYPE_FONT, new File("src/media/MedievalSharp-Regular.ttf")).deriveFont(35f);
            perderFuente = Font.createFont(Font.TRUETYPE_FONT, new File("src/media/MedievalSharp-Regular.ttf")).deriveFont(60f);
            miFuenteDialogo = Font.createFont(Font.TRUETYPE_FONT, new File("src/media/MedievalSharp-Regular.ttf")).deriveFont(25f);
        } catch (Exception e) {
            e.printStackTrace();
            miFuente = new Font("Arial", Font.BOLD, 40);
            perderFuente = new Font("Arial", Font.BOLD, 60);
            miFuenteDialogo = new Font("Arial", Font.BOLD, 40);
        }

        reproducirMusica("/media/musicaInicio.wav");
        cargarNivel1();
    }

    private void cargarNivel1() {
        enemigos.clear();
        plataformas.clear();
        
        cargarEnemigosNivel1();
        cargarPlataformasNivel1();

        // Detener sonidos de niveles previos
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) sonidosCastilloThread.interrupt();
        if(estado == Estado.JUGANDO) {
        	reproducirMusica("/media/cancion.wav");
        	iniciarSonidosBosque();
        }
    }
    
    private void cargarEnemigosNivel1()
    {
    	enemigos.add(new Enemigo(1350, 570, 1300, 1500, "esqueleto", 1));
    	enemigos.add(new Enemigo(1650, 450, 1600, 1750, "esqueleto", 1));
    	enemigos.add(new Enemigo(1800, sueloNivel1, 1700, 1900, "esqueleto", 1));
    	enemigos.add(new Enemigo(2050, 100, 30, 400, "murcielago", 1));
    	enemigos.add(new Enemigo(2050, 600, 2000, 2110, "esqueleto", 1));
    	enemigos.add(new Enemigo(2250, 400, 2200, 2310, "esqueleto", 1));
    	enemigos.add(new Enemigo(2450, 500, 2400, 2510, "esqueleto", 1));
    	enemigos.add(new Enemigo(2700, sueloNivel1, 2500, 2900, "esqueleto", 1));
    	enemigos.add(new Enemigo(2750, sueloNivel1, 2550, 2950, "esqueleto", 1));
    	enemigos.add(new Enemigo(4500, sueloNivel1, 4250, 4550, "esqueleto", 1));
    	enemigos.add(new Enemigo(4510, sueloNivel1, 4250, 4650, "esqueleto", 1));
    	enemigos.add(new Enemigo(4520, sueloNivel1, 4250, 4750, "esqueleto", 1));
    	enemigos.add(new Enemigo(4530, sueloNivel1, 4250, 4850, "esqueleto", 1));
    	enemigos.add(new Enemigo(4540, sueloNivel1, 4250, 4950, "esqueleto", 1));
    	enemigos.add(new Enemigo(4550, sueloNivel1, 4250, 5050, "esqueleto", 1));
    	enemigos.add(new Enemigo(4560, sueloNivel1, 4250, 5150, "esqueleto", 1));
    	enemigos.add(new Enemigo(4570, sueloNivel1, 4250, 5250, "esqueleto", 1));
    	enemigos.add(new Enemigo(4580, sueloNivel1, 4250, 5350, "esqueleto", 1));
    	enemigos.add(new Enemigo(4590, sueloNivel1, 4250, 5450, "esqueleto", 1));
    	enemigos.add(new Enemigo(4600, sueloNivel1, 4250, 5550, "esqueleto", 1));
    	enemigos.add(new Enemigo(4610, sueloNivel1, 4250, 5650, "esqueleto", 1));
    	enemigos.add(new Enemigo(4620, sueloNivel1, 4250, 5750, "esqueleto", 1));
    	enemigos.add(new Enemigo(4630, sueloNivel1, 4250, 5850, "esqueleto", 1));
    	enemigos.add(new Enemigo(4640, sueloNivel1, 4250, 5950, "esqueleto", 1));
    	enemigos.add(new Enemigo(5500, sueloNivel1, 5500, 5950, "esqueleto", 1));
    	enemigos.add(new Enemigo(5600, sueloNivel1, 5550, 5950, "esqueleto", 1));
    	enemigos.add(new Enemigo(5700, sueloNivel1, 5600, 5950, "esqueleto", 1));
    	enemigos.add(new Enemigo(5800, sueloNivel1, 5650, 5950, "esqueleto", 1));
    	enemigos.add(new Enemigo(5900, sueloNivel1, 5700, 5950, "esqueleto", 1));
    	enemigos.add(new Enemigo(4700, 460, 4600, 4800, "esqueleto", 1));
    	enemigos.add(new Enemigo(4900, 100, 30, 400, "murcielago", 1));
    	enemigos.add(new Enemigo(5100, 100, 30, 500, "murcielago", 1));
    	enemigos.add(new Enemigo(6300, 100, 30, 600, "murcielago", 1));
    	enemigos.add(new Enemigo(6300, 330, 30, 600, "murcielago", 1));
    	enemigos.add(new Enemigo(6300, 570, 30, 600, "murcielago", 1));
    	enemigos.add(new Enemigo(6800, 250, 6700, 7100, "esqueleto", 1));
    	enemigos.add(new Enemigo(7400, 450, 7300, 7450, "esqueleto", 1));
    }
    
    private void cargarPlataformasNivel1()
    {
    	plataformas.add(new Plataforma("pasto", 1300, 570, 5));
    	plataformas.add(new Plataforma("pasto", 1600, 450, 4));
    	plataformas.add(new Plataforma("pasto", 2000, 600, 3));
    	plataformas.add(new Plataforma("pasto", 2200, 400, 3));
    	plataformas.add(new Plataforma("pasto", 2400, 500, 3));
    	plataformas.add(new Plataforma("pasto", 0, 650, 65));
    	plataformas.add(new Plataforma("pasto", 3300, 650, 4));
    	plataformas.add(new Plataforma("pasto", 3600, 600, 4));
    	plataformas.add(new Plataforma("pasto", 3900, 550, 4));
    	plataformas.add(new Plataforma("pasto", 4600, 460, 8));
    	plataformas.add(new Plataforma("pasto", 4500, 570, 16));
    	plataformas.add(new Plataforma("pasto", 4200, 650, 38));
    	plataformas.add(new Plataforma("pasto", 6170, 350, 2));
    	plataformas.add(new Plataforma("pasto", 6100, 550, 4));
    	plataformas.add(new Plataforma("pasto", 6430, 250, 2));
    	plataformas.add(new Plataforma("pasto", 6400, 450, 4));
    	plataformas.add(new Plataforma("pasto", 6700, 250, 10));
    	plataformas.add(new Plataforma("pasto", 7300, 450, 4));
    	plataformas.add(new Plataforma("pasto", 7600, 650, 50));
    }

    private void cargarNivel2() {
        enemigos.clear();
        plataformas.clear();
        derecha = false;
        izquierda = false;
        jugador.setX(100);
        jugador.setY(sueloNivel2 - jugador.getHeight());
        camaraX = 0;
        
        cargarEnemigosNivel2();
        cargarPlataformasNivel2();

        // Activar diálogo del nivel 2
        estado = Estado.DIALOGO;
        lineaActualNivel2 = 0;

     // Detener sonidos del castillo si hubiera
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) {
            sonidosCastilloThread.interrupt();
        }
        // Asegurarse de que el hilo de bosque no esté ya corriendo antes de iniciarlo
        if (sonidosBosqueThread == null || !sonidosBosqueThread.isAlive()) {
            iniciarSonidosBosque();
        }
    }
    
    private void cargarEnemigosNivel2()
    {
        enemigos.add(new Enemigo(1300, sueloNivel2, 1300, 1450, "bandido", 1));
        enemigos.add(new Enemigo(1450, sueloNivel2, 1370, 1530, "bandido", 1));
        enemigos.add(new Enemigo(1950, sueloNivel2, 1900, 2000, "bandido", 1));
        enemigos.add(new Enemigo(2050, sueloNivel2, 2000, 2100, "bandido", 1));
        enemigos.add(new Enemigo(2150, sueloNivel2, 2100, 2200, "bandido", 1));
        enemigos.add(new Enemigo(2420, 100, 1, 300, "murcielago", 1));
        enemigos.add(new Enemigo(2530, 200, 1, 250, "murcielago", 1));
        enemigos.add(new Enemigo(2450, 500, 2420, 2580, "bandido", 1));
        enemigos.add(new Enemigo(2700, sueloNivel2, 2550, 2850, "bandido", 1));
        enemigos.add(new Enemigo(3000, sueloNivel2, 2850, 3150, "bandido", 1));
        enemigos.add(new Enemigo(3300, sueloNivel2, 3150, 3450, "bandido", 1));
        enemigos.add(new Enemigo(3800, sueloNivel2, 3650, 3850, "bandido", 1));
        enemigos.add(new Enemigo(3900, sueloNivel2, 3850, 4050, "bandido", 1));
        enemigos.add(new Enemigo(4400, sueloNivel2, 4200, 4600, "bandido", 1));
        enemigos.add(new Enemigo(4450, sueloNivel2, 4250, 4650, "bandido", 1));
        enemigos.add(new Enemigo(4500, sueloNivel2, 4300, 4700, "bandido", 1));
        enemigos.add(new Enemigo(5200, 600, 5150, 5350, "bandido", 1));
        enemigos.add(new Enemigo(5500, 600, 5450, 5900, "bandido", 1));
        enemigos.add(new Enemigo(5950, 600, 5900, 6100, "bandido", 1));
        enemigos.add(new Enemigo(5800, 520, 5700, 5950, "bandido", 1));
        enemigos.add(new Enemigo(6700, 650, 6500, 6900, "bandido", 1));
        enemigos.add(new Enemigo(7100, 650, 6900, 7300, "bandido", 1));
        enemigos.add(new Enemigo(8200, 240, 8150, 8270, "bandido", 1));
        enemigos.add(new Enemigo(8500, 420, 8450, 8620, "bandido", 1));
        enemigos.add(new Enemigo(9700, sueloNivel2, 9650, 9750, "bandido", 1));
        enemigos.add(new Enemigo(9950, 620, 9850, 10000, "esqueleto", 1));
        enemigos.add(new Enemigo(10100, sueloNivel2, 10050, 10250, "bandido", 1));
        enemigos.add(new Enemigo(11550, 350, 310, 410, "murcielago", 1));
        enemigos.add(new Enemigo(11650, 360, 320, 410, "murcielago", 1));
        enemigos.add(new Enemigo(11750, 370, 330, 410, "murcielago", 1));
        enemigos.add(new Enemigo(11850, 380, 340, 410, "murcielago", 1));
        enemigos.add(new Enemigo(11950, 390, 350, 450, "murcielago", 1));
        enemigos.add(new Enemigo(12050, 400, 360, 450, "murcielago", 1));
        enemigos.add(new Enemigo(12150, 410, 370, 450, "murcielago", 1));
        enemigos.add(new Enemigo(12250, 420, 380, 450, "murcielago", 1));
        enemigos.add(new Enemigo(12350, 430, 390, 490, "murcielago", 1));
        enemigos.add(new Enemigo(12450, 440, 400, 490, "murcielago", 1));
        enemigos.add(new Enemigo(12550, 450, 410, 490, "murcielago", 1));
        enemigos.add(new Enemigo(12650, 460, 420, 490, "murcielago", 1));
    }

    private void cargarPlataformasNivel2()
    {
    	plataformas.add(new Plataforma("pasto", 2400, 500, 5));
    	plataformas.add(new Plataforma("pasto", 2670, 350, 2));
    	plataformas.add(new Plataforma("pasto", 3850, 540, 3));
    	plataformas.add(new Plataforma("pasto", 0, 650, 105));
    	plataformas.add(new Plataforma("pasto", 5150, 600, 5));
    	plataformas.add(new Plataforma("pasto", 5700, 520, 6));
    	plataformas.add(new Plataforma("pasto", 5500, 600, 13));
    	plataformas.add(new Plataforma("pasto", 6300, 650, 22));
    	plataformas.add(new Plataforma("pasto", 7500, 250, 1));
    	plataformas.add(new Plataforma("pasto", 7700, 225, 1));
    	plataformas.add(new Plataforma("pasto", 7900, 200, 1));
    	plataformas.add(new Plataforma("pasto", 7450, 400, 2));
    	plataformas.add(new Plataforma("pasto", 7650, 425, 2));
    	plataformas.add(new Plataforma("pasto", 7850, 450, 2));
    	plataformas.add(new Plataforma("pasto", 7400, 650, 3));
    	plataformas.add(new Plataforma("pasto", 7600, 625, 3));
    	plataformas.add(new Plataforma("pasto", 7800, 600, 3));
    	plataformas.add(new Plataforma("pasto", 8150, 240, 4));
    	plataformas.add(new Plataforma("pasto", 8450, 420, 5));
    	plataformas.add(new Plataforma("pasto", 9850, 620, 4));
    	plataformas.add(new Plataforma("pasto", 8800, 650, 48));
    	plataformas.add(new Plataforma("pasto", 11200, 500, 2));
    	plataformas.add(new Plataforma("pasto", 11400, 350, 2));
    	plataformas.add(new Plataforma("pasto", 12750, 650, 60));
    }
    
    private void cargarNivel3() {
    	sonidosBosqueThread.interrupt();
    	sonidosCastillo();
        enemigos.clear();
        plataformas.clear();
        derecha = false;
        izquierda = false;
        jugador.setX(100);
        jugador.setY(sueloNivel3 - jugador.getHeight());
        camaraX = 0;
        
        cargarEnemigosNivel3();

        estado = Estado.DIALOGO; 
        lineaActualBatalla = 0;
        batallaDialogoMostrado = true;
    }

    
    private void cargarEnemigosNivel3()
    {
    	enemigos.add(new Enemigo(1300, sueloNivel3, 1250, 1350, "caballero", 1));
    	enemigos.add(new Enemigo(1360, sueloNivel3, 1350, 1450, "esqueleto", 1));
    	enemigos.add(new Enemigo(1500, sueloNivel3, 1450, 1550, "esqueleto", 1));
    	enemigos.add(new Enemigo(1600, sueloNivel3, 1550, 1625, "esqueleto", 1));
    	enemigos.add(new Enemigo(1800, sueloNivel3, 1700, 1900, "caballero", 1));
    	enemigos.add(new Enemigo(2000, 300, 1, 300, "sierra", 1));
    	enemigos.add(new Enemigo(2350, sueloNivel3, 2340, 2400, "caballero", 1));
    	enemigos.add(new Enemigo(2500, sueloNivel3, 2400, 2600, "bandido", 1));
    	enemigos.add(new Enemigo(2600, sueloNivel3, 2500, 2700, "caballero", 1));
    	enemigos.add(new Enemigo(2700, sueloNivel3, 2600, 2800, "bandido", 1));
    	enemigos.add(new Enemigo(3000, sueloNivel3, 2910, 3090, "bandido", 1));
    	enemigos.add(new Enemigo(3120, sueloNivel3, 3090, 3190, "caballero", 1));
    	enemigos.add(new Enemigo(3300, sueloNivel3, 3200, 3400, "caballero", 1));
        enemigos.add(new Enemigo(3600, 300, 1, 300, "sierra", 1));
        enemigos.add(new Enemigo(3950, 300, 1, 300, "sierra", 1));
        enemigos.add(new Enemigo(4300, 300, 1, 300, "sierra", 1));
        enemigos.add(new Enemigo(4700, sueloNivel3, 4650, 4750, "caballero", 1));
        enemigos.add(new Enemigo(4900, 300, 1, 300, "sierra", 1));
        enemigos.add(new Enemigo(5500, sueloNivel3, 5300, 5650, "esqueleto", 1));
    }

    public static void reproducirEfecto(String nombreArchivo) {
        try {
            // Cargar el archivo de sonido
            File archivo = new File("src/media/" + nombreArchivo + ".wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(archivo);

            // Crear el clip
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Reproducir desde el inicio
            clip.setFramePosition(0);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public void reproducirMusica(String ruta) {
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
            String[] efectos = {"/media/bird1.wav", "/media/bird2.wav", "/media/bird3.wav"};
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

 // En tu clase SuperPablo.java
    public void sonidosCastillo() {
        // Detener cualquier hilo previo y su sonido
        if (sonidoActual != null && sonidoActual.isRunning()) {
            sonidoActual.stop();
        }
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) {
            sonidosCastilloThread.interrupt();
        }
        if (sonidosBosqueThread != null && sonidosBosqueThread.isAlive()) {
            sonidosBosqueThread.interrupt();
        }

        sonidosCastilloThread = new Thread(() -> {
            String[] efectos = {"/media/castilloNoche.wav"};
            Random rand = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int i = rand.nextInt(efectos.length);
                    reproducirSonido(efectos[i]); // <--- Este método ahora maneja la detención del clip anterior
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
                if (sonidoActual != null && sonidoActual.isRunning()) {
                    sonidoActual.stop();
                }
                sonidoActual = clip; // Asigna el nuevo clip
                sonidoActual.start();
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

        boolean moverCamara = false;

        // --- decidir si se mueve cámara ---
        if (nivelActual == 1) {
            if (camaraX < 7750) { // si no llegamos al tope
                if ((jugador.getX() > 400 && dx > 0) || (jugador.getX() < 400 && dx < 0 && camaraX > 0)) {
                    moverCamara = true;
                }
            }
        } else if(nivelActual == 2){
            if ((jugador.getX() > 400 && dx > 0) || (jugador.getX() < 400 && dx < 0 && camaraX > 0)) {
                moverCamara = true;
            }
        } else if(nivelActual == 3)
        {
        	if(camaraX < 5700) {
        	if ((jugador.getX() > 400 && dx > 0) || (jugador.getX() < 400 && dx < 0 && camaraX > 0)) {
                moverCamara = true;
            }
        	}
        }
        
        if (moverCamara) {
            camaraX += dx;
            if (camaraX < 0) camaraX = 0;
            if (nivelActual == 1 && camaraX > 10000) camaraX = 10000;
            if (nivelActual == 3 && camaraX > 5700) camaraX = 5700;

            if (nivelActual == 1) {
                fondoNivel1.update(dx);
            } else if (nivelActual == 2) {
                fondoNivel2.update(dx, 15000);
            } else if (nivelActual == 3) {
                fondoNivel3.update(dx, camaraX, dx);
            }

            if (camaraX >= 14000  && nivelActual == 2) {
                nivelActual = 3;
                cargarNivel3();
            }
            
            if(camaraX >= 6800 && nivelActual == 3)
			{
				estado=Estado.WIN;
			}


        } else {
            // si no movemos cámara, mover jugador directamente
            jugador.mover(dx);
            
            // cambio de nivel si supera tope total
            if (jugador.getX() >= 1200 && nivelActual == 1) {
                nivelActual = 2;
                cargarNivel2();
            }
        }

        if (saltando) {
            jugador.saltar();
            saltando = false;
        }

        jugador.update(izquierda || derecha);

        // Colisión para el nivel 2
        if (nivelActual == 2) {

            if (!vendedorDialogoMostrado && camaraX >= 12800) {
                estado = Estado.DIALOGO;
                lineaActualVendedor = 0;
                vendedorDialogoMostrado = true;
                
            }

            if (camaraX >= 13500) {
                nivelActual = 3;
                cargarNivel3();
            }
        }


        if (jugador.getX() + camaraX > 5700 && nivelActual == 3 && !wilsonCreado) {
        	wilson = new Wilson(camaraX + 1450, 542, 150, 160);
        	wilsonCreado=true;
        }
        
        if (nivelActual == 3) {
            if (jugador.getY() + jugador.getHeight() >= sueloNivel3) {
                jugador.ajustarSobrePlataforma(sueloNivel3);
            }
        }

        if(wilsonCreado && wilson.estaVivo())
        {
        	wilson.update();
        	colisionWilson();
        	colisionProyectiles();
        	if (fase == 0 && wilson.getVida() <= 10) {
        		fase = 1;
        		reproducirEfecto("tp");
        	}
        	else if (fase == 1 && wilson.getVida() <= 9) {
        		fase = 2;
        		reproducirEfecto("tp");
        	}
        	else if (fase == 2 && wilson.getVida() <= 8) {
        		wilson.habilitar(false);
        	    fase = 3;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	else if (fase == 3 && wilson.getVida() <= 7) {
        		wilson.habilitar(false);
        	    fase = 4;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	else if (fase == 4 && wilson.getVida() <= 6) {
        		wilson.habilitar(false);
        	    fase = 5;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	else if (fase == 5 && wilson.getVida() <= 5) {
        		wilson.habilitar(false);
        	    fase = 6;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}if (fase == 6 && wilson.getVida() <= 4) {
        		wilson.habilitar(false);
        	    fase = 7;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	else if (fase == 7 && wilson.getVida() <= 3) {
        		wilson.habilitar(false);
        	    fase = 8;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	else if (fase == 8 && wilson.getVida() <= 2) {
        		wilson.habilitar(false);
        	    fase = 9;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	else if (fase == 9 && wilson.getVida() <= 1) {
        		wilson.habilitar(false);
        	    fase = 10;
        	    wilson.invocarEnemigos(camaraX, sueloNivel3, fase, enemigosJ);
        	    reproducirEfecto("tp");
        	}
        	
        }
        colisionEnemigo();
        
        int contFuera = 0;
        int contDebajo = 0;

        for (Plataforma p : plataformas) {
            for (int i = 0; i < p.cantidad; i++) {
                Rectangle boundsPlataforma = p.getBounds(i);
                Rectangle boundsJugador = jugador.getBounds();

                // --- Cae desde arriba ---
                boolean caeDesdeArriba =
                    (boundsJugador.x + jugador.getWidth() > boundsPlataforma.x - camaraX) &&
                    (boundsJugador.x < boundsPlataforma.x + boundsPlataforma.getWidth() - camaraX) &&
                    (boundsJugador.y + jugador.getHeight() >= boundsPlataforma.y - 5) &&
                    (boundsJugador.y + jugador.getHeight() <= boundsPlataforma.y + jugador.getVelocidadY()) &&
                    (jugador.getVelocidadY() > 0);

                if (caeDesdeArriba) {
                    jugador.ajustarSobrePlataforma(p.y);
                    reproducirEfecto("caida");
                }

                // --- Chequear si estaba abajo ---
                if (Math.abs(boundsJugador.y + jugador.getHeight() - boundsPlataforma.y) <= 2) {
                    contDebajo++;
                    boolean caminarFuera =
                        (boundsJugador.x + jugador.getWidth() <= boundsPlataforma.x - camaraX ||
                         boundsJugador.x >= boundsPlataforma.x + boundsPlataforma.getWidth() - camaraX);
                    if (caminarFuera) {
                        contFuera++;
                    }
                }
            }
        }

        // Si estaba en el nivel de alguna plataforma pero todas las dejó, cae
        if (contDebajo > 0 && contFuera == contDebajo && jugador.getVelocidadY() == 0) {
            jugador.setVelocidadY(1);
        }
        

    }
    
    private void colisionProyectiles() {
        Rectangle boundsJugador = jugador.getBounds();
        boundsJugador.x += camaraX; 

        for (Proyectil p : wilson.getProyectiles()) {
            if (boundsJugador.intersects(p.getBounds())) {
                jugador.recibirDanio();
                p.destruir();
            }
        }
    }
    
    private void colisionWilson() {
        if (wilson == null || !wilson.estaVivo()) return;

            Rectangle boundsJugador = jugador.getBounds();
            boundsJugador.x += camaraX; 

            Rectangle boundsEnemigo = wilson.getBounds(); 
            
            if (
            	    (boundsJugador.x + jugador.getWidth() > boundsEnemigo.x) &&
            	    (boundsJugador.x < boundsEnemigo.x + wilson.getWidth()) &&
            	    (boundsJugador.y + jugador.getHeight() >= boundsEnemigo.y - 5) &&
            	    (boundsJugador.y + jugador.getHeight() <= boundsEnemigo.y + jugador.getVelocidadY()) && 
            	    (jugador.getVelocidadY() > 0) && wilson.estaVivo() && wilson.isActivo()
            	) {
            	    wilson.recibirDanio();
            	    jugador.rebotar();
            	    puntos += 100;
            	}

            else if (
            		(boundsJugador.x + jugador.getWidth() > boundsEnemigo.x &&
            		 boundsJugador.x < boundsEnemigo.x + wilson.getWidth()) &&  
            	    (boundsJugador.y + jugador.getHeight() > boundsEnemigo.y &&
            	     boundsJugador.y < boundsEnemigo.y + wilson.getHeight()) &&  
            	     wilson.estaVivo()
               ) {
                jugador.recibirDanio();
            }
            
            if(!jugador.estaVivo())
            {
            	estado=Estado.GAMEOVER;
            }
            
            if(!wilson.estaVivo()) {
            	estado=Estado.WIN;
            }
            
        }

    
    private void colisionEnemigo()
    {
        int enemigosVivos=0;
    	for (Enemigo e : enemigos) {
            e.update(); // mueve al enemigo

            Rectangle boundsJugador = jugador.getBounds();
            boundsJugador.x += camaraX; 

            Rectangle boundsEnemigo = e.getBounds(); 
            
            if (
            	    (boundsJugador.x + jugador.getWidth() > boundsEnemigo.x) &&
            	    (boundsJugador.x < boundsEnemigo.x + e.getWidth()) &&
            	    (boundsJugador.y + jugador.getHeight() >= boundsEnemigo.y - 5) &&
            	    (boundsJugador.y + jugador.getHeight() <= boundsEnemigo.y + jugador.getVelocidadY()) && 
            	    (jugador.getVelocidadY() > 0) && e.estaVivo()
            	) {
            	    e.recibirDanio();
            	    if(dobleDanio) {
            	    	e.recibirDanio();
            	    }
            	    reproducirEfecto("danioEnem");
            	    jugador.rebotar();
            	    if (!e.estaVivo()) puntos += e.getPuntos();
            	}

            else if (
            		(boundsJugador.x + jugador.getWidth() > boundsEnemigo.x &&
            		 boundsJugador.x < boundsEnemigo.x + e.getWidth()) &&  
            	    (boundsJugador.y + jugador.getHeight() > boundsEnemigo.y &&
            	     boundsJugador.y < boundsEnemigo.y + e.getHeight()) &&  
            	     e.estaVivo()
               ) {
                jugador.recibirDanio();
            }
            
            if(!jugador.estaVivo())
            {
            	estado=Estado.GAMEOVER;
            }
        }
    	
    	for (Enemigo e : enemigosJ) {
            e.update(); // mueve al enemigo
            Rectangle boundsJugador = jugador.getBounds();
            boundsJugador.x += camaraX; 

            Rectangle boundsEnemigo = e.getBounds(); 
            
            if (
            	    (boundsJugador.x + jugador.getWidth() > boundsEnemigo.x) &&
            	    (boundsJugador.x < boundsEnemigo.x + e.getWidth()) &&
            	    (boundsJugador.y + jugador.getHeight() >= boundsEnemigo.y - 5) &&
            	    (boundsJugador.y + jugador.getHeight() <= boundsEnemigo.y + jugador.getVelocidadY()) && 
            	    (jugador.getVelocidadY() > 0) && e.estaVivo()
            	) {
            	    e.recibirDanio();
            	    if(dobleDanio) {
            	    	e.recibirDanio();
            	    }
            	    reproducirEfecto("danioEnem");
            	    jugador.rebotar();
            	    if (!e.estaVivo()) puntos += e.getPuntos();
            	}

            else if (
            		(boundsJugador.x + jugador.getWidth() > boundsEnemigo.x &&
            		 boundsJugador.x < boundsEnemigo.x + e.getWidth()) &&  
            	    (boundsJugador.y + jugador.getHeight() > boundsEnemigo.y &&
            	     boundsJugador.y < boundsEnemigo.y + e.getHeight()) &&  
            	     e.estaVivo()
               ) {
                jugador.recibirDanio();
            }
            
            if (e.estaVivo()) {
                enemigosVivos++;
            }
            
            if(!jugador.estaVivo())
            {
            	estado=Estado.GAMEOVER;
            }
            
        }
    		if(enemigosVivos==0 && wilsonCreado)
    		{
    			wilson.habilitar(true);
    		}
    }

    private void reiniciarNivel() {
    	estado=Estado.JUGANDO;
    	jugador.reiniciarVidas();
        jugador = new Jugador(100, 591);
        jugador.reiniciarVidas();
        camaraX = 0;
        puntos = 0;
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
                g.drawString("Presiona cualquier tecla para comenzar", 25, 603);
                g.drawString("Presiona cualquier tecla para comenzar", 25, 602);
                g.drawString("Presiona cualquier tecla para comenzar", 25, 601);
                g.setColor(Color.WHITE);
                g.drawString("Presiona cualquier tecla para comenzar", 25, 600);
                break;

            case DIALOGO:
                if (nivelActual == 2) {
                    // Dibujar fondo del nivel 2
                    fondoNivel2.draw(g, 15000, 700);
                   
                    for (Plataforma p : plataformas) {
                        p.draw(g, camaraX);
                    }
                    
                    if (vendedorDialogoMostrado) {
                        // Diálogo del vendedor
                    	g.drawImage(vendedor, 0, 250, 500, 450, null); //medidas vendedor
                        g.setColor(new Color(0,0,0,180));
                        g.fillRoundRect(420, 50, 800, 150, 20, 20);
                        g.setColor(Color.WHITE);
                        g.drawRoundRect(420, 50, 800, 150, 20, 20);

                        if (miFuenteDialogo != null) g.setFont(miFuenteDialogo);
                        g.drawString(dialogoVendedor[lineaActualVendedor], 440, 100);

                        g.setFont(new Font("Arial", Font.ITALIC, 16));
                        g.drawString("Presiona ENTER para continuar", 440, 170);
                        if(lineaActualVendedor == 4) {
                        	poderElegir = true;
                        }
                    } else {
                        // Diálogo normal del nivel 2
                    	g.drawImage(mago, 50, 250, 337, 450, null);
                        g.setColor(new Color(0,0,0,180));
                        g.fillRoundRect(420, 50, 800, 150, 20, 20);
                        g.setColor(Color.WHITE);
                        g.drawRoundRect(420, 50, 800, 150, 20, 20);

                        if (miFuenteDialogo != null) g.setFont(miFuenteDialogo);
                        g.drawString(dialogoNivel2[lineaActualNivel2], 440, 100);

                        g.setFont(new Font("Arial", Font.ITALIC, 16));
                        g.drawString("Presiona ENTER para continuar", 440, 170);
                    }
                    

                } 
                else if (nivelActual == 3 && batallaDialogoMostrado) {
                    // Fondo del castillo (puede ser tu fondoNivel3)
                    fondoNivel3.draw(g, camaraX);

                    // Mostrar Walter y Wilson
                    g.drawImage(mago, 50, 250, 337, 450, null);     // Walter
                    g.drawImage(new ImageIcon("src/media/wilson.png").getImage(), 800, 210, 500, 500, null); // Wilson

                    g.setColor(new Color(0,0,0,180));
                    g.fillRoundRect(420, 50, 800, 150, 20, 20);
                    g.setColor(Color.WHITE);
                    g.drawRoundRect(420, 50, 800, 150, 20, 20);

                    if (miFuenteDialogo != null) g.setFont(miFuenteDialogo);
                    g.drawString(dialogoBatalla[lineaActualBatalla], 440, 100);

                    g.setFont(new Font("Arial", Font.ITALIC, 16));
                    g.drawString("Presiona ENTER para continuar", 440, 170);
                }
                else {
                    // Diálogo de Walter u otros niveles
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
                }
                
                break;

                

            case JUGANDO:
                if (nivelActual == 1) {
                    fondoNivel1.draw(g, nivelActual);
                } else if (nivelActual == 2) {
                    fondoNivel2.draw(g, 15000, 700);
                } else {
                	fondoNivel3.draw(g, camaraX);
                }
                
                for (Plataforma p : plataformas) {
                    p.draw(g, camaraX);
                }
                
                cont += 1; 
                if(cont % 20 == 0) {
                	paso *= -1;
                }

                for (Enemigo e : enemigos) {
                    e.draw(g, camaraX, paso);
                }
                
                for (Enemigo e : enemigosJ) {
                    e.draw(g, camaraX, paso);
                }
                
                
                if (nivelActual == 3 && wilson != null) {
                	wilson.draw(g, camaraX);
                }

                
                jugador.draw(g);

                g.setColor(Color.BLACK);
                g.setFont(miFuente);
                g.drawString("Puntos: " + puntos, 25, 52);
                g.drawString("Puntos: " + puntos, 25, 51);
                g.drawString("Puntos: " + puntos, 25, 50);
                g.setColor(Color.YELLOW);
                g.drawString("Puntos: " + puntos, 25, 49);
                
                g.setColor(Color.BLACK);
                g.setFont(miFuente);
                g.drawString("Vidas: " + jugador.getVida(), 1110, 52);
                g.drawString("Vidas: " + jugador.getVida(), 1110, 51);
                g.drawString("Vidas: " + jugador.getVida(), 1110, 50);
                g.setColor(Color.RED);
                g.drawString("Vidas: " + jugador.getVida(), 1110, 49);
                
                break;

            case PAUSA:
                if (miFuente != null) g.setFont(miFuente);
                g.setColor(Color.BLACK);
                g.drawString("PAUSA", getWidth() / 2 - 80, getHeight() / 2 - 47);
                g.drawString("PAUSA", getWidth() / 2 - 80, getHeight() / 2 - 48);
                g.drawString("PAUSA", getWidth() / 2 - 80, getHeight() / 2 - 49);
                g.setColor(Color.WHITE);
                g.drawString("PAUSA", getWidth() / 2 - 80, getHeight() / 2 - 50);
                
                g.setColor(Color.BLACK);
                g.drawString("ESC - Continuar", getWidth() / 2 - 150, getHeight() / 2 + 23);
                g.drawString("ESC - Continuar", getWidth() / 2 - 150, getHeight() / 2 + 22);
                g.drawString("ESC - Continuar", getWidth() / 2 - 150, getHeight() / 2 + 21);
                g.setColor(Color.WHITE);
                g.drawString("ESC - Continuar", getWidth() / 2 - 150, getHeight() / 2 + 20);
                
                g.setColor(Color.BLACK);
                g.drawString("Q - Salir", getWidth() / 2 - 90, getHeight() / 2 + 63);
                g.drawString("Q - Salir", getWidth() / 2 - 90, getHeight() / 2 + 62);
                g.drawString("Q - Salir", getWidth() / 2 - 90, getHeight() / 2 + 61);
                g.setColor(Color.WHITE);
                g.drawString("Q - Salir", getWidth() / 2 - 90, getHeight() / 2 + 60);
                break;
                
            case GAMEOVER:
            	g.setColor(Color.BLACK);
            	g.fillRect(0, 0, getWidth(), getHeight());
            	g.setColor(Color.RED);
            	g.setFont(perderFuente);
            	g.drawString("HAS PERDIDO", getWidth()/2 - 188, getHeight()/2 - 40);
            	g.setColor(Color.WHITE);
            	g.setFont(miFuente);
            	g.drawString("Puntaje: " + puntos, getWidth()/2 - 100, getHeight()/2 + 25);
            	g.drawString("- Presione R para volver a jugar -", getWidth()/2 - 272, getHeight()/2 + 80);
            	break;
            	
            case WIN:
            	g.setColor(Color.BLACK);
            	g.fillRect(0, 0, getWidth(), getHeight());
            	g.setColor(Color.YELLOW);
            	g.setFont(perderFuente);
            	g.drawString("HAS GANADO", getWidth()/2 - 188, getHeight()/2 - 40);
            	g.setColor(Color.WHITE);
            	g.setFont(miFuente);
            	g.drawString("Puntaje: " + puntos, getWidth()/2 - 100, getHeight()/2 + 25);
            	g.drawString("- Presione R para volver a jugar -", getWidth()/2 - 272, getHeight()/2 + 80);
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
            derecha = false;
        	izquierda = false;
            return;
        }

        if (estado == Estado.INICIO) {
            estado = Estado.DIALOGO;
            if (musicaInicio != null && musicaInicio.isRunning()) {
                musicaInicio.stop();
                musicaInicio.close();
            }
        } else if (estado == Estado.DIALOGO) {
            if (nivelActual == 2) {
                if (vendedorDialogoMostrado) { // diálogo del vendedor
                    if (key == KeyEvent.VK_ENTER) {
                        lineaActualVendedor++;
                        if (lineaActualVendedor >= dialogoVendedor.length) {
                            estado = Estado.JUGANDO;
                            izquierda = false;
                            derecha = false;
                        }
                    }
                } else { // diálogo normal del nivel 2
                    if (key == KeyEvent.VK_ENTER) {
                        lineaActualNivel2++;
                        if (lineaActualNivel2 >= dialogoNivel2.length) {
                            estado = Estado.JUGANDO;
                            reproducirMusicaBosque("/media/wind.wav");
                            iniciarSonidosBosque();
                        }
                    }
                }
            } 
            
            // >>>>> ACA VA EL DIALOGO DE WILSON <<<<<
            else if (nivelActual == 3 && batallaDialogoMostrado) {
                if (key == KeyEvent.VK_ENTER) {
                    lineaActualBatalla++;
                    if (lineaActualBatalla >= dialogoBatalla.length) {
                        estado = Estado.JUGANDO;
                        batallaDialogoMostrado = false;
                    }
                }
            }
            
            else { // diálogo inicial u otros niveles
                if (key == KeyEvent.VK_ENTER) {
                    lineaActual++;
                    if (lineaActual >= dialogo.length) {
                        estado = Estado.JUGANDO;
                        reproducirMusica("/media/cancion.wav");
                        iniciarSonidosBosque();
                    }
                }
            }
        }
        else if (estado == Estado.JUGANDO) {
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) izquierda = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) derecha = true;
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) saltando = true;

            /*if (key == KeyEvent.VK_N) {
                nivelActual = 2;
                cargarNivel2();
                System.out.println("Nivel 2 activado");
            }
            if (key == KeyEvent.VK_T) {
                nivelActual = 3;
                cargarNivel3();
                System.out.println("Nivel 3 activado");
            }*/
            if (key == KeyEvent.VK_1 && !elegido && poderElegir) {
                jugador.curar();
                elegido = true;
                reproducirEfecto("pocion");
            }
            if (key == KeyEvent.VK_2 && !elegido && poderElegir) {
            	dobleDanio = true;
            	elegido = true;
            	reproducirEfecto("pocion");
            }

        } else if (estado == Estado.PAUSA) {
            if (key == KeyEvent.VK_Q) System.exit(0);
        } 
        else if (estado == Estado.GAMEOVER)
        {
        	if (key == KeyEvent.VK_R) reiniciarNivel(); 
        	izquierda = false; 
        	derecha = false; 
        }else if (estado == Estado.WIN)
        {
        	if (key == KeyEvent.VK_R) reiniciarNivel(); 
        	izquierda = false; 
        	derecha = false; 
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