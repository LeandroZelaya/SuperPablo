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
    private int nivelActual = 2;
    private Fondo fondoNivel1;
    private Fondo2 fondoNivel2;
    private Fondo3 fondoNivel3;
    
    private int puntos;

    public static boolean izquierda = false;
    public static boolean derecha = false;
    private boolean saltando = false;

    private int camaraX = 0;
    
    private int cont = 0;
    private int paso = 1;

    private enum Estado { INICIO, DIALOGO, JUGANDO, PAUSA, GAMEOVER, WIN }
    private Estado estado = Estado.INICIO;

    private Image inicioFondo;
    private Image dialogoFondo;
    private Image mago;
    private Image vendedor; 

    private Thread sonidosBosqueThread;
    private Thread sonidosCastilloThread;

    private Font miFuente;
    private Font perderFuente;
    private Font miFuenteDialogo;

    private Clip musicaInicio;
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
    	    "Sigue asi Pablo, ya estas cerca del castillo."
    	};

    	private int lineaActualNivel2 = 0;

    	private boolean vendedorDialogoMostrado = false;
    	private String[] dialogoVendedor = {
    	    "¡Hola, Pablo! Bienvenido a mi tienda.",
    	    "Soy Pancrasio, escuche que te diriges al castillo.",
    	    "Debes de tener cuidado, cosas malas pasan ahi.",
    	    "Ten esta posicion de vida de regalo",
    	    "Aumentara mas uno en tu vida",
    	    "¡Gracias por tu visita, y suerte en el castillo!"
    	};
    	private int lineaActualVendedor = 0;
    	
    	private String[] dialogoBatalla = {
    		    "Walter: ¡Por fin llegaste al castillo, Pablo!",
    		    "Wilson: ¡JAJA! No tan rápido, hermano...",
    		    "Walter: ¡Wilson, traidor! Nunca debiste volver.",
    		    "Wilson: Este reino será mío, y destruiré a tu aprendiz.",
    		    "Walter: Pablo, prepárate. Esta será la batalla más difícil.",
    		    "Wilson: ¡Muere caballero inútil!",
    		    "Walter: ¡Resiste Pablo, juntos lo venceremos!"
    		};
    		private int lineaActualBatalla = 0;
    		private boolean batallaDialogoMostrado = false;



    private java.util.List<Enemigo> enemigos = new ArrayList<>();
    private java.util.List<Plataforma> plataformas = new ArrayList<>();
    private int sueloNivel3 = 535; // píxeles desde arriba hasta el suelo del nivel 3
    private int sueloNivel2 = 635; 
    private int sueloNivel1 = 650;
    private Wilson wilson; 
    
    public SuperPablo() {
        setBackground(Color.CYAN);
        jugador = new Jugador(100, 591);
        setFocusable(true);
        addKeyListener(this);

        fondoNivel1 = new Fondo(1280);
     // En tu clase SuperPablo, al inicializar el fondo del nivel 2:
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

        reproducirMusicaInicio("/media/musicaInicio.wav");
        cargarNivel1();
    }

    private void cargarNivel1() {
        enemigos.clear();
        plataformas.clear();
        
        cargarEnemigosNivel1();
        cargarPlataformasNivel1();

        // Detener sonidos de niveles previos
        if (sonidosCastilloThread != null && sonidosCastilloThread.isAlive()) sonidosCastilloThread.interrupt();

        //iniciarSonidosBosque();
    }
    
    private void cargarEnemigosNivel1()
    {
    	enemigos.add(new Enemigo(600, sueloNivel1, 550, 800, "esqueleto"));
        enemigos.add(new Enemigo(800, sueloNivel1, 800, 950, "esqueleto"));
        enemigos.add(new Enemigo(950, sueloNivel1, 950, 1100, "esqueleto"));
        enemigos.add(new Enemigo(1050, sueloNivel1, 1000, 1150, "caballero"));
        enemigos.add(new Enemigo(1100, sueloNivel1, 1100, 1250, "esqueleto"));
        enemigos.add(new Enemigo(1150, sueloNivel1, 1150, 1250, "esqueleto"));
        enemigos.add(new Enemigo(1300, sueloNivel1, 1250, 1400, "caballero"));
        enemigos.add(new Enemigo(1300, sueloNivel1, 1300, 1450, "bandido"));
    }
    
    private void cargarPlataformasNivel1()
    {
    	plataformas.add(new Plataforma("pasto", 300, 550, 3));
    	plataformas.add(new Plataforma("pasto", 500, 530, 3));
    	plataformas.add(new Plataforma("pasto", 0, 650, 40));
    	plataformas.add(new Plataforma("pasto", 1940, 570, 2));
    	plataformas.add(new Plataforma("pasto", 2140, 550, 2));
    	plataformas.add(new Plataforma("pasto", 2340, 530, 2));
    	plataformas.add(new Plataforma("pasto", 2540, 550, 2));
    	plataformas.add(new Plataforma("pasto", 2740, 570, 2));
    	plataformas.add(new Plataforma("pasto", 2940, 650, 200));
    }

    private void cargarNivel2() {
        enemigos.clear();
        plataformas.clear();
        jugador.setX(100);
        jugador.setY(sueloNivel2 - jugador.getHeight() + 30);
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

        //iniciarSonidosBosque();
    }
    
    private void cargarEnemigosNivel2()
    {
    	enemigos.add(new Enemigo(600, sueloNivel2, 550, 800, "esqueleto"));
        enemigos.add(new Enemigo(800, sueloNivel2, 800, 950, "caballero"));
        enemigos.add(new Enemigo(950, sueloNivel2, 950, 1100, "bandido"));
        enemigos.add(new Enemigo(1050, sueloNivel2, 1000, 1150, "caballero"));
        enemigos.add(new Enemigo(1100, sueloNivel2, 1100, 1250, "esqueleto"));
        enemigos.add(new Enemigo(1150, sueloNivel2, 1150, 1250, "esqueleto"));
        enemigos.add(new Enemigo(1300, sueloNivel2, 1250, 1400, "bandido"));
        enemigos.add(new Enemigo(1300, sueloNivel2, 1300, 1450, "bandido"));
    }

    private void cargarPlataformasNivel2()
    {
    	plataformas.add(new Plataforma("pasto", 0, 650, 220));
    }
    
    private void cargarNivel3() {
        enemigos.clear();
        plataformas.clear();
        jugador.setX(100);
        jugador.setY(sueloNivel3 - jugador.getHeight());
        camaraX = 0;

        cargarEnemigosNivel3();
        cargarPlataformasNivel3();

        wilson = new Wilson(500, 532, 300, 900, 60, 80);

        // Crear Wilson

        estado = Estado.DIALOGO; 
        lineaActualBatalla = 0;
        batallaDialogoMostrado = true;
    }

    
    private void cargarEnemigosNivel3()
    {
    	enemigos.add(new Enemigo(600, sueloNivel3, 550, 800, "caballero"));
        enemigos.add(new Enemigo(800, sueloNivel3, 800, 950, "esqueleto"));
        enemigos.add(new Enemigo(950, sueloNivel3, 950, 1100, "caballero"));
        enemigos.add(new Enemigo(1050, sueloNivel3, 1000, 1150, "caballero"));
        enemigos.add(new Enemigo(1100, sueloNivel3, 1100, 1250, "bandido"));
        enemigos.add(new Enemigo(1150, sueloNivel3, 1150, 1250, "bandido"));
        enemigos.add(new Enemigo(1300, sueloNivel3, 1250, 1400, "bandido"));
        enemigos.add(new Enemigo(1300, sueloNivel3, 1300, 1450, "bandido"));
        
        
    }

    private void cargarPlataformasNivel3()
    {
    	// plataformicas
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
            String[] efectos = {"/media/fantasmaBostezo.wav", "/media/puertaCerrandose.wav", "/media/castilloNoche.wav"};
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
            if (camaraX < 10000) { // si no llegamos al tope
                if ((jugador.getX() > 400 && dx > 0) || (jugador.getX() < 400 && dx < 0 && camaraX > 0)) {
                    moverCamara = true;
                }
            }
        } else {
            // otros niveles (simulando lo que ya tenías)
            if ((jugador.getX() > 400 && dx > 0) || (jugador.getX() < 400 && dx < 0 && camaraX > 0)) {
                moverCamara = true;
            }
        }

        if (moverCamara) {
            camaraX += dx;
            if (camaraX < 0) camaraX = 0;
            if (nivelActual == 1 && camaraX > 10000) camaraX = 10000;

            if (nivelActual == 1) {
                fondoNivel1.update(dx);
            } else if (nivelActual == 2) {
                fondoNivel2.update(dx, 15000);
            } else if (nivelActual == 3) {
                fondoNivel3.update(dx, camaraX, dx);
            }

            if (camaraX >= 20000 && nivelActual == 2) {
                nivelActual = 3;
                cargarNivel3();
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
     // Dentro del método update(), después de mover al jugador
     // Dentro del método update()

        // Lógica de colisión para el nivel 2
        if (nivelActual == 2) {
            // Lógica de colisión para el Vendedor (ya existente)
            Rectangle marcadorVendedorRect = new Rectangle(
                fondoNivel2.getXMarcador(), 
                fondoNivel2.getYMarcador(), 
                fondoNivel2.getTamañoMarcador(), 
                fondoNivel2.getTamañoMarcador()
            );

            Rectangle jugadorRect = new Rectangle(
                jugador.getX() + camaraX,
                jugador.getY(),
                jugador.getWidth(),
                jugador.getHeight()
            );

            if (!vendedorDialogoMostrado && jugadorRect.intersects(marcadorVendedorRect)) {
                estado = Estado.DIALOGO;
                lineaActualVendedor = 0;
                vendedorDialogoMostrado = true;
            }

            // Lógica de colisión para la puerta del Castillo
            Rectangle marcadorCastilloRect = new Rectangle(
                fondoNivel2.getXMarcadorCastillo(),
                fondoNivel2.getYMarcadorCastillo(),
                fondoNivel2.getTamañoMarcadorCastillo(),
                fondoNivel2.getTamañoMarcadorCastillo()
            );

            // Comprueba la colisión con el nuevo marcador
            if (jugadorRect.intersects(marcadorCastilloRect)) {
                // Cambia de nivel aquí
                nivelActual = 3;
                cargarNivel3();
            }
        }


        
        if (nivelActual == 3 && wilson != null) {
            if (jugador.getY() + jugador.getHeight() >= sueloNivel3) {
                jugador.setY(sueloNivel3 - jugador.getHeight());
                jugador.setVelocidadY(0); // detener caída
            }

            // Actualizar a Wilson
            wilson.update();
            
         // Colisión proyectiles con jugador
            for (Proyectil p : wilson.getProyectiles()) {
                if (jugador.getBounds().intersects(p.getBounds())) {
                    jugador.recibirDanio();
                    p.destruir();
                }
            }


            // Detectar colisión entre jugador y Wilson
            Rectangle jugadorRect = jugador.getBounds();
            Rectangle wilsonRect = wilson.getBounds();

            if (jugadorRect.intersects(wilsonRect)) {
                // Colisión desde arriba (jugador cayendo sobre Wilson)
                if (jugador.getY() + jugador.getHeight() <= wilson.getY() + 10 && jugador.getVelocidadY() > 0) {
                    wilson.recibirDanio();  // Wilson pierde vida
                    jugador.rebotar();       // el jugador rebota
                    puntos += 10;            // sumar puntos
                } else {
                    // Colisión lateral o desde abajo: jugador recibe daño
                    jugador.recibirDanio();
                }
                 
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
                }

                // --- Chequear si estaba "debajo" ---
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

        // Si estaba en el nivel de alguna plataforma pero todas las dejó -> cae
        if (contDebajo > 0 && contFuera == contDebajo && jugador.getVelocidadY() == 0) {
            jugador.setVelocidadY(1);
        }

    }
    private void colisionWilson() {
        if (wilson == null || !wilson.estaVivo()) return;

        Rectangle jugadorRect = jugador.getBounds();
        Rectangle wilsonRect = wilson.getBounds();

        if (jugadorRect.intersects(wilsonRect)) {
            // Colisión desde arriba (golpeando a Wilson)
            if (jugador.getY() + jugador.getHeight() <= wilson.getY() + 10 && jugador.getVelocidadY() > 0) {
                wilson.recibirDanio();
                jugador.rebotar();
                puntos += 10;
            } else { 
                // Colisión lateral o desde abajo: daño al jugador
                jugador.recibirDanio();
            }
        }

        if (!jugador.estaVivo()) {
            estado = Estado.GAMEOVER;
        }
    }

    
    private void colisionEnemigo()
    {
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
    }

    private void reiniciarNivel() {
    	estado=Estado.JUGANDO;
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

                    
                   /* Rectangle marcadorRect = new Rectangle(
                        fondoNivel2.getXMarcador() - camaraX, // ajustar con cámara
                        fondoNivel2.getYMarcador(),
                        fondoNivel2.getTamañoMarcador(),
                        fondoNivel2.getTamañoMarcador()
                    );
                    g.setColor(Color.MAGENTA);
                    g.drawRect(marcadorRect.x, marcadorRect.y, marcadorRect.width, marcadorRect.height);*/
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
            	g.drawString("HAS PERDIDO", getWidth()/2 - 188, getHeight()/2);
            	g.setColor(Color.RED);
            	g.setFont(miFuente);
            	g.drawString("- Presione R para volver a jugar -", getWidth()/2 - 272, getHeight()/2 + 60);
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

                        // Arranca la música de pelea contra Wilson
                        //reproducirMusicaBosque("/media/bossfight.wav");

                        // Llamar a tu lógica de pelea con Wilson
                        iniciarPeleaConWilson();
                    }
                }
            }
            
            else { // diálogo inicial u otros niveles
                if (key == KeyEvent.VK_ENTER) {
                    lineaActual++;
                    if (lineaActual >= dialogo.length) {
                        estado = Estado.JUGANDO;
                        reproducirMusicaBosque("/media/wind.wav");
                        iniciarSonidosBosque();
                    }
                }
            }
        }
        else if (estado == Estado.JUGANDO) {
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) izquierda = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) derecha = true;
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) saltando = true;

            if (key == KeyEvent.VK_N) {
                nivelActual = 2;
                cargarNivel2();
                System.out.println("Nivel 2 activado");
            }
            if (key == KeyEvent.VK_T) {
                nivelActual = 3;
                cargarNivel3();
                System.out.println("Nivel 3 activado");
            }

        } else if (estado == Estado.PAUSA) {
            if (key == KeyEvent.VK_Q) System.exit(0);
        } 
        else if (estado == Estado.GAMEOVER)
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

    private void iniciarPeleaConWilson() {
        // Acá va tu lógica de boss fight
        // Ejemplo simple: activar un flag para que aparezca Wilson como enemigo
        System.out.println("¡Comienza la batalla contra Wilson!");

        // Si tenés un objeto wilsonEnemigo, lo podés activar
        // wilsonEnemigo.setActivo(true);

        // Podrías también preparar su posición inicial
        // wilsonEnemigo.setX(800);
        // wilsonEnemigo.setY(300);
    }

}
