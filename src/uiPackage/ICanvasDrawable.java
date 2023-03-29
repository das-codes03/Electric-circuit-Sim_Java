package uiPackage;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;

import javax.swing.event.MouseInputListener;

import uiPackage.RenderingCanvas.BiHashMap.MapBox;

public abstract class ICanvasDrawable extends Component implements MouseInputListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 744247388132276375L;
	abstract Rectangle getTransformedBounds();
	protected Vector<MapBox> gridLocations;
	protected Vector<Shape> regions;
	
	public abstract int getPriority();
	public RenderingCanvas canvas;
	public abstract void update(Graphics g);

	public ICanvasDrawable(RenderingCanvas canvas) {
		this.canvas = canvas;
		this.gridLocations = new Vector<>();
		this.regions = new Vector<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

}
