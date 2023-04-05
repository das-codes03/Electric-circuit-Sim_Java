package uiPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import uiPackage.RenderingCanvas.currentMode;

public class DeviceUI extends CanvasDrawable {
	private static final long serialVersionUID = -4008080849860554001L;
	private static final int priority = 2;
	private static final int LOD_COUNT = 10;
	private Animable animator;

	@Override
	public int getPriority() {
		return priority;
	}

	private static Map<String, BufferedImage[]> sharedImageMemory = new HashMap<>();
	private Point lastClickedLocalSpace;
	/** Image source. */
	private ArrayList<BufferedImage> rawimage;
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
			n.setLocation((int) at.getTranslateX(), (int) at.getTranslateY());
			at.translate(-pt.x, -pt.y);
		}
	}

	// TODO: override set location
	@Override
	public void setLocation(Point loc) {
		setLocation(loc.x, loc.y);
	}

	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		updateNodes();
		getTransformedBounds();
		canvas.objectsMap.store(this);
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
		this(canvas, imagePath, width, height, null, null, null);
	}

	public DeviceUI(RenderingCanvas canvas, String imagePath, int width, int height, ComponentDescriptor descr,
			Point[] nodes, Animable animator) {
		super(canvas, descr);
		this.nodes = new HashMap<>();
		this.rawimage = new ArrayList<>();
		this.animator = animator;
		if (nodes != null)
			for (var p : nodes) {
				this.nodes.put(new NodeUI(canvas, 5), p);
			}

		this.lastClickedLocalSpace = new Point(0, 0);
		setSize(new Dimension(width, height));

		try {
			rawimage = ResourceManager.loadImage(imagePath, LOD_COUNT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getTransformedBounds();
		canvas.objectsMap.store(this);
	}

	boolean flag = false;

	public void update(Graphics g) {
		Graphics2D gx = (Graphics2D) g.create();
		int lod = Math.max(Math.min(canvas.getLOD(), LOD_COUNT - 1), 0);
		System.out.println(lod);
		BufferedImage img = rawimage.get(lod);
		Rectangle bounds = getTransformedBounds(); // get bounding box
		gx.translate(bounds.getCenterX() - getWidth() / 2.0, bounds.getCenterY() - getHeight() / 2.0);
		gx.rotate(Math.toRadians(rotation), getWidth() / 2.0, getHeight() / 2.0);
		gx.drawImage(img, 0, 0, getWidth(), getHeight(), canvas);
		if (animator != null)
			animator.animate(gx);
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
		setLocation(getX() + dx, getY() + dy);

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
