package uiPackage;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Animable {
	abstract void animate(Graphics g);

	public static final Font globalFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

	public static void drawArrow(Graphics2D g, int x, int y, double magnitude, double rotation) {
		BufferedImage arrow = null;

		arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

		Graphics2D gx = (Graphics2D) g.create();
		gx.translate(x, y);

		gx.rotate(Math.toRadians(rotation));
		gx.scale(Math.signum(magnitude), 1);
		Image scaled = arrow.getScaledInstance(60, 30, Image.SCALE_FAST);
		gx.translate(-scaled.getWidth(null) / 2, -scaled.getHeight(null) / 2);
		gx.drawImage(scaled, 0, 0, null);
		gx.dispose();
	}

	public static void writeCenteredText(String s, Font font, Graphics2D g, Point pos) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int x = pos.x - metrics.stringWidth(s) / 2;
		// Determine the Y coordinate for the text
		int y = pos.y - metrics.getHeight() / 2 + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(s, x, y);
	}
}
