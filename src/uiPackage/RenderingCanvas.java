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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class RenderingCanvas extends JPanel implements MouseInputListener, MouseWheelListener {

	enum currentMode {
		MOVE_CANVAS, DRAG_COMPONENT, MAKE_WIRE, NONE
	}

	/** Here screen will be stored. */
	private BufferedImage renderImage;

	/** Transformation of the camera. */
	AffineTransform camTransform;
	/** Last clicked point in local space. */
	Point lastClicked;
	currentMode mode;
	ICanvasDrawable currSelected = null;
	// minimum layer till now
	private long minLayer;
	/** Size of each box in component grid. */
	private int boxSize = 100;
	double quality = 1;
	/** Component grid divisions. */
	public BiHashMap objectsMap;

	/** Bring this component to top. */
	public void bringToFront(ICanvasDrawable comp) {
		comp.layer = minLayer - 1;
		minLayer--;
	}

	/** Transform screenspace point to renderspace */
	public Point screenToLocalPoint(Point worldPoint) {
		Point local = new Point();
		local.x = (int) ((worldPoint.x - getLocationOnScreen().x) / camTransform.getScaleX()
				+ camTransform.getTranslateX());
		local.y = (int) ((worldPoint.y - getLocationOnScreen().y) / camTransform.getScaleY()
				+ camTransform.getTranslateY());
		return local;
	}

	/** Data structure to store components in grid. */
	class BiHashMap {
		class MapBox {
			public MapBox(int x, int y) {
				this.components = new ArrayList<>();
				boxRect = generateRect(x, y);
			}

			public Rectangle getBoxRect() {
				return boxRect;
			}

			private final Rectangle boxRect;
			private final ArrayList<ICanvasDrawable> components;
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

		private Set<Point> getOverlappingBoxCoordinates(Shape sh) {
			Set<Point> o = new HashSet<>();
			var bn = sh.getBounds();
			Point b = getBoxCoordinate(new Point((int) Math.round(bn.getCenterX()), (int) Math.round(bn.getCenterY())));
			recurFind(sh, b, o, true, true, true, true);
			return o;
		}

		private Point getBoxCoordinate(Point locationOnCanvas) {
			int x = locationOnCanvas.x / boxSize - (locationOnCanvas.x < 0 ? 1 : 0);
			int y = locationOnCanvas.y / boxSize - (locationOnCanvas.y < 0 ? 1 : 0);
			return new Point(x, y);
		}

		public void store(ICanvasDrawable comp) {
			for (var b : comp.gridLocations) {
				b.components.remove(comp);
			}
			comp.gridLocations.clear();
			for (var x : comp.regions) {
				var bs = getOverlappingBoxCoordinates(x);
				for (var p : bs) {
					comp.gridLocations.add(set(p.x, p.y, comp));
				}
			}
			bringToFront(comp);
		}

		public ArrayList<ICanvasDrawable> getComponentsInRect(Point p, Dimension dimension) {
			var b1 = getBoxCoordinate(p);
			var b2 = getBoxCoordinate(
					new Point((int) (p.x + dimension.getWidth()), (int) (p.y + dimension.getHeight())));
			int x1 = b1.x - 1;

			int y1 = b1.y - 1;

			int x2 = (b2.x + 1);

			int y2 = (b2.y + 1);

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
			temp.sort(new Comparator<ICanvasDrawable>() {
				@Override
				public int compare(final ICanvasDrawable o1, final ICanvasDrawable o2) {
					return o1.compareTo(o2);
				}
			});
			return temp;
		}

		public ICanvasDrawable getTop(Point localPoint) {
			var b = getBoxCoordinate(localPoint);
			ICanvasDrawable found = null;
			var s = getBox(b.x, b.y);
			if (s == null)
				return null;
			for (var c : s.components) {
				for (var bnd : c.regions) {
					if (bnd.contains(localPoint))
						if (found == null) {
							found = c;
						} else if (found.compareTo(c) == 1) {
							found = c;
						}
				}
			}
			return found;
		}
	}

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
		Point center = screenToLocalPoint(focus);

		var relX = (center.x - camTransform.getTranslateX()) / camTransform.getScaleX();
		var relY = (center.y - camTransform.getTranslateY()) / camTransform.getScaleY();
//		var ratio = camTransform.getScaleX()
		camTransform.translate(-relX, -relY);
		camTransform.scale(targetZoom, targetZoom);
		camTransform.translate(relX, relY);

		Render();
	}

	public void Render() {
		repaint();
	}

	public RenderingCanvas() {
		this.camTransform = new AffineTransform();
		this.minLayer = 0;
		this.setDoubleBuffered(true);
		renderImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.objectsMap = new BiHashMap(boxSize);
	}

	Color gridColor = new Color(0, 50, 0);
	Color secondaryGridColor = new Color(00, 30, 00);
//	public double scaleX = 1;
//	public double scaleY = 1;
//	public int offsetX = 0;
//	public int offsetY = 0;
	private static final long serialVersionUID = 6803922477054275835L;

	private void drawGrid(Graphics2D g) {
		int basegap = 100;
		var scale = camTransform.getScaleX();
		var offX = camTransform.getTranslateX();
		var offY = camTransform.getTranslateY();
		int logzoom = (int) (Math.log(scale) / Math.log(2));
		int gap = (int) (basegap / Math.pow(2, logzoom));

		g.setColor(gridColor);

		for (double i = (-offX % gap) * scale; i < getWidth() / scale; i += gap * scale) {
			g.drawLine((int) Math.round(i), 0, (int) Math.round(i), this.getHeight());
		}
		for (double i = (-offY % gap) * scale; i < getHeight() / scale; i += gap * scale) {
			g.drawLine(0, (int) Math.round(i), this.getWidth(), (int) Math.round(i));
		}
		g.setColor(secondaryGridColor);

		for (double i = (-offX % gap + gap / 2) * scale; i < getWidth() / scale; i += gap * scale) {
			g.drawLine((int) Math.round(i), 0, (int) Math.round(i), this.getHeight());
		}
		for (double i = (-offY % gap + gap / 2) * scale; i < getHeight() / scale; i += gap * scale) {
			g.drawLine(0, (int) Math.round(i), this.getWidth(), (int) Math.round(i));
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

		var inRect = objectsMap.getComponentsInRect(p1, dim).toArray();

		renderContext.scale(camTransform.getScaleX(), camTransform.getScaleY());
		renderContext.translate(-camTransform.getTranslateX(), -camTransform.getTranslateY());

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

		var t = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));
		if (t == null) {
			if (mode == currentMode.MAKE_WIRE) {
				var temp = new NodeUI(this);
				temp.setRadius(3);
				temp.setLocation(screenToLocalPoint(e.getLocationOnScreen()));
				((Wire) currSelected).addNode(temp);
				return;
			}
		} else if (t instanceof NodeUI) {
			if (mode == currentMode.MAKE_WIRE) {
				if (t == ((Wire) currSelected).nodes.lastElement()) {
					objectsMap.remove(t);
					var temp = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));

					if (temp instanceof NodeUI) {
						((Wire) currSelected).nodes.remove(t);
						((Wire) currSelected).addNode((NodeUI) temp);
						currSelected = null;
						mode = currentMode.NONE;
					} else {
						objectsMap.store(t);
						temp = new NodeUI(this);
						temp.setLocation(screenToLocalPoint(e.getLocationOnScreen()));
						((Wire) currSelected).addNode((NodeUI) temp);
					}
				}
			} else {
				mode = currentMode.MAKE_WIRE;
				Wire w = new Wire(this);
				w.addNode((NodeUI) t);
				currSelected = w;
				var temp = new NodeUI(this);
				temp.setLocation(screenToLocalPoint(e.getLocationOnScreen()));
				w.addNode(temp);
			}
		}

		Render();
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

