package uiPackage;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
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

	public static ArrayList<BufferedImage> loadImage(String path, int uptoLOD) throws IOException {
		File f = new File(parentFolder + path);
		if (!images.containsKey(f)) {
			var lods = new ArrayList<BufferedImage>();
			BufferedImage temp = ImageIO.read(ResourceManager.class.getResource(parentFolder+path));
			for (int i = 0; i <= uptoLOD; ++i) {
				lods.add(getLODimage(temp, i));
			}
			images.put(f, lods);
		}
		var gotImgs = images.get(f);
		for (int i = gotImgs.size(); i <= uptoLOD; ++i) {
			gotImgs.add(getLODimage(gotImgs.get(0), i));
		}

		System.out.println("Image count = " + images.size());
		return images.get(f);
	}

}
