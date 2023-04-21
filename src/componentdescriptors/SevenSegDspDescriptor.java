package componentdescriptors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;

import circuitlogic.solver.devices.SevenSegmentDisplay;
import simulatorgui.rendering.Animable;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.RenderingCanvas;
import utilities.ResourceManager;


public class SevenSegDspDescriptor extends DeviceUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3538924516145553712L;
	private double current = 0;
	private Color segmentColor = new Color(0, 255, 0);

	private final double[] intensities = new double[8];
	public SevenSegDspDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public SevenSegDspDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/sevensegment.png", 300, 400,
				new Point[] { new Point(-135, -143), new Point(-135, -109), new Point(-135, -75), new Point(-135, -41),
						new Point(-135, -7), new Point(-135, 27), new Point(-135, 61), new Point(-135, 95),
						new Point(122, 190), },
				"SevenSegmentDisplay");
		addAnimator(new Animable() {
			class segmentCoords {
				int x, y;
				double rotation;

				public segmentCoords(int x, int y, double rotation) {
					this.x = x;
					this.y = y;
					this.rotation = rotation;
				}
			}

			public final String segmentImgPath = "components/segment.png";
			public final Dimension segmentSize = new Dimension(25, 110);
			public final segmentCoords[] sCoords = new segmentCoords[] { new segmentCoords(0, -260, 90), // a
					new segmentCoords(60, -200, 0), // b
					new segmentCoords(60, -85, 0), // c
					new segmentCoords(0, -23, 90), // d
					new segmentCoords(-60, -85, 0), // e
					new segmentCoords(-60, -200, 0), // f
					new segmentCoords(0, -143, 90),// g
			};
			public final Image blankSegments = drawBlank();

			public Image drawBlank() {

				Image img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D gx = (Graphics2D) img.getGraphics();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(new Color(20, 40, 20));
				Image tempSegment = ResourceManager
						.applyAldebo(ResourceManager.loadImage(segmentImgPath, 0).get(0), gx.getColor(), 1)
						.getScaledInstance(segmentSize.width, segmentSize.height, Image.SCALE_SMOOTH);

				var org = gx.getTransform();
				for (var s : sCoords) {

					gx.translate(s.x + tempSegment.getWidth(null) / 2, s.y + tempSegment.getHeight(null) / 2);
					gx.rotate(Math.toRadians(s.rotation), tempSegment.getWidth(null) / 2,
							tempSegment.getHeight(null) / 2);
					gx.drawImage(tempSegment, 0, 0, null);
					gx.setTransform(org);

				}
				BufferedImage dotImg = ResourceManager.loadImage("components/dot.png", 0).get(0);
				gx.drawImage(ResourceManager.applyAldebo(dotImg, gx.getColor(), 1), 90, 85, null);
				gx.dispose();

				return img;
			}

			@Override
			public void animate(Graphics g) {

				Graphics2D gx = (Graphics2D) g.create();

				gx.drawImage(blankSegments, 0, 0, null);
				gx.translate(getWidth() / 2, getHeight() / 2);

				// draw the segments

				Animable.drawArrow(gx, 140, 155, current, 90);
				var org = gx.getTransform();
				for (int i = 0; i < sCoords.length; ++i) {
					var s = sCoords[i];
					Image tempSegment = ResourceManager
							.applyAldebo(ResourceManager.loadImage(segmentImgPath, 0).get(0), segmentColor,
									intensities[i])
							.getScaledInstance(segmentSize.width, segmentSize.height, Image.SCALE_SMOOTH);

					gx.translate(s.x + tempSegment.getWidth(null) / 2, s.y + tempSegment.getHeight(null) / 2);
					gx.rotate(Math.toRadians(s.rotation), tempSegment.getWidth(null) / 2,
							tempSegment.getHeight(null) / 2);
					gx.drawImage(tempSegment, 0, 0, null);
					gx.setTransform(org);

				}
				BufferedImage dotImg = ResourceManager.loadImage("components/dot.png", 0).get(0);
				gx.drawImage(ResourceManager.applyAldebo(dotImg, segmentColor, intensities[7]), 90, 85, null);			
				gx.dispose();
			}
		});
		this.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel title = new JLabel("SEVEN SEGMENT DISPLAY");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);
		parent.revalidate();
		parent.repaint();
	}

	@Override
	public void writeState(HashMap<String, Object> data, double t) {
		// TODO Auto-generated method stub
		for(int i = 0; i < intensities.length; ++i)
		{
			intensities[i] = (double) data.get(SevenSegmentDisplay.INTENSITIES[i]);
		}
		this.t = t;
		current = (double) data.get(SevenSegmentDisplay.CURRENT);
	}

	@Override
	public HashMap<String, Object> readProperties() {
		HashMap<String, Object> data= new HashMap<>();
		return data;

	}
	@Override
	public void writeProperties(HashMap<String, Object> data) {

	}
}
