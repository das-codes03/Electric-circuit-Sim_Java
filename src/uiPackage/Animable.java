package uiPackage;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

public interface Animable {
	abstract void animate(Graphics g);

	public static void writeCenteredText(String s, Font font, Graphics g, Point pos) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int x =pos.x- metrics.stringWidth(s) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java
		int y = pos.y-metrics.getHeight() / 2 + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(s, x, y);
	}
}
