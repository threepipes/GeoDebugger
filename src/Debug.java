import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Debug extends JPanel
	implements MouseWheelListener
	, MouseMotionListener
	, MouseListener
	, Runnable{
	Drawable draw;
	private Thread thread;
	private static final int width = 1000;
	private static final int height = 1000;
	public Debug(Drawable d){
		this.draw = d;
		init();
	}
	
	public void init(){
		JFrame frame = new JFrame("debug");
		frame.add(this);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		frame.addMouseWheelListener(this);
		frame.setSize(width, height);
		frame.setVisible(true);
		thread = new Thread(this);
		thread.start();
	}
	
	public static final double enlarge = 1.5;
	// 拡大率：1より大きい数字を入れる
	private int mx, my;
	private int dx, dy;
	private int dm;
	private boolean enter = false;
	public void update(){
		if(dm != 0){
			double per = Math.pow(enlarge, -dm);
			draw.zoom *= per;
			draw.ox += (mx*(per-1.0))/draw.zoom;
			draw.oy += (my*(per-1.0))/draw.zoom;
			dm = 0;
		}
		draw.mx = -dx;
		draw.my = -dy;
		if(enter){
			draw.enter();
			enter = false;
			dx = 0;
			dy = 0;
		}
	}
	
	public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING
        		, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
		draw.draw(g);
	}
	@Override
	protected void paintComponent(Graphics g) {
		draw(g);
	}
	
	@Override
	public void run() {
		while(true){
			update();
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		dx = e.getX() - mx;
		dy = e.getY() - my;
	}
	public void mousePressed(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}
	public void mouseReleased(MouseEvent e) {
		enter = true;
		mx = e.getX();
		my = e.getY();
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		dm = e.getWheelRotation();
	}
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}

abstract class Drawable{
	public Drawable(int min, int max){
		int len = max-min;
		zoom = 1000.0/len;
		ox = min;
		oy = min;
	}
	public abstract void draw(Graphics g);
	public double zoom = 1;
	public int ox = 0;
	public int oy = 0;
	public int mx = 0;
	public int my = 0;
	public int px(double x){
		return (int)((x-ox)*zoom-mx);
	}
	public int py(double y){
		return (int)((y-oy)*zoom-my);
	}
	public int rs(double s){
		int res = (int)(s*zoom);
		return (int)(res<1?1:res);
	}
	public void enter(){
		ox += mx/zoom;
		oy += my/zoom;
		mx = 0;
		my = 0;
	}
	public void circle(Graphics g, double x, double y, double r){
		g.drawOval(px(x-r), py(y-r), (int)r*2, (int)r*2);
	}
	public void line(Graphics g, double x1, double y1, double x2, double y2){
		g.drawLine(px(x1), py(y1), px(x1), py(y1));
	}
	public void rect(Graphics g, double x1, double y1, double x2, double y2){
		g.drawRect(px(x1), py(y1), px(x1), py(y1));
	}
}