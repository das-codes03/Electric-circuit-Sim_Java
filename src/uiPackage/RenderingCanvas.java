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
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import frontend.MainWindow;
import utilities.NumericUtilities;
import frontend.Driver;

public class RenderingCanvas extends JPanel implements MouseInputListener, MouseWheelListener {

	enum currentMode {
		MOVE_CANVAS, DRAG_COMPONENT, MAKE_WIRE, NONE
	}

	Color gridColor = new Color(20, 30, 20);
	Color secondaryGridColor = new Color(10, 20, 10);
	private static final long serialVersionUID = 6803922477054275835L;
	private MainWindow mw;
	/** Transformation of the camera. */
	private AffineTransform camTransform;
	/** Last clicked point in local space. */
	Point lastClicked;
	currentMode mode;
	CanvasDrawable currSelected = null;
	// minimum layer till now
	private long minLayer;
	/** Size of each box in component grid. */
	private int boxSize = 200;

	// **Higher level, low quality, better performance.*/
	double lodMultiplier = 5;
	/** Component grid divisions. */
	public ComponentMapping objectsMap;

	/** Bring this component to top. */
	public void bringToFront(CanvasDrawable comp) {
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

	public void setCamTransform(AffineTransform at) {
		camTransform = at;
	}

	public AffineTransform getZoomToFit(int width, int height) {

		AffineTransform at = new AffineTransform();

		// get extreme boxes:
		var bnds = objectsMap.getBoundingRange();
		int minX = bnds[0];
		int minY = bnds[1];
		int maxX = bnds[2];
		int maxY = bnds[3];

		// compute size of rect
		double w = (maxX - minX + 1) * objectsMap.boxSize;
		double h = (maxY - minY + 1) * objectsMap.boxSize;

		// get target scale
		double tarScale = Math.min(width / w, height / h);

		// we need to get extra margin
		double margX = width / tarScale - w;
		double margY = height / tarScale - h;

//		double offX = (maxX-minX)/2.0;
		at.setToTranslation(minX * objectsMap.boxSize - margX / 2, minY * objectsMap.boxSize - margY / 2);

		at.scale(tarScale, tarScale);
		// next get offset
//		Render();
		return at;
	}

	/** Data structure to store components in grid. */
	class ComponentMapping {
		class MapBox {
			public MapBox(int x, int y) {
				this.components = new Vector<>();
				this.x = x;
				this.y = y;
				boxRect = generateRect(x, y);
			}

			public Rectangle getBoxRect() {
				return boxRect;
			}

			private final int x;
			private final int y;
			private final Rectangle boxRect;
			private final Vector<CanvasDrawable> components;
		}

		private Rectangle generateRect(int xi, int yi) {
			var rect = new Rectangle(xi * ComponentMapping.this.boxSize, yi * ComponentMapping.this.boxSize,
					ComponentMapping.this.boxSize, ComponentMapping.this.boxSize);
			return rect;
		}

		public ComponentMapping(int b) { // constructor
			this.boxSize = b;
			mMap = new HashMap<Point, MapBox>();
		}

		private final int boxSize;
		private final HashMap<Point, MapBox> mMap;

		public void remove(CanvasDrawable comp) {
			try {
				for (var b : comp.gridLocations) {
					b.components.remove(comp);
					if (b.components.isEmpty()) {
						discard(b.x, b.y);
					}
				}
			} catch (NullPointerException e) {
				System.err.println("Component not found!");
				e.printStackTrace();
			}
		}

		private void discard(int x, int y) {
			mMap.remove(new Point(x, y));
		}

		private MapBox set(int x, int y, CanvasDrawable comp) {
			MapBox box;
			Point pt = new Point(x, y);
			if ((box = mMap.get(pt)) == null) {
				mMap.put(pt, new MapBox(x, y));
				box = mMap.get(pt);
			}

			box.components.add(comp);
			return box;
		}

		public int[] getBoundingRange() {
			int[] coords = new int[4]; // minX, minY, maxX, maxY
			if (mMap.isEmpty())
				return coords;
			coords[0] = coords[1] = Integer.MAX_VALUE;
			coords[2] = coords[3] = Integer.MIN_VALUE;
			for (var b : mMap.keySet()) {
				coords[0] = Math.min(b.x, coords[0]);
				coords[1] = Math.min(b.y, coords[1]);
				coords[2] = Math.max(b.x, coords[2]);
				coords[3] = Math.max(b.y, coords[3]);
			}
			return coords;
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

		public void store(CanvasDrawable comp) {
			remove(comp);
			comp.gridLocations.clear();
			for (var x : comp.regions) {
				var bs = getOverlappingBoxCoordinates(x);
				for (var p : bs) {
					comp.gridLocations.add(set(p.x, p.y, comp));
				}
			}
			bringToFront(comp);
		}

		public Set<CanvasDrawable> getComponentsInRect(Point p, Dimension dimension) {
			var b1 = getBoxCoordinate(p);
			var b2 = getBoxCoordinate(
					new Point((int) (p.x + dimension.getWidth()), (int) (p.y + dimension.getHeight())));
			int x1 = b1.x;

			int y1 = b1.y;

			int x2 = (b2.x);

			int y2 = (b2.y);

			SortedSet<CanvasDrawable> temp = new TreeSet<>(new Comparator<CanvasDrawable>() {
				@Override
				public int compare(final CanvasDrawable o1, final CanvasDrawable o2) {
					return o1.compareTo(o2);
				}
			});
			Rectangle rect = new Rectangle(p, dimension);
			for (int i = x1; i <= x2; ++i) {
				for (int j = y1; j <= y2; ++j) {
					var s = mMap.get(new Point(i, j));
					if (s == null)
						continue;
					for (int index = 0; index < s.components.size(); ++index) {
						var c = s.components.get(index);
						for (var bnd : c.regions) {
							if (bnd.intersects(rect)) {
								temp.add(c);

							}
						}
					}
				}
			}
			return temp;
		}

		public CanvasDrawable getTop(Point localPoint) {
			var b = getBoxCoordinate(localPoint);
			CanvasDrawable found = null;
			var s = mMap.get(new Point(b.x, b.y));
			if (s == null)
				return null;
			for (var c : s.components) {
				if (c.isVisible())
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

	public void zoom(Point focus, double targetZoom) {
		Point center = screenToLocalPoint(focus);

		var relX = (center.x - camTransform.getTranslateX()) / camTransform.getScaleX();
		var relY = (center.y - camTransform.getTranslateY()) / camTransform.getScaleY();
		camTransform.translate(-relX, -relY);
		camTransform.scale(targetZoom, targetZoom);
		camTransform.translate(relX, relY);

		Render();
	}

	public void Render() {
		repaint();
	}

	public RenderingCanvas(MainWindow parent) {
		this.setFocusable(true);

		this.mw = parent;
		this.camTransform = new AffineTransform();
		this.minLayer = 0;
		this.setDoubleBuffered(true);
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.objectsMap = new ComponentMapping(boxSize);
	}

	int getLOD() {
		int l = -(int) Math.log(Math.min(camTransform.getScaleX() / lodMultiplier, 1));
		return l;
	}

	private void drawGrid(Graphics2D g, AffineTransform camTrans, int w, int h) {
		int basegap = 100;
		var scale = camTrans.getScaleX();
		var offX = camTrans.getTranslateX();
		var offY = camTrans.getTranslateY();
		int logzoom = (int) (Math.log(scale) / Math.log(2));
		int gap = (int) (basegap / Math.pow(2, logzoom));

		g.setColor(gridColor);

		for (double i = -gap * scale; i < w + gap * scale; i += gap * scale) {
			var shift = (-offX % gap) * scale;
			g.drawLine((int) Math.round(i + shift), 0, (int) Math.round(i + shift), h);
		}
		for (double i = -gap * scale; i < h + gap * scale; i += gap * scale) {
			var shift = (-offY % gap) * scale;
			g.drawLine(0, (int) Math.round(i + shift), w, (int) Math.round(i + shift));
		}
		g.setColor(secondaryGridColor);

		for (double i = -gap * scale; i < w + gap * scale; i += gap * scale) {
			var shift = (-offX % gap + gap / 2) * scale;
			g.drawLine((int) Math.round(i + shift), 0, (int) Math.round(i + shift), h);
		}
		for (double i = -gap * scale; i < h + gap * scale; i += gap * scale) {
			var shift = (-offY % gap + gap / 2) * scale;
			g.drawLine(0, (int) Math.round(i + shift), w, (int) Math.round(i + shift));
		}
	}

	public BufferedImage getSnapshot(AffineTransform camTrans, int w, int h) {
		BufferedImage renderImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D renderContext = (Graphics2D) renderImage.getGraphics();
		drawGrid(renderContext, camTrans, w, h);
		var p1 = new Point((int) camTrans.getTranslateX(), (int) camTrans.getTranslateY());
		var p2 = NumericUtilities.addPoint(p1,
				new Point((int) (w / camTrans.getScaleX()), (int) (h / camTrans.getScaleY())));
		var dim = new Dimension(p2.x - p1.x, p2.y - p1.y);
		renderContext.setColor(Color.red);

		var raw = objectsMap.getComponentsInRect(p1, dim);
		var inRect = raw.toArray(new CanvasDrawable[raw.size()]);

		renderContext.scale(camTrans.getScaleX(), camTrans.getScaleY());
		renderContext.translate(-camTrans.getTranslateX(), -camTrans.getTranslateY());
		for (int i = inRect.length - 1; i >= 0; --i) {
			var c = inRect[i];
			if (c.isVisible())
				c.update(renderContext);
		}
		return renderImage;
	}

	@Override
	public void paintComponent(Graphics g) {

//		reset(1);
//		Graphics2D renderContext = (Graphics2D) renderImage.getGraphics();
//		drawGrid(renderContext);
//
//		var p1 = screenToLocalPoint(getLocationOnScreen());
//		var p2 = screenToLocalPoint(
//				new Point(getLocationOnScreen().x + getWidth(), getLocationOnScreen().y + getHeight()));
//		var dim = new Dimension(p2.x - p1.x, p2.y - p1.y);
//		renderContext.setColor(Color.red);
//
//		var raw = objectsMap.getComponentsInRect(p1, dim);
//		var inRect = raw.toArray(new CanvasDrawable[raw.size()]);
//
//		renderContext.scale(camTransform.getScaleX(), camTransform.getScaleY());
//		renderContext.translate(-camTransform.getTranslateX(), -camTransform.getTranslateY());
//		for (int i = inRect.length - 1; i >= 0; --i) {
//			var c = inRect[i];
//			if (c.isVisible())
//				c.update(renderContext);
//		}

		g.drawImage(getSnapshot(camTransform, getWidth(), getHeight()), 0, 0, this);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.requestFocus();
		var children = getComponents();
		for (var c : children) {
			if (c instanceof DeviceToolbox) {
				remove(c);
			}
		}
		// get what was clicked
		var t = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));
		switch (mode) {
		case DRAG_COMPONENT: {

			break;
		}
		case MAKE_WIRE: {
			if (t instanceof NodeUI) {
				var w = ((Wire) currSelected);
				w.nodes.removeElementAt(w.nodes.size() - 1);

				w.addNode((NodeUI) t);

				currSelected = null;
				mode = currentMode.NONE;
			} else {
				var temp = new NodeUI(screenToLocalPoint(e.getLocationOnScreen()), this);
				temp.setVisible(false);
				((Wire) currSelected).nodes.lastElement().setVisible(true);
				((Wire) currSelected).addNode(temp);
			}
			break;
		}

		case NONE: {
			if (t == null) {
				mw.refreshDescription();
			} else {
				if (t instanceof NodeUI) {
					mode = currentMode.MAKE_WIRE;
					currSelected = new Wire(this);
					Driver.getDriver().addWire((Wire) currSelected);
					((Wire) currSelected).addNode((NodeUI) t);
					var temp = new NodeUI(screenToLocalPoint(e.getLocationOnScreen()), this);
					temp.setVisible(false);
					((Wire) currSelected).addNode(temp);
				} else if (t instanceof DeviceUI) {
					t.dispatchEvent(e);
				} else if (t instanceof Wire) {
					t.dispatchEvent(e);
				}
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + mode);
		}
		Render();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		var t = objectsMap.getTop(screenToLocalPoint(e.getLocationOnScreen()));
		if (t != null)
			((Component) t).dispatchEvent(e);
		lastClicked = screenToLocalPoint(e.getLocationOnScreen());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mode != currentMode.MAKE_WIRE) {
			mode = currentMode.NONE;
			currSelected = null;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
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
		if (e.getWheelRotation() < 0) {
			zoom(e.getLocationOnScreen(), 1.02);
		}
		if (e.getWheelRotation() > 0) {
			zoom(e.getLocationOnScreen(), 1 / 1.02);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (mode == currentMode.MAKE_WIRE) {
			((Wire) currSelected).nodes.lastElement().setLocation(screenToLocalPoint(e.getLocationOnScreen()));
			Render();
		}
	}
}
