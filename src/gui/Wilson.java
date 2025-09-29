package gui; 

import java.util.*;
import java.util.List;

import javax.swing.ImageIcon;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Wilson {
    private int x, y;
    private int ancho, alto;

    private int vida = 10; // necesita 10 golpes para morir
    private boolean vivo = true;
    private Image imagen;
    private boolean estaALaIzquierda=false;
    private boolean direccionAnterior;
    private boolean activo=true;
    
    private ArrayList<Proyectil> proyectiles;
    private int contadorDisparo = 0; // para controlar tiempo entre disparos
    private int intervaloDisparo = 100; // frames entre disparos

    public Wilson(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y - alto;
        this.ancho = ancho;
        this.alto = alto;
        imagen = new ImageIcon("src/media/wilson.png").getImage();
        proyectiles = new ArrayList<>();
    }

    public void update() {
        if (!vivo) return;

        boolean direccionActual = estaALaIzquierda;
        // Disparo de proyectiles
        contadorDisparo++;
        if (contadorDisparo >= intervaloDisparo) {
            disparar();
            SuperPablo.reproducirEfecto("disparo");
            contadorDisparo = 0;
        }

        if (direccionActual != estaALaIzquierda) {
            limpiarProyectiles();
        }
        
        // Actualizar proyectiles
        for (Proyectil p : proyectiles) {
            p.update();
        }
        // Eliminar proyectiles inactivos
        proyectiles.removeIf(p -> !p.isActivo());
        
        direccionAnterior = estaALaIzquierda;
    }

    public void draw(Graphics g, int camaraX) {
        if (vivo) {
        	 Graphics2D g2d = (Graphics2D) g;
        	    AffineTransform at = new AffineTransform();

        	    Image img = imagen; 

        	    if (estaALaIzquierda) {
        	        // Voltear horizontalmente
        	        at.translate(x - camaraX + ancho, y);
        	        at.scale(-1, 1); // Flip horizontal
        	    } else {
        	        // Imagen normal
        	        at.translate(x - camaraX, y);
        	    }

        	    // Escalar imagen si no coincide con ancho/alto
        	    at.scale((double) ancho / img.getWidth(null), (double) alto / img.getHeight(null));

        	    // Dibujar con la transformacion aplicada
        	    g2d.drawImage(img, at, null);
        // Dibujar proyectiles
        for (Proyectil p : proyectiles) {
            p.draw(g, camaraX);
        }
        }
    }

    private void disparar() {
        // Lanza proyectil desde el centro de Wilson
        proyectiles.add(new Proyectil(x, y + alto / 2, estaALaIzquierda));
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public ArrayList<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public void recibirDanio() {
        vida--;
        if (vida <= 0) {
        	vivo = false;
        	SuperPablo.musicaInicio.stop();
        	SuperPablo.reproducirEfecto("gg");
        }
        
        if (estaALaIzquierda) {
            x = obtenerBordeDerecho(); 
        } else {
            x = obtenerBordeIzquierdo(); 
        }

        estaALaIzquierda = !estaALaIzquierda; 
    }
    
    public void invocarEnemigos(int camaraX, int sueloNivel3, int fase, List<Enemigo> enemigos)
    {
    	if(fase==3)
    	{
    		enemigos.add(new Enemigo(obtenerBordeDerecho() - 150, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 100, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "esqueleto",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 50, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
    	}else if(fase==4)
    	{
    		enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 150, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 100, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "esqueleto",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 50, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
    	}else if(fase==5)
    	{
    		enemigos.add(new Enemigo(obtenerBordeDerecho() - 200, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 150, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "esqueleto",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 10, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 50, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
    	}else if(fase==6)
    	{
    		enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 200, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 150, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "esqueleto",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 100, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 50, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
    	}else if(fase==7)
    	{
    		enemigos.add(new Enemigo(obtenerBordeDerecho() - 225, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 200, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 150, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 100, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 50, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
    	}else if(fase==8)
    	{
    		enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 225, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 200, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "esqueleto",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 150, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 100, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 50, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
    	}else if(fase==9)
    	{
    		enemigos.add(new Enemigo(obtenerBordeDerecho() - 250, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 225, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 200, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 150, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "caballero",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 100, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
            enemigos.add(new Enemigo(obtenerBordeDerecho() - 50, sueloNivel3, obtenerBordeIzquierdo(), obtenerBordeDerecho() + 100, "bandido",-1));
    	}else if(fase==10)
    	{
    		enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 250, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 225, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 200, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 150, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "caballero",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 100, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
            enemigos.add(new Enemigo(obtenerBordeIzquierdo() + 50, sueloNivel3, obtenerBordeIzquierdo() - 100, obtenerBordeDerecho(), "bandido",1));
    	}
    }
  
    
    private int obtenerBordeIzquierdo() {
        return 5750;
    }

    private int obtenerBordeDerecho() {
        return 6800; 
    }

    public boolean estaVivo() {
        return vivo;
    }

    public int getVida() {
        return vida;
    }
    
    public void limpiarProyectiles()
    {
    	proyectiles.removeIf(p -> p.isIzquierda() != estaALaIzquierda);
    }
    
    public void habilitar(boolean a)
    {
    	activo=a;
    }
    
    public boolean isActivo()
    {
    	return activo;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return ancho; }
    public int getHeight() { return alto; }
}
