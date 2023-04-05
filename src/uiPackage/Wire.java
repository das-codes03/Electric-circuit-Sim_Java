package uiPackage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

import uiPackage.RenderingCanvas.BiHashMap.MapBox;

public class Wire extends CanvasDrawable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4130602527644480994L;
	private static final int priority = 1;
	Vector<NodeUI> nodes;
	private Color wireColor = Color.pink;
	public double wireThickness = 5;
	public double wireBoundWidth = 10;

	public void addNode(NodeUI n) {
		if (n == null)
			n = new NodeUI(canvas, 10);
		nodes.add(n);
		getTransformedBounds();
		canvas.objectsMap.store(this);
		// canvas.components.add(this);
		canvas.bringToFront(this);
	}

	public void setWire(Vector<NodeUI> nodes) {
		canvas.objectsMap.remove(this);
		this.nodes = nodes;

		getTransformedBounds();
		canvas.objectsMap.store(this);
		// canvas.components.add(this);
		canvas.bringToFront(this);

	}

	public Wire(RenderingCanvas canvas) {
		super(canvas);
		nodes = new Vector<>();
		getTransformedBounds();
		canvas.objectsMap.store(this);

	}

	@Override
	public Rectangle getTransformedBounds() {
		// TODO Auto-generated method stub
		regions.clear();
		AffineTransform at = new AffineTransform();
		for (var n : nodes) {
			canvas.objectsMap.store(n);
		}
		for (int i = 0; i < nodes.size() - 1; i++) {
			NodeUI currN = nodes.get(i);
			NodeUI nextN = nodes.get(i + 1);

			// translate to current node
			at.translate(currN.getX(), currN.getY());

			// get length of current segment
			var len = Point.distance(currN.getX(), currN.getY(), nextN.getX(), nextN.getY());

			// get rotation of current segment
			var rot = Math.atan2(nextN.getY() - currN.getY(), nextN.getX() - currN.getX());
			at.rotate(rot);

			// translate to adjust for wire width
			at.translate(-wireBoundWidth / 2.0, -wireBoundWidth / 2.0);

			// add the new rect
			regions.add(at.createTransformedShape(
					new Rectangle((int) Math.round(len + wireBoundWidth), (int) wireBoundWidth)));

			// reset the transform for next segment!
			at.setToIdentity();

		}
		System.out.println("Wire : " + nodes.size());
		return null;
	}

	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D gx = (Graphics2D) g.create(); // cast to 2D
		getTransformedBounds(); // get bounding box

		gx.setStroke(new BasicStroke((float) (wireThickness), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		gx.setColor(wireColor);
		for (int i = 0; i < nodes.size() - 1; ++i) {
			var currNPos = nodes.get(i).getLocation();
			var nextNPos = nodes.get(i + 1).getLocation();
			gx.drawLine(currNPos.x, currNPos.y, nextNPos.x, nextNPos.y);
		}

		gx.dispose();
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
		return priority;
	}

}
