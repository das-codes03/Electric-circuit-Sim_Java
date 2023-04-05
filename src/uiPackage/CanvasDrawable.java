package uiPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import uiPackage.RenderingCanvas.BiHashMap.MapBox;

public abstract class CanvasDrawable extends JComponent implements MouseInputListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 744247388132276375L;

	abstract Rectangle getTransformedBounds();

	final ComponentDescriptor descr;
	protected Vector<MapBox> gridLocations;
	protected Vector<Shape> regions;
	private Color aldebo;

	public abstract int getPriority();

	public long layer;
	public RenderingCanvas canvas;

	public abstract void update(Graphics g);

	public static BufferedImage applyAldebo(BufferedImage original, Color aldebo) {
		BufferedImage img = copyImage(original);
		double[] alRGB = new double[] { aldebo.getRed() / 255.0, aldebo.getGreen() / 255.0, aldebo.getBlue() / 255.0 };
		DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
		int[] bts = db.getData();
		for (int i = 0; i < bts.length; i++) {
			var rgb = bts[i];
			int a = (rgb >> 24) & 0xff;
			int r = (rgb >> 16) & 0xff;
			int g = (rgb >> 8) & 0xff;
			int b = rgb & 0xff;
			r = (int) (r * alRGB[0]);
			g = (int) (g * alRGB[1]);
			b = (int) (b * alRGB[2]);
			rgb = a << 24 | r << 16 | g << 8 | b;
			bts[i] = rgb;
		}
		return img;
	}

	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	public int compareTo(CanvasDrawable obj2) {
		if (this.getPriority() < obj2.getPriority()) {
			return -1;
		}
		if (this.getPriority() > obj2.getPriority()) {
			return 1;
		}
		if (this.layer < obj2.layer) {
			return -1;
		}
		if (this.layer > obj2.layer) {
			return 1;
		}
		return 0;
	}

	public CanvasDrawable(RenderingCanvas canvas, ComponentDescriptor desc) {
		this(canvas, desc, Color.white);
	}

	public CanvasDrawable(RenderingCanvas canvas, ComponentDescriptor desc, Color aldebo) {
		this.setAldebo(aldebo);
		this.descr = desc;
		this.canvas = canvas;
		this.gridLocations = new Vector<>();
		this.regions = new Vector<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public CanvasDrawable(RenderingCanvas canvas) {
		this(canvas, null, Color.white);
	}

	public void setAldebo(Color aldebo) {
		this.aldebo = aldebo;
	}

}
