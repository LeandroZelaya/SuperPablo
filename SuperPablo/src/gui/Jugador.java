package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Jugador extends JPanel {

	private int ancho=50;
	private int alto=20;
	private int x, y;
	
	
	public Jugador(int x, int y) {
		this.x =x;
		this.y =y;
	}
	
	public void dibujar(Graphics g)
	{
			g.setColor(Color.GREEN);
			g.fillRect(x, y, ancho, alto);
	}
	
	public void moverDerecha()
	{
		x+=4;
	}
	
	public void moverIzquierda()
	{
		x-=4;
	}
	
	public void moverArriba()
	{
		y+=4;
	}
	
	public void moverAbajo()
	{
		y-=4;
	}
	

	protected void superPaintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.GREEN);
		g.fillRect(500, 50, 50, 20);
	}
}
