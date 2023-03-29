package uiPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class RenderingCanvas extends JPanel implements MouseInputListener, MouseWheelListener {

	enum currentMode {
		MOVE_CANVAS, DRAG_COMPONENT, NONE
	}

	/** Here screen will be stored. */
	private BufferedImage renderImage;

	/** Last clicked point in local space. */
	Point lastClicked;
	currentMode mode;

	/** Size of each box in component grid. */
	private int boxSize = 100;
	double quality = 1;
	/** Component grid divisions. */
	public BiHashMap componentGrid;

	/** Add component in viewport. */
	public void addComponent(ScalableImage comp) {
		componentGrid.store(comp);
	}

	/** Bring this component to top. */
	public void bringToFront(ICanvasDrawable comp) {
		for (var g : comp.gridLocations) {
			int i = g.components.indexOf(comp);
			// shifting component to front
			for (; i > 0; i--) {
				var curr = g.components.get(i);
				var prev = g.components.get(i - 1);
//				if(prev.getPriority() < curr.getPriority())
//					return;
				g.components.set(i, prev);
				g.components.set(i - 1, curr);
			}
		}

	}

	/** Transform screenspace point to renderspace */
	public Point screenToLocalPoint(Point worldPoint) {
		Point local = new Point();
		local.x = (int) ((worldPoint.x - getLocationOnScreen().x + offsetX) / scaleX);
		local.y = (int) ((worldPoint.y - getLocationOnScreen().y + offsetY) / scaleY);
		return local;
	}

	/** Data structure to store components in grid. */
	public class BiHashMap {
		class MapBox {
			public MapBox(int x, int y) {
				super();
				this.components = new ArrayList<>();
				boxRect = generateRect(x, y);
			}
			public Rectangle getBoxRect() {
				return boxRect;
			}
			private Rectangle boxRect;
			private ArrayList<ICanvasDrawable> components;
		}

		private Rectangle generateRect(int xi, int yi) {
			var rect = new Rectangle(xi * BiHashMap.this.boxSize, yi * BiHashMap.this.boxSize, BiHashMap.this.boxSize,
					BiHashMap.this.boxSize);
			return rect;
		}

		public BiHashMap(int b) { // constructor
			this.boxSize = b;
			mMap = new HashMap<Integer, HashMap<Integer, MapBox>>();
		}

		private final int boxSize;
		private final HashMap<Integer, HashMap<Integer, MapBox>> mMap;

		private MapBox getBox(int x, int y) {
			var temp = mMap.get(x);
			if (temp != null)
				return temp.get(y);
			else
				return null;
		}

		public void remove(ICanvasDrawable comp) {
			try {
				for (var b : comp.gridLocations) {
					b.components.remove(comp);
					// TODO: delete entire box if components is empty
				}
			} catch (NullPointerException e) {
				System.err.println("Component not found!");
				e.printStackTrace();
			}
		}

		private MapBox set(int x, int y, ICanvasDrawable comp) {
			MapBox box;
			if ((box = getBox(x, y)) == null) {
				HashMap<Integer, MapBox> m = null;
				if (!mMap.containsKey(x)) {
					m = mMap.put(x, new HashMap<Integer, MapBox>());
				}
				m = mMap.get(x);
				m.put(y, new MapBox(x, y));
				box = m.get(y);
			}
			box.components.add(comp);
			return box;
		}

		private boolean checkInBox(Shape s, int xi, int yi) {
			var rect = generateRect(xi, yi);
			return s.intersects(rect);
		}

		private void recurFind(Shape s, Point currB, Set<Point> bs, boolean l, boolean r, boolean u, boolean d) {
			if (!checkInBox(s, currB.x, currB.y) || bs.contains(currB)) {
				return;
			} else {
				bs.add(currB);
				if (u)
					recurFind(s, new Point(currB.x, currB.y - 1), bs, true, true, true, false);
				if (d)
					recurFind(s, new Point(currB.x, currB.y + 1), bs, true, true, false, true);
				if (l)
					recurFind(s, new Point(currB.x - 1, currB.y), bs, true, false, true, true);
				if (r)
					recurFind(s, new Point(currB.x + 1, currB.y), bs, false, true, true, true);
			}
		}

		private Set<Point> getOverlappingBoxes(Shape sh) {
			Set<Point> o = new HashSet<>();
			var bn = sh.getBounds();
			Point b = getBox(new Point((int) Math.round(bn.getCenterX()), (int) Math.round(bn.getCenterY())));
			recurFind(sh, b, o, true, true, true, true);
			return o;
		}

		private Point getBox(Point coordinate) {
			int x = coordinate.x / boxSize - (coordinate.x < 0 ? 1 : 0);
			int y = coordinate.y / boxSize - (coordinate.y < 0 ? 1 : 0);
			return new Point(x, y);
		}

		public void store(ICanvasDrawable comp) {

			for (var b : comp.gridLocations) {
				b.components.remove(comp);
			}
			comp.gridLocations.clear();
			for (var x : comp.regions) {
				var bs = getOverlappingBoxes(x);
				for (var p : bs) {
					comp.gridLocations.add(set(p.x, p.y, comp));
				}
			}
			bringToFront(comp);
		}

		public ArrayList<ICanvasDrawable> getComponentsInRect(Point p, Dimension dimension) {
			var b1 = getBox(p);
			var b2 = getBox(new Point((int) (p.x + dimension.getWidth()), (int) (p.y + dimension.getHeight())));
			int x1 = b1.x - 1;

			int y1 = b1.y - 1;

			int x2 = (b2.x + 1);

			int y2 = (b2.y + 1);

//			SortedSet<ICanvasDrawable> temp = new TreeSet<ICanvasDrawable>(new Comparator<ICanvasDrawable>() {
//				@Override
//				public int compare(final ICanvasDrawable o1, final ICanvasDrawable o2) {
//					// Define comparing logic here
//					return o1.compareTo(o2);
//				}
//			});
			ArrayList<ICanvasDrawable> temp = new ArrayList<>();
			Rectangle rect = new Rectangle(p, dimension);
			for (int i = x1; i <= x2; ++i) {
				for (int j = y1; j <= y2; ++j) {
					var s = getBox(i, j);
					if (s == null)
						continue;

					synchronized (s.components) {
						if (x1 != i && x2 != i && y1 != j && y2 != j) // non corner box
							temp.addAll(s.components);
						else
							for (var c : s.components) {

								for (var bnd : c.regions) {
									if (bnd.intersects(rect)) {
										temp.add(c);

									}
								}

							}
					}

				}
			}
			return temp;
		}

		public ICanvasDrawable getTop(Point localPoint) {
			var b = getBox(localPoint);
			ICanvasDrawable found = null;
			var s = getBox(b.x, b.y);
			if (s == null)
				return null;
			for (var c : s.components) {
				for (var bnd : c.regions) {
					if (bnd.contains(localPoint))
						if (found == null) {
							found = c;
							break;
						}
				}
			}
			if (found != null)
				bringToFront(found);
			return found;
		}

	}
	// hashmap of all references

	public boolean reset(double quality) {
		try {
			renderImage = new BufferedImage((int) Math.ceil(getWidth() * quality),
					(int) Math.ceil(getHeight() * quality), BufferedImage.TYPE_INT_RGB);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void zoom(Point focus, double targetZoom) {
//TODO: ZOOM yet to implement
		Point center = getLocationOnScreen();
		center.x += getWidth() / 2;
		center.y += getHeight() / 2;

		offsetX -= (int) Math.round((focus.x - center.x) - ((focus.x - center.x) * targetZoom / scaleX));
		offsetY -= (int) Math.round((focus.y - center.y) - ((focus.y - center.y) * targetZoom / scaleY));

//		var scalechange = targetZoom / scaleX; //
		scaleX = scaleY = targetZoom;
//		var xRatio = (focus.x-center.x) / (double) getWidth();
////		var offsetBase = (scalechange-1) * getWidth();
//		offsetX -= offsetX * scalechange * xRatio;
//		// offsetY -= (int)-((-focus.y+center.y) * scalechange);
//		offsetX = (int) (offsetX - sign  * (center.x- focus.x));
//		offsetY = (int) (offsetY - sign  * (center.y - focus.y));
		Render();
		// System.out.println("Offset x: " + offsetX + " Offset y: " + offsetY);
	}

	public void Render() {
		repaint();
	}

	public RenderingCanvas() {

		this.setDoubleBuffered(true);
		renderImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.componentGrid = new BiHashMap(boxSize);
	}

	Color gridColor = new Color(0, 50, 0);
	Color secondaryGridColor = new Color(00, 30, 00);
	public double scaleX = 1;
	public double scaleY = 1;
	public int offsetX = 0;
	public int offsetY = 0;
	private static final long serialVersionUID = 6803922477054275835L;

	private void drawGrid(Graphics2D g) {
		int basegap = 100;
		int logzoom = (int) (Math.log(scaleX) / Math.log(2));
		int gap = (int) (basegap / Math.pow(2, logzoom));

		g.setColor(gridColor);

		for (double i = (-gap * scaleX); i < this.getWidth() + (gap * scaleX); i += gap * scaleX) {
			double shift = -offsetX % (gap * scaleX);
			g.drawLine((int) Math.round(i + shift), 0, (int) Math.round(i + shift), this.getHeight());
		}
		for (double i = (-gap * scaleY); i < this.getHeight() + (gap * scaleX); i += gap * scaleY) {
			double shift = -offsetY % (gap * scaleY);
			g.drawLine(0, (int) Math.round(i + shift), this.getWidth(), (int) Math.round(i + shift));
		}

		g.setColor(secondaryGridColor);
		for (double i = (-gap * scaleX); i < this.getWidth() + (gap * scaleX); i += gap * scaleX) {
			double shift = -(offsetX) % (gap * scaleX) + (gap / 2.0 * scaleX);
			g.drawLine((int) Math.round(i + shift), 0, (int) Math.round(i + shift), this.getHeight());
		}
		for (double i = (-gap * scaleY); i < this.getHeight() + (gap * scaleX); i += gap * scaleY) {
			double shift = -(offsetY) % (gap * scaleY) + (gap / 2.0 * scaleY);
			g.drawLine(0, (int) Math.round(i + shift), this.getWidth(), (int) Math.round(i + shift));
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		var t1 = System.nanoTime();
		reset(quality);
		Graphics2D renderContext = (Graphics2D) renderImage.getGraphics();
		renderContext.scale(quality, quality);
		drawGrid(renderContext);

		var p1 = screenToLocalPoint(getLocationOnScreen());
		var p2 = screenToLocalPoint(
				new Point(getLocationOnScreen().x + getWidth(), getLocationOnScreen().y + getHeight()));
		var dim = new Dimension(p2.x - p1.x, p2.y - p1.y);
		renderContext.setColor(Color.red);
//		renderContext.draw(new Rectangle(p1, dim));

		var inRect = componentGrid.getComponentsInRect(p1, dim).toArray();

		for (int i = inRect.length - 1; i >= 0; --i) {
			// System.out.println("layer: " + c.layer);
			var c = inRect[i];
			((ICanvasDrawable) c).update(renderContext);
		}

		// FOR DEBUGGING PURPOSE ///
//		File outputfile = new File("image.png");
//		try {
//			ImageIO.write(renderImage, "png", outputfile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}



		if (!g.drawImage(renderImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST), 0, 0, this)) {
			System.out.println("Not drawn");
		}
		var t2 = System.nanoTime();
		double rTime = (t2 - t1) / 1000000000d;
		double goalTime = 1d / 30d;
		System.out.println("Render time: " + rTime + " sec. Quality = " + quality + " Ratio = " + goalTime / rTime);
		quality = (Math.pow((goalTime / rTime), 3) + quality) / 2;
		quality = Math.max(0.01, Math.min(1, (quality)));
//		g2.dispose();

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		// var curr = ComponentListUI.last;
		// System.out.println("Local point: " +
		// screenToLocalPoint(e.getLocationOnScreen()));
//		while (curr != null) {
//			if (ComponentListUI.last.handleMouseEvent(e))
//				return;
//			else
//				curr = curr.prevInOrder;
//		}

		System.out.println(e.getLocationOnScreen());
		var t1 = System.nanoTime();
		var s = componentGrid.getTop(screenToLocalPoint(e.getLocationOnScreen()));

		var t2 = System.nanoTime();
		System.out.println("Time taken click: " + (t2 - t1) / 1000000000d);

		Render();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		var t = componentGrid.getTop(screenToLocalPoint(e.getLocationOnScreen()));
		if (t != null)
			((Component) t).dispatchEvent(e);

		lastClicked = screenToLocalPoint(e.getLocationOnScreen());
		System.out.println("Mouse pressed");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		mode = currentMode.NONE;
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

		var t = componentGrid.getTop(screenToLocalPoint(e.getLocationOnScreen()));
		if (t != null) {
			t.dispatchEvent(e);
		} else {
			System.out.println("Dragged canvas");
			int dx = screenToLocalPoint(e.getLocationOnScreen()).x - lastClicked.x;
			int dy = screenToLocalPoint(e.getLocationOnScreen()).y - lastClicked.y;
			offsetX -= dx * scaleX;
			offsetY -= dy * scaleY;
		}
		lastClicked = screenToLocalPoint(e.getLocationOnScreen());
		Render();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if (e.getWheelRotation() < 0) {
			zoom(e.getLocationOnScreen(), scaleX * 1.01);

		}
		if (e.getWheelRotation() > 0) {
			zoom(e.getLocationOnScreen(), scaleX / 1.01);

		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
