import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Test
{
	static int framerate = 60;
	static Window window = new Window();
	
	public static void main(String[] args)
	{
		Timer timer = new Timer();
		Update update = new Update(window);
		timer.schedule(update, 0, 1000l/framerate);
	}
}

class Controls extends KeyAdapter
{
	double movementSpeed = 0.05;
	double turnSpeed = 3;
	Window window;
	
	public Controls(Window window)
	{
		this.window = window;
	}
	
	public void keyPressed(KeyEvent event) 
	{
        int keyCode = event.getKeyCode();
        if (keyCode == event.VK_LEFT)
        	window.playerX-=movementSpeed;
        else if (keyCode == event.VK_RIGHT)
        	window.playerX+=movementSpeed;
        else if (keyCode == event.VK_UP)
        	window.playerY-=movementSpeed;
        else if (keyCode == event.VK_DOWN)
        	window.playerY+=movementSpeed;
        else if (keyCode == event.VK_A)
        	window.playerDegrees-=turnSpeed;
        else if (keyCode == event.VK_D)
        	window.playerDegrees+=turnSpeed;
    }
}

/*This class is instantiated once in the Timer constructor, 
  and then the run method of the instance is executed at 
  the interval described in the Timer constructor
*/
class Update extends TimerTask
{
	Window window;
	
	public Update(Window window)
	{
		this.window = window;
	}
	
	@Override
    public void run() 
	{
		window.repaint();
	}
}

class Window extends JFrame 
{	
	int[][] map = { { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1 }, 
					{ 1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,1 }, 
					{ 1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1 }, 
					{ 1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1 }, 
					{ 1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,1 }, 
					{ 1,0,0,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1 }, 
					{ 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1 }, 
					{ 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, };
	
	double playerX = 10;
	double playerY = 18;
	double playerDegrees = 270;
	int fov = 90;
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	// This is set to maximum ray resolution (each pixel of the screen gets its own ray)
	double rayDegreesResolution = fov / (double)dim.width;
	double rayAdvanceResolution = 0.1;
	int drawDistance = 6;
	
	public Window()
	{
		addKeyListener(new Controls(this));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(dim);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));		
		setVisible(true);
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(1));

		// Loop through all rays
		for (int rayNumber = 0; rayNumber < dim.width; rayNumber++)
		{
			// Get degrees for this ray
			double rayDegrees = playerDegrees - fov/2 + (rayNumber/(double)dim.width) * fov;
			// Get radians from degrees
			double rayRadians = rayDegrees/180 * Math.PI;
			// Get unit vector
			final Point2D.Double RAY_UNIT_VECTOR = new Point2D.Double(Math.cos(rayRadians), Math.sin(rayRadians));
			// Advance the ray forward
			double rayLength = 0;
			boolean hitWall = false;
			while (!hitWall)
			{
				// Advance
				Point2D.Double rayVector = new Point2D.Double(RAY_UNIT_VECTOR.x * rayLength, RAY_UNIT_VECTOR.y * rayLength);
				// Get vector end point position when starting from player position
				Point2D.Double rayEndPosition = new Point2D.Double(playerX + rayVector.x, playerY + rayVector.y);
				// Check wall hit
				if (map[(int)rayEndPosition.y][(int)rayEndPosition.x] == 1)
					hitWall = true;
				else
					rayLength += rayAdvanceResolution;
			}
			
			// Distance of wall within drawDistance?
			if (rayLength < drawDistance)
			{
				// Determine color based on rayLength
				int colorByte = (int)(255 * rayLength/drawDistance);
				Color color = new Color(colorByte, colorByte, colorByte);
				g2d.setColor(color);
				// Draw vertical column based on rayLength
				g2d.drawRect(rayNumber, dim.height/3, 1, dim.height/3);
			}
		}
		
	}
}