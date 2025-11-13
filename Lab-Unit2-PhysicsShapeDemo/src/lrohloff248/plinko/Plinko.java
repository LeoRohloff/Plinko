package lrohloff248.plinko;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.DecimalFormat;
import crispy.shapes.*;
import processing.core.PApplet;
import java.util.concurrent.ThreadLocalRandom;

public class Plinko {
	private ArrayList<PhysicsShape> shapes;
	private ArrayList<PhysicsShape> wins;
	private ArrayList<PhysicsShape> power;
	private int width,height,dropped, bet = 5;
	double balance = 2000;
	private boolean changed;
	private double[] multies;
	
	public Plinko(ArrayList<PhysicsShape> shapes, int width, int height) {
		this.wins = new ArrayList<>();
		this.power = new ArrayList<>();
		this.width = width;
		this.height = height;
		this.shapes = shapes;
		this.multies = new double[8];
		boolean offset = false;
		for (int y = 125; y < height - 125; y += 50) {
			for (int x = -100; x < width + 100; x += 50) {
				shapes.add(new PhysicsShape(new Circle(x + (offset?0:25),y,15),true,false));
			}
			offset = !offset;
		}
		
		for (int x = 0; x < width + 100; x += 98) {
			shapes.add(new PhysicsShape(new Rectangle(x,height - 100,15,height - 100),true,false));
		}
		for (int i = 15; i < width - 15; i +=  98) {
			wins.add(new PhysicsShape(new Rectangle(i,height - 10,90,height - 10),true,false));
		}
	}
	
	private double win() {
		double total = 0;

		for (Iterator<PhysicsShape> it = shapes.iterator(); it.hasNext();) {
		    PhysicsShape c = it.next();
		    Shape s = c.getShape();
		    if (!(c.isBall)) continue;

		    int px = (int) s.getX();
		    int py = (int) s.getY() + (int) ((Circle) s).getRadius();

		    boolean hit = false;
		    int index = -1;
		    for (PhysicsShape w : wins) {
		        if (w.isPointInside(px, py) && c.isBall) { hit = true; index = wins.indexOf(w); break; }
		    }
		    if (hit) { it.remove(); total += multies[index] * c.bet;}
		}
		return total;
	}
	
	private void addPowerUp() {
		double rx = ThreadLocalRandom.current().nextDouble(width);
		double ry = ThreadLocalRandom.current().nextDouble(height - 125) + 125;
		PhysicsShape shape = new PhysicsShape(new Circle(rx,ry,10),true,false);
//		for()
//		power.add();
	}
	
	public void bet(int x) {
		x = x * Math.max((int)bet/50,1);
		this.bet = Math.max(0, bet + x);
	}
    public static double random() {
        final double mean = 0.9;

        double u = ThreadLocalRandom.current().nextDouble();
        double x = -mean * Math.log(1.0 - u);

        if (x > 1000.0) x = 1000.0;

        if (x < 10.0) {
            return Math.rint(x * 10.0) / 10.0;
        } else {
            return Math.rint(x);
        }
    }
    
    public void text(PApplet window) {
    	window.pushStyle();
		for(int i = 0; i < 8; i++) {
			Rectangle rect = (Rectangle) (wins.get(i).getShape());
			if(multies[i] < 1.0) {
				window.fill(255,0,0);
			}
			else if(multies[i] < 10.0) {
				window.fill(0,255,0);
			}
			else{
				window.fill(255, 255, 0);
			}
			window.textSize(20);
			window.text("" + multies[i] + "x",(float) (rect.getX() + 20), (float) (rect.getY() - 20));
		}
		
		window.fill(175,175,20);
		window.text("Balance: $" + (double)((int)(balance*100 + 0.1))/100 + "\nBet: $" + bet, 10, 20);
		window.popStyle();
    }
    
    public void drop(double x, double y) {
    	if (balance >= bet) {
	    	shapes.add(new PhysicsShape(new Circle(x,50,10),false,true,bet));
	    	dropped++;
	    	changed = false;
	    	balance -= bet;
    	}
    }
	
	public void multiply() {
		if(dropped%5 == 0 && !changed) {
			for(int i = 0; i < 8; i++) {
				multies[i] = random();
			}
			changed = true;
		}
	}
	public void act(PApplet window) {
		balance += win();
		multiply();
		text(window);
	}
	
}
