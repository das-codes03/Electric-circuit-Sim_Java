package simulatorgui.rendering;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import simulatorgui.rendering.RenderingCanvas.ComponentMapping.MapBox;

public abstract class CanvasDrawable extends JComponent implements MouseInputListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 744247388132276375L;
	abstract Rectangle getTransformedBounds();
	protected Vector<MapBox> gridLocations;
	protected Vector<Shape> regions;
	public abstract int getPriority();
	public long layer;
	public RenderingCanvas canvas;
	public abstract void update(Graphics g);
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

	public CanvasDrawable(RenderingCanvas canvas) {
		this.canvas = canvas;
		this.gridLocations = new Vector<>();
		this.regions = new Vector<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
}
