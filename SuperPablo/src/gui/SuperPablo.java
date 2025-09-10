package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class SuperPablo extends JPanel implements ActionListener, KeyListener{

	private boolean izquierdaPresionada=false;
	private boolean derechaPresionada=false;
	private boolean arribaPresionada=false;
	private boolean abajoPresionada=false;
	
	private Jugador Jugador;
	
	public SuperPablo() {
		 setFocusable(true);
	     addKeyListener(this);
	     
	     Jugador = new Jugador(600, 650);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Jugador.dibujar(g);
	}
	
	public void mover()
	{
		if(arribaPresionada) Jugador.moverArriba();
		if(abajoPresionada) Jugador.moverAbajo();
		if(izquierdaPresionada) Jugador.moverIzquierda();
		if(derechaPresionada) Jugador.moverDerecha();
	}
	
	 @Override
	    public void keyPressed(KeyEvent e) {
	            switch(e.getKeyCode()) {
	            	case KeyEvent.VK_UP -> arribaPresionada=true;
	            	case KeyEvent.VK_DOWN -> abajoPresionada=true;
	            	case KeyEvent.VK_LEFT -> izquierdaPresionada=true;
	                case KeyEvent.VK_RIGHT -> derechaPresionada=true;
	            } 
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	        switch(e.getKeyCode()) {
	        	case KeyEvent.VK_UP -> arribaPresionada = false;
	        	case KeyEvent.VK_DOWN -> abajoPresionada = false;
	        	case KeyEvent.VK_LEFT -> izquierdaPresionada = false;
	            case KeyEvent.VK_RIGHT -> derechaPresionada = false;
	        }
	    }

	    @Override
	    public void keyTyped(KeyEvent e) {}

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
