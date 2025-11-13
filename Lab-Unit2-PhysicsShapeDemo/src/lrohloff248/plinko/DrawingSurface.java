package lrohloff248.plinko;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import crispy.shapes.*;
import processing.core.PApplet;


public class DrawingSurface extends PApplet {
	
	private ArrayList<PhysicsShape> shapes;
	private Plinko game;

	public DrawingSurface() {
		shapes = new ArrayList<>();
	}
	
	public void settings() {
		setSize(800,600);
		game = new Plinko(shapes,width,height);
	}
	
	// The statements in the setup() function 
	// execute once when the program begins
	public void setup() {
	}
	
	// The statements in draw() are executed until the 
	// program is stopped. Each statement is executed in 
	// sequence and after the last line is read, the first 
	// line is executed again.
	public void draw() {
		
		background(44, 76, 96);
		this.fill(0);
		for (PhysicsShape shape : shapes) {
			shape.draw(this);
			PhysicsShape[] arr = shapes.toArray(new PhysicsShape[0]);
			shape.act(arr);
			shape.walls(this.width, this.height);
		}
		game.act(this);
		
	}
	
	public void mousePressed() {
		game.drop(mouseX, 50);
	}
	
	public void keyPressed() {
		    if (keyCode == UP) {
		      game.bet(5);
		    } else if (keyCode == DOWN) {
		      game.bet(-5);
		    } 
        }
	
}


