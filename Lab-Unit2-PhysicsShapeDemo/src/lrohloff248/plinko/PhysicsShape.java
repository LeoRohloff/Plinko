package lrohloff248.plinko;
import crispy.shapes.*;
import processing.core.PApplet;

public class PhysicsShape {

	private Shape s;
	private double vx,vy;
	private double invMass = 1.0;
	public boolean isBall;
	public int bet = 0;
	
	public PhysicsShape(Shape s) {
		vx = 0;
		vy = 0;
		this.s = s;
	}


	public PhysicsShape(Shape s, boolean immovable, boolean isBall) {
	    this(s);
	    this.invMass = immovable ? 0.0 : 1.0;
	    this.isBall = isBall;
	}
	
	public PhysicsShape(Shape s, boolean immovable, boolean isBall, int bet) {
	    this(s);
	    this.invMass = immovable ? 0.0 : 1.0;
	    this.isBall = isBall;
	    this.bet = bet;
	}

	public void setImmovable(boolean immovable) {
	    this.invMass = immovable ? 0.0 : 1.0;
	}
	
	public void draw(PApplet surface) {
		surface.pushStyle();
		surface.fill(255);
		surface.stroke(255);
		if(isBall) {
			surface.fill(215, 0, 64);
			surface.stroke(215, 0, 64);
		}
		s.draw(surface);
		surface.popStyle();
	}

	
	public void accelerate(double ax, double ay) {
		vx += ax * invMass;
		vy += ay * invMass;
	}
	
	public Shape getShape() {
		return s;
	}
	

	public void collision(PhysicsShape[] others) {
	    final double restitution  = 0.9;
	    final double posPercent   = 0.8;
	    final double posSlop      = 0.01;
	    final double minBounceSpd = 0.25; 

	    // randomness settings
	    final double randAngleDeg   = 3.0;                      
	    final double randAngleRad   = Math.toRadians(randAngleDeg);
	    final double randTangential = 0.12;

	    for (PhysicsShape o : others) {
	        if (o == null || o == this || o.s == null || this.s == null) continue;
	        if (System.identityHashCode(this) > System.identityHashCode(o)) continue;

	        boolean aCircle = (this.s.getClass() == Circle.class);
	        boolean bCircle = (o.s.getClass()   == Circle.class);
	        boolean aRect   = (this.s.getClass() == Rectangle.class);
	        boolean bRect   = (o.s.getClass()   == Rectangle.class);


	        double ax = this.s.getX(), ay = this.s.getY();
	        double bx = o.s.getX(),    by = o.s.getY();
	        double aHX, aHY, bHX, bHY;

	        if (aCircle) {
	            double r = ((Circle)this.s).getRadius();
	            aHX = r; aHY = r;
	        } else {
	            double rw = ((Rectangle)this.s).getWidth();
	            double rh = ((Rectangle)this.s).getHeight();
	            aHX = rw/2.0; aHY = rh/2.0;
	            ax += aHX; ay += aHY;
	        }
	        if (bCircle) {
	            double r = ((Circle)o.s).getRadius();
	            bHX = r; bHY = r;
	        } else {
	            double rw = ((Rectangle)o.s).getWidth();
	            double rh = ((Rectangle)o.s).getHeight();
	            bHX = rw/2.0; bHY = rh/2.0;
	            bx += bHX; by += bHY;
	        }

	        double nx=0, ny=0, penetration=0;
	        boolean hit = false;


	        if (aCircle && bCircle) {
	            double dx = bx - ax, dy = by - ay;
	            double rsum = aHX + bHX;
	            double d2 = dx*dx + dy*dy;
	            if (d2 < rsum*rsum) {
	                double d = Math.sqrt(Math.max(d2, 1e-12));
	                nx = (d > 1e-12) ? dx/d : 1.0;
	                ny = (d > 1e-12) ? dy/d : 0.0;
	                penetration = rsum - d;
	                hit = true;
	            }
	        }
	       
	        else {
	            double dx = bx - ax, dy = by - ay;
	            double ox = (aHX + bHX) - Math.abs(dx);
	            double oy = (aHY + bHY) - Math.abs(dy);
	            if (ox > 0 && oy > 0) {
	                if (ox < oy) { nx = (dx < 0) ? -1 : 1; ny = 0;  penetration = ox; }
	                else         { nx = 0; ny = (dy < 0) ? -1 : 1;  penetration = oy; }
	                hit = true;
	            }
	        }

	        if (!hit || penetration <= 0) continue;

	       
	        double invMassA = this.invMass;
	        double invMassB = o.invMass;
	        double invSum   = invMassA + invMassB;
	        if (invSum == 0.0) continue; // both immovable

	        
	        double corr = Math.max(penetration - posSlop, 0) * posPercent / invSum;
	        double corrAx = -nx * corr * invMassA;
	        double corrAy = -ny * corr * invMassA;
	        double corrBx =  nx * corr * invMassB;
	        double corrBy =  ny * corr * invMassB;
	        this.s.setX(this.s.getX() + corrAx);
	        this.s.setY(this.s.getY() + corrAy);
	        o.s.setX(o.s.getX() + corrBx);
	        o.s.setY(o.s.getY() + corrBy);


	        double rvx = o.vx - this.vx;
	        double rvy = o.vy - this.vy;
	        double velN = rvx*nx + rvy*ny;


	        double desiredSep = Math.max((1.0 + restitution) * (-velN), minBounceSpd);
	        double j = desiredSep / invSum;

	       
	        double ang = java.util.concurrent.ThreadLocalRandom.current().nextDouble(-randAngleRad, randAngleRad);
	        double cos = Math.cos(ang), sin = Math.sin(ang);
	        double pnx = nx * cos - ny * sin;
	        double pny = nx * sin + ny * cos;


	        double tx = -ny, ty = nx;
	        double tangMag = java.util.concurrent.ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
	                         * randTangential * (minBounceSpd + Math.max(0.0, -velN));


	        double ixN = j * pnx, iyN = j * pny;

	        double ixT = tangMag * tx, iyT = tangMag * ty;

	        
	        this.vx -= (ixN + ixT) * invMassA;
	        this.vy -= (iyN + iyT) * invMassA;
	        o.vx    += (ixN + ixT) * invMassB;
	        o.vy    += (iyN + iyT) * invMassB;
	    }
	}


