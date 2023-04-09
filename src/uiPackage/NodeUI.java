package uiPackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import uiPackage.RenderingCanvas.currentMode;

public class NodeUI extends CanvasDrawable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4558460949541414417L;
	private static final int priority = 0;
	public int radius = 5;
	public final DeviceUI parentDevice;
	private final int nodeIndex;
	private Color nodeColor = Color.white;

	public void setNodeColor(Color c) {
		nodeColor = c;
	}

	public void setRadius(int r) {
		radius = r;
		getTransformedBounds();
	}
	public NodeUI(RenderingCanvas canvas, int radius, DeviceUI parent, int nodeIndex) {
		this(new Point(), canvas, radius, parent, nodeIndex);
	}
	public NodeUI(RenderingCanvas canvas, int radius) {
		this(new Point(), canvas, radius, null, 0);
	}

	@Override
	public void setLocation(Point loc) {
		setLocation(loc.x, loc.y);
	}

	@Override
	public void setLocation(int x, int y) {
		// TODO Auto-generated method stub
		super.setLocation(x, y);
		getTransformedBounds();
		canvas.objectsMap.store(this);
	}

	public NodeUI(Point p, RenderingCanvas canvas, int radius, DeviceUI parent, int nodeIndex) {
		super(canvas);
		this.radius = radius;
		this.parentDevice = parent;
		this.nodeIndex = nodeIndex;
		setSize(radius * 2, radius * 2);
		setLocation(p);
		getTransformedBounds();
		canvas.objectsMap.store(this);
	}

	@Override
	Rectangle getTransformedBounds() {
		// TODO Auto-generated method stub
		regions.clear();
		AffineTransform at = new AffineTransform();
		var x = getX() - radius;
		var y = getY() - radius;
		at.translate(x, y);
		regions.add(at.createTransformedShape(new Rectangle(getSize())));
		return regions.get(0).getBounds2D().getBounds();
	}

	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D gx = (Graphics2D) g.create();
		getTransformedBounds();
		gx.translate(getX() - radius, getY() - radius);
		gx.setColor(nodeColor);
		gx.fillOval(0, 0, (int) Math.round(2 * radius), (int) Math.round(2 * radius));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Node clicked");
		if (canvas.mode != currentMode.MAKE_WIRE) {
			canvas.mode = currentMode.MAKE_WIRE;

			Wire w = new Wire(canvas);
			canvas.currSelected = w;
			w.addNode(this);
//			var temp = new NodeUI(canvas);
//			temp.setLocation(canvas.screenToLocalPoint(e.getLocationOnScreen()));
//			w.addNode(temp);
			canvas.Render();
		} else {
			if (this != ((Wire) canvas.currSelected).nodes.lastElement()) {
				((Wire) canvas.currSelected).addNode(this);
				canvas.currSelected = null;
				canvas.mode = currentMode.NONE;
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
		// TODO Auto-generated method stub
		return priority;
	}

	public int getNodeIndex() {
		return nodeIndex;
	}
}
