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

import uiPackage.RenderingCanvas.ComponentMapping.MapBox;

public abstract class CanvasDrawable extends JComponent implements MouseInputListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 744247388132276375L;

	abstract Rectangle getTransformedBounds();

	protected Vector<MapBox> gridLocations;
	protected Vector<Shape> regions;
	private Color aldebo;

	public abstract int getPriority();

	public long layer;
	public RenderingCanvas canvas;

	public abstract void update(Graphics g);

	public static BufferedImage applyAldebo(BufferedImage original, Color aldebo) {
		return applyAldebo(original, aldebo, 1);
	}

	public static BufferedImage applyAldebo(BufferedImage original, Color aldebo, double intensity) {
		BufferedImage img = copyImage(original);
		float[] alHSB = new float[3];
//		Color.RGBtoHSB(aldebo.getRed(), aldebo.getGreen(), aldebo.getBlue(), alHSB);
		double[] alRGB = new double[] { aldebo.getRed() / 255.0, aldebo.getGreen() / 255.0, aldebo.getBlue() / 255.0 };
		// apply intensity and clamp
//		alRGB[0] = Math.max(Math.min(alRGB[0] * intensity, 255), 0);
//		alRGB[1] = Math.max(Math.min(alRGB[1] * intensity, 255), 0);
//		alRGB[2] = Math.max(Math.min(alRGB[2] * intensity, 255), 0);

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
			Color.RGBtoHSB(r, g, b, alHSB);
			if(i == bts.length/2) {
				//hi
				System.out.println("hi");
			}
			alHSB[2] *= intensity;
			a =(int) (Math.pow(a/255.0, 1/intensity)*255.0);
			//normalize
			if(intensity > 1) {
			alHSB[0]/=alHSB[2];
			alHSB[1]/=alHSB[2];
			alHSB[2]=1;
			}
//			rgb = a << 24;
			var c = new Color( Color.HSBtoRGB(alHSB[0], alHSB[1], alHSB[2]));
//			rgb = a << 24 |
			rgb = a << 24 | c.getRed() << 16 | c.getGreen() << 8 | c.getBlue();

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



	public CanvasDrawable(RenderingCanvas canvas) {
		this.canvas = canvas;
		this.gridLocations = new Vector<>();
		this.regions = new Vector<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}


	public void setAldebo(Color aldebo) {
		this.aldebo = aldebo;
	}

}
