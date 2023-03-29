package uiPackage;

import java.awt.Color;
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
import java.util.Vector;

import javax.imageio.ImageIO;
import uiPackage.RenderingCanvas.BiHashMap.MapBox;

public class ScalableImage extends ICanvasDrawable {
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
	public Vector<NodeUI> nodes;

	public ScalableImage setRotation(double r) {
		rotation = r;
		getTransformedBounds();
		canvas.componentGrid.store(this);
		return this;
	}

	public ScalableImage setLocalPosition(int x, int y) {
		setLocation(new Point(x,y));
		getTransformedBounds();
		canvas.componentGrid.store(this);
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

	public ScalableImage(RenderingCanvas canvas, String imagePath, int width, int height) {
		super(canvas);
		this.nodes = new Vector<>();
		this.lastClickedLocalSpace = new Point(0,0);
		setSize(new Dimension(width,height));
		//dimensions = new Dimension(width, height);
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
		canvas.addComponent(this);
		// parent.add(this);
	}

//	public boolean globalPointInShape(Point p) {
//		var screenspace = canvas.getLocationOnScreen();
//		var localPoint = new Point(p.x - screenspace.x, p.y - screenspace.y);
//		return imageboundary.contains(localPoint);
//	}

	public void update(Graphics g) {
		Graphics2D gx = (Graphics2D) g.create(); // cast to 2D
			Rectangle bounds = getTransformedBounds(); // get bounding box

		gx.scale(canvas.scaleX, canvas.scaleY);
		gx.translate(-canvas.offsetX / canvas.scaleX, -canvas.offsetY / canvas.scaleY);

//		synchronized (gridLocations) {
//			gx.setColor(Color.red);
//			for (var k : gridLocations) {
//
//				gx.draw(k.boxRect);
//			}
//		}

		gx.translate(bounds.getCenterX() - getWidth() / 2.0, bounds.getCenterY() - getHeight() / 2.0);
		gx.rotate(Math.toRadians(rotation), getWidth() / 2.0, getHeight() / 2.0);

		if (!gx.drawImage(rawImage, 0, 0, getWidth(), getHeight(),Color.red, canvas)) {
			System.out.println("NOT DRAWN!");
		}
//		 gx.setStroke(new BasicStroke((float) (canvas.scaleX*dimensions.width), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		gx.setColor(Color.red);
//		gx.drawLine(0, 0, 0,dimensions.height);
		gx.dispose();
		// reset graphics transform to original!
		// gx.setTransform(oldTr);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println(e.getPoint());

//		if (globalPointInShape(e.getLocationOnScreen())) {
			System.out.println("Dragged " + this.hashCode());
			var p = canvas.screenToLocalPoint(e.getLocationOnScreen());
			int dx =p.x - getX() - lastClickedLocalSpace.x;
			int dy = p.y  - getY() - lastClickedLocalSpace.y;
			setLocalPosition(getX()+dx, getY()+dy);


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
