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

	public RenderingCanvas canvas;
	public abstract void update(Graphics g);

	public ICanvasDrawable(RenderingCanvas canvas) {
		this.canvas = canvas;
		this.gridLocations = new Vector<>();
		this.regions = new Vector<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	public int compareTo(ICanvasDrawable o2) {
		// TODO Auto-generated method stub
		if (getLayer() < o2.getLayer())
			return -1;
		if (getLayer() > o2.getLayer())
			return 1;
		return 0;
	}
	private int layer;
	public int getLayer() {
		return layer;
	}
	void setLayer(int layer){
		this.layer = layer;
	}


}
