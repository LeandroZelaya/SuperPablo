package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gui.SuperPablo;

public class Principal {

	public static void main(String[] args) {
		 SwingUtilities.invokeLater(() -> {
	            JFrame ventana = new JFrame("Space Invaders");
	            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            ventana.setSize(1280, 720);
	            ventana.setResizable(false);

	            ventana.setLocationRelativeTo(null);

	            SuperPablo juego = new SuperPablo();
	            ventana.add(juego);

	            ventana.setVisible(true);
	        });
	}

}
