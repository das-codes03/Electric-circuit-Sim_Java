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

import javax.swing.event.MouseInputListener;

import uiPackage.RenderingCanvas.BiHashMap.MapBox;

public abstract class ICanvasDrawable extends Component implements MouseInputListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 744247388132276375L;

	abstract Rectangle getTransformedBounds();

	final IComponentDescriptor descr;
	protected Vector<MapBox> gridLocations;
	protected Vector<Shape> regions;
	private Color aldebo;

	public abstract int getPriority();

	public long layer;
	public RenderingCanvas canvas;

	public abstract void update(Graphics g);

	private static int getRGB(int x, int y, int[] pixels, int width, int height, boolean hasAlphaChannel) {
		int pixelLength = 3;
		if (hasAlphaChannel)
			pixelLength++;
		int pos = (x * pixelLength * width) + (y * pixelLength);

		int argb = -16777216; // 255 alpha
		if (hasAlphaChannel) {
			argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
		}

		argb += ((int) pixels[pos++] & 0xff); // blue
		argb += (((int) pixels[pos++] & 0xff) << 8); // green
		argb += (((int) pixels[pos++] & 0xff) << 16); // red
		return argb;
	}

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
if(i == 20181) {
	System.out.println("here");
}
			r = (int) (r * alRGB[0]);
			g = (int) (g * alRGB[1]);
			b = (int) (b * alRGB[2]);
			rgb = a<<24 | r<<16 | g<<8 | b;
			
			bts[i] = rgb;
		}
		return img;
//		for(int i = 0; i < img.getWidth(); ++i) {
//			for(int j = 0; j < img.getHeight(); ++j) {
////				img.getRGB(i, j);
//				var rgb = getRGB(i, j, data, img.getWidth(), img.getHeight()	, true);
//				int a = (rgb>>24)&0xff;
//				int r = (rgb>>16)&0xff;
//				int g = (rgb>>8)&0xff;
//				int b = rgb&0xff;
//				
//				r =(int)(r * (aldebo.getRed()/255.0));
//				g =(int)(g * (aldebo.getGreen()/255.0));
//				b =(int)(b * (aldebo.getBlue()/255.0));
//				rgb = a;
//				rgb = (rgb << 8) + r;
//				rgb = (rgb << 8) + g;
//				rgb = (rgb << 8) + b;
//				img.setRGB(i, j, rgb);
//			}
//		}
//		return img;
	}

	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	public int compareTo(ICanvasDrawable obj2) {
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

	public ICanvasDrawable(RenderingCanvas canvas, IComponentDescriptor desc) {
		this(canvas, desc, Color.white);
	}

	public ICanvasDrawable(RenderingCanvas canvas, IComponentDescriptor desc, Color aldebo) {
		this.setAldebo(aldebo);
		this.descr = desc;
		this.canvas = canvas;
		this.gridLocations = new Vector<>();
		this.regions = new Vector<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public ICanvasDrawable(RenderingCanvas canvas) {
		this(canvas, null, Color.white);
	}

	public void setAldebo(Color aldebo) {
		this.aldebo = aldebo;
	}

}
