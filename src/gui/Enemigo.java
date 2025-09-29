package gui;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

public class Enemigo {
    private int x, y;
    private int ancho, alto;
    private int velocidad = 2;
    private int limiteIzq, limiteDer, limiteInf, limiteSup;
    private int direccion;
    public boolean vivo=true;
    public int vida;
    public String tipo;

    private Image[] esqueletoCaminando = new Image[2];
    private Image[] caballeroCaminando = new Image[2];
    private Image[] bandidoCaminando = new Image[2];
    private Image[] murcielagoVolando = new Image[2];
    private Image[] sierraGirando = new Image[2];
    
    public Enemigo(int x, int y, int limite1, int limite2,  String tipo, int direccion){
    
    	esqueletoCaminando[0] = new ImageIcon("src/media/esqueleto1.png").getImage();
    	esqueletoCaminando[1] = new ImageIcon("src/media/esqueleto2.png").getImage();
    	caballeroCaminando[0] = new ImageIcon("src/media/caballero1.png").getImage();
    	caballeroCaminando[1] = new ImageIcon("src/media/caballero2.png").getImage();
    	bandidoCaminando[0] = new ImageIcon("src/media/bandido1.png").getImage();
    	bandidoCaminando[1] = new ImageIcon("src/media/bandido2.png").getImage();
    	murcielagoVolando[0] = new ImageIcon("src/media/murcielago1.png").getImage();
    	murcielagoVolando[1] = new ImageIcon("src/media/murcielago2.png").getImage();
    	sierraGirando[0] = new ImageIcon("src/media/sierra1.png").getImage();
    	sierraGirando[1] = new ImageIcon("src/media/sierra2.png").getImage();
        
	    switch(tipo) {
	        case "esqueleto" -> vida = 1;
	        case "caballero" -> vida = 2;
	        case "bandido" -> vida = 3;
	        case "murcielago" -> vida = 1;
	        case "sierra" -> vida = 99;
	        default -> vida = 1;
	    }
	
	    switch(tipo) {
	        case "esqueleto" -> { ancho = 36; alto = 59; }
	        case "caballero" -> { ancho = 36; alto = 59; }
	        case "bandido" -> { ancho = 54; alto = 70; }
	        case "murcielago" -> { ancho = 60; alto = 36; }
	        case "sierra" -> { ancho = 256; alto = 256; }
	    }
	    
	    this.x = x;
	    this.y = y - alto;
	    if(tipo == "murcielago" || tipo == "sierra")
	    {
	    	this.limiteInf = limite1;
		    this.limiteSup = limite2;
	    }else {
	    this.limiteIzq = limite1;
	    this.limiteDer = limite2;
	    }
	    this.tipo = tipo; 
	    this.direccion=direccion;
}

    public void update() {
    	if(tipo == "murcielago" || tipo == "sierra")
    	{
    		y += direccion * velocidad;
    		if (y <= limiteInf || y >= limiteSup) {
            	direccion *= -1;
            }
    	}else {
    	x += direccion * velocidad;
        if (x <= limiteIzq || x >= limiteDer) {
        	direccion *= -1;
        }
    	}
    }

    public void draw(Graphics g, int camaraX, int paso) {
        if(vivo) {
            Image img = null;
            if(paso == -1) paso = 0;
            
            if(tipo == "esqueleto") img = esqueletoCaminando[paso];
            if(tipo == "caballero") img = caballeroCaminando[paso];
            if(tipo == "bandido") img = bandidoCaminando[paso];
            if(tipo == "murcielago") img = murcielagoVolando[paso];
            if(tipo == "sierra") img = sierraGirando[paso];
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform at = new AffineTransform();

            if(direccion == 1) {
                // Imagen normal
                at.translate(x - camaraX, y);
            } else {
                // Voltear horizontalmente
                at.translate(x - camaraX + ancho, y);
                at.scale(-1, 1);
            }

            at.scale((double) ancho / img.getWidth(null), (double) alto / img.getHeight(null));
            g2d.drawImage(img, at, null);
            }
        }



    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }
    
    public void recibirDanio()
    {
    	vida--;
    	if(vida<=0)
    	{
    		vivo=false;
    	}
    }
    
    public int getPuntos()
    {
    	int num=0;
    	if(tipo == "esqueleto")
    	{	
    		num=2;
    	}else if(tipo == "caballero")
    	{
    		num=4;
    	}else if(tipo == "bandido")
    	{
    		num=6;
    	}else if(tipo == "murcielago")
    	{
    		num=2;
    	}
    	return 10 * num;
    }
    
    public int getX() { return x;}
    public int getY() { return y;}
    public int getWidth() { return ancho;}
    public int getHeight() { return alto;}
    public boolean estaVivo() {return vivo;}
}
