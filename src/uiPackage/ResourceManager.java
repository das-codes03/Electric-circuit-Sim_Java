package uiPackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ResourceManager {
	private static HashMap<File, ArrayList<BufferedImage>> images;
	private static String parentFolder = "/resources/";
	static {
		images = new HashMap<>();
	}

	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	public static BufferedImage applyAldebo(BufferedImage original, Color aldebo, double intensity) {
		BufferedImage img = copyImage(original);
		float[] alHSB = new float[3];
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
			Color.RGBtoHSB(r, g, b, alHSB);
			alHSB[2] *= intensity;
			a =(int) (Math.pow(a/255.0, 1/intensity)*255.0);
			//normalize
			if(intensity > 1) {
			alHSB[0]/=alHSB[2];
			alHSB[1]/=alHSB[2];
			alHSB[2]=1;
			}
			var c = new Color( Color.HSBtoRGB(alHSB[0], alHSB[1], alHSB[2]));
			rgb = a << 24 | c.getRed() << 16 | c.getGreen() << 8 | c.getBlue();
			bts[i] = rgb;
		}
		return img;
	}
	private static BufferedImage getLODimage(BufferedImage original, int LODlevel) {
		var x = original.getWidth();
		var y = original.getHeight();
		x = (int) Math.ceil(x / Math.pow(2, LODlevel));
		y = (int) Math.ceil(y / Math.pow(2, LODlevel));
		Image toolkitImage = original.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		int w = toolkitImage.getWidth(null);
		int h = toolkitImage.getHeight(null);

		// width and height are of the toolkit image
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		g.drawImage(toolkitImage, 0, 0, null);
		g.dispose();
		return newImage;
	}

	public static ArrayList<BufferedImage> loadImage(String path, int uptoLOD) {
		File f = new File(parentFolder + path);
		if (!images.containsKey(f)) {
			var lods = new ArrayList<BufferedImage>();
			BufferedImage temp = null;
			try {
				temp = ImageIO.read(ResourceManager.class.getResource(parentFolder + path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i <= uptoLOD; ++i) {
				lods.add(getLODimage(temp, i));
			}
			images.put(f, lods);
		}
		var gotImgs = images.get(f);
		for (int i = gotImgs.size(); i <= uptoLOD; ++i) {
			gotImgs.add(getLODimage(gotImgs.get(0), i));
		}

		return images.get(f);
	}

}
