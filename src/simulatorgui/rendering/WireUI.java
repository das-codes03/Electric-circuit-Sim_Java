package simulatorgui.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Vector;

public class WireUI extends CanvasDrawable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4130602527644480994L;
	private static final int priority = 1;
	public final Vector<NodeUI> nodes;
	private Color wireColor = new Color(255, 174, 0);
	public double wireThickness = 5;
	public double wireBoundWidth = 10;

	public void addNode(NodeUI n) {
		if (n == null)
			n = new NodeUI(canvas, NodeUI.DEFAULT_RADIUS);
		nodes.add(n);
		n.includeWire(this);
		getTransformedBounds();
		canvas.objectsMap.store(this);
		canvas.bringToFront(this);
	}
	public void removeNode(NodeUI n) {
		nodes.remove(n);
		n.incidentWires.remove(this);
		getTransformedBounds();
		canvas.objectsMap.store(this);
		canvas.bringToFront(this);
	}
	public boolean contains(NodeUI node) {
		return nodes.contains(node);
	}


	public WireUI(RenderingCanvas canvas) {
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

		return null;
	}

	@Override
	public void update(Graphics g) {
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
		canvas.add(new DeviceToolbox(this));
		canvas.revalidate();
	}

	public void remove() {
		canvas.objectsMap.remove(this);
		for (var n : nodes) {
			n.incidentWires.remove(this);
			if(n.incidentWires.size()== 0 && n.parentDevice == null) {
				n.remove();
			}
		}
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
