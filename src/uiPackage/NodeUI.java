package uiPackage;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class NodeUI extends ICanvasDrawable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4558460949541414417L;
	private static final int priority = 0;
	public double radius;
	public NodeUI(Point p, RenderingCanvas canvas) {
		super(canvas);
		setLocation(p);
	}
	@Override
	Rectangle getTransformedBounds() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return priority;
	}
}
