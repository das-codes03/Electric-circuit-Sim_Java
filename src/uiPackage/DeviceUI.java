package uiPackage;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import uiPackage.RenderingCanvas.currentMode;

public class DeviceUI extends ICanvasDrawable {
	private static final long serialVersionUID = -4008080849860554001L;
	private static final int priority = 2;

	@Override
	public int getPriority() {
		return priority;
	}

	private static Map<String, BufferedImage> sharedImageMemory = new HashMap<>();
	private Point lastClickedLocalSpace;
	/** Image source. */
	private BufferedImage rawImage = null;
	private double rotation = 00d; // in degrees
	protected Map<NodeUI, Point> nodes; // node_reference->localposition

	public DeviceUI setRotation(double r) {
		rotation = r;
		getTransformedBounds();
		canvas.objectsMap.store(this);
		updateNodes();
		return this;
	}
	private void updateNodes() {
		AffineTransform at = new AffineTransform();
		at.setToTranslation(getX(), getY());
		at.rotate(Math.toRadians(rotation));
		for (var n : nodes.keySet()) {
			var pt = nodes.get(n);
			at.translate(pt.x, pt.y);
			n.setLocation((int)at.getTranslateX(),(int)at.getTranslateY());
			at.translate(-pt.x, -pt.y);
		}
	}
	// TODO: override set location
	public DeviceUI setLocalPosition(int x, int y) {
		setLocation(new Point(x, y));
		updateNodes();
		getTransformedBounds();
		canvas.objectsMap.store(this);
		return this;
	}

	@Override
	public Rectangle getTransformedBounds() {
		regions.clear();
		AffineTransform at = new AffineTransform();
		var x = getX() - getWidth() / 2.0;
		var y = getY() - getHeight() / 2.0;

		at.translate(x, y);
		at.rotate(Math.toRadians(rotation), getWidth() / 2.0, getHeight() / 2.0);
		regions.add(at.createTransformedShape(new Rectangle(getSize())));
		return regions.get(0).getBounds2D().getBounds();
	}

	public DeviceUI(RenderingCanvas canvas, String imagePath, int width, int height) {
		super(canvas);
		this.nodes = new HashMap<>();
		//TODO: testing only
		nodes.put(new NodeUI(canvas), new Point(50,0));
		nodes.put(new NodeUI(canvas), new Point(-50,0));

		//***********************
		this.lastClickedLocalSpace = new Point(0, 0);
		setSize(new Dimension(width, height));
		// dimensions = new Dimension(width, height);
		if (!sharedImageMemory.containsKey(imagePath))
			try {
				rawImage = ImageIO.read(getClass().getResource(imagePath));
				sharedImageMemory.put(imagePath, rawImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			rawImage = sharedImageMemory.get(imagePath);
		getTransformedBounds();
		canvas.objectsMap.store(this);
		// parent.add(this);
	}

	public void update(Graphics g) {
		Graphics2D gx = (Graphics2D) g.create();
		Rectangle bounds = getTransformedBounds(); // get bounding box
		gx.translate(bounds.getCenterX() - getWidth() / 2.0, bounds.getCenterY() - getHeight() / 2.0);
		gx.rotate(Math.toRadians(rotation), getWidth() / 2.0, getHeight() / 2.0);
		gx.drawImage(rawImage, 0, 0, getWidth(), getHeight(), canvas);
		gx.dispose();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println(e.getPoint());

//		if (globalPointInShape(e.getLocationOnScreen())) {
		canvas.mode = currentMode.DRAG_COMPONENT;
		canvas.currSelected = this;
		System.out.println("Dragged " + this.hashCode());
		var p = canvas.screenToLocalPoint(e.getLocationOnScreen());
		int dx = p.x - getX() - lastClickedLocalSpace.x;
		int dy = p.y - getY() - lastClickedLocalSpace.y;
		setLocalPosition(getX() + dx, getY() + dy);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastClickedLocalSpace = canvas.screenToLocalPoint(e.getLocationOnScreen());
		lastClickedLocalSpace.x -= getX();
		lastClickedLocalSpace.y -= getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