//		System.out.println(e.getLocationOnScreen());
//		var t1 = System.nanoTime();
//		var s = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));
//		System.out.println("Offset: " + camTransform.getTranslateX());
//		var t2 = System.nanoTime();
//		System.out.println("Time taken click: " + (t2 - t1) / 1000000000d);

		// Render();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		var t = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));
		if (t != null)
			((Component) t).dispatchEvent(e);

		lastClicked = screenToLocalPoint(e.getLocationOnScreen());
		System.out.println("Mouse pressed");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if (mode != currentMode.MAKE_WIRE) {
			mode = currentMode.NONE;
			currSelected = null;
		}
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
		mouseMoved(e);
		if (currSelected != null) {
			currSelected.dispatchEvent(e);
		} else {
			var t = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));
			if (t != null && mode != currentMode.MOVE_CANVAS) {
				t.dispatchEvent(e);
			} else {
				mode = currentMode.MOVE_CANVAS;
				System.out.println("Dragged canvas");
				int dx = screenToLocalPoint(e.getLocationOnScreen()).x - lastClicked.x;
				int dy = screenToLocalPoint(e.getLocationOnScreen()).y - lastClicked.y;
				camTransform.translate(-dx / camTransform.getScaleX(), -dy / camTransform.getScaleY());
			}

		}
		lastClicked = screenToLocalPoint(e.getLocationOnScreen());
		Render();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if (e.getWheelRotation() < 0) {
			zoom(e.getLocationOnScreen(), 1.02);
		}
		if (e.getWheelRotation() > 0) {
			zoom(e.getLocationOnScreen(), 1 / 1.02);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if (mode == currentMode.MAKE_WIRE) {
			((Wire) currSelected).nodes.lastElement().setLocation(screenToLocalPoint(e.getLocationOnScreen()));
			Render();
		}
//		if (mode == currentMode.MAKE_WIRE) {
//			((Wire) currSelected).nodes.lastElement().setLocation(screenToLocalPoint(e.getLocationOnScreen()));
//			Render();
//		}
	}
}