	public boolean isPointInside(int x, int y) {
		return s.isPointInside(x, y);
	}
	
	public void stop() {
		this.vx = 0;
		this.vy = 0;
	}
	public void slow(double ax, double ay) {
        if (this.vx > 0) {
            this.vx = Math.max(0.0, this.vx - ax);
        } else if (this.vx < 0) {
            this.vx = Math.min(0.0, this.vx + ax);
        }
        
        if (this.vy > 0) {
            this.vy = Math.max(0.0, this.vy - ay);
        } else if (this.vy < 0) {
            this.vy = Math.min(0.0, this.vy + ay);
        }
	}
	
	public void setCoordinates(double x,double y) {
		if(s.getClass() == Circle.class) {
			s.setX(x);
			s.setY(y);
		}
		else {
			s.setX(x - ((Rectangle)s).getWidth()/2);
			s.setY(y - ((Rectangle)s).getHeight()/2);

		}
	}
	
	public void gravity() {
		if (this.vy < 100) {
			this.accelerate(0, 0.15);
		}
	}
	public void walls(int w, int h) {
		final double bounce = 0.9; // 1.0 = perfectly elastic, <1.0 = slight energy loss
		
		if (this.invMass == 0) {
			return;
		}
	    if (s.getClass() == Circle.class) {
	        double r = ((Circle) s).getRadius();
	        double x = s.getX(), y = s.getY();

	        if (x - r < 0) { s.setX(r);            vx = Math.abs(vx) * bounce; }
	        if (x + r > w) { s.setX(w - r);        vx = -Math.abs(vx) * bounce; }
	        if (y - r < 0) { s.setY(r);            vy = Math.abs(vy) * bounce; }
	        if (y + r > h) { s.setY(h - r);        vy = -Math.abs(vy) * bounce; }

	    } else if (s.getClass() == Rectangle.class) {
	        double x = s.getX(), y = s.getY();
	        double rw = ((Rectangle) s).getWidth();
	        double rh = ((Rectangle) s).getHeight();

	        if (x < 0)        { s.setX(0);         vx = Math.abs(vx) * bounce; }
	        if (x + rw > w)   { s.setX(w - rw);    vx = -Math.abs(vx) * bounce; }
	        if (y < 0)        { s.setY(0);         vy = Math.abs(vy) * bounce; }
	        if (y + rh > h)   { s.setY(h - rh);    vy = -Math.abs(vy) * bounce; }
	    }
	}
	public void act(PhysicsShape[] other) {
		s.setX(s.getX() + vx);
		s.setY(s.getY() + vy);
		this.slow(Math.abs(this.vx/125) + 0.005, Math.abs(this.vy/125) + 0.005);
		this.collision(other);
		this.gravity();
	}

}
