package componentdescriptors;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.CanvasDrawable;
import uiPackage.ComponentDescriptor;
import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class BulbDescriptor extends DeviceUI {
	private Color color = new Color(0, 255, 0);
	private double intensity = 1;
	private double current = 0;

	public BulbDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public BulbDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		
		
		super(canvas, "components/bulb.png", 100, 100, 
				new Point[] { new Point(45, 0), new Point(-45, 0) , 
				}, "Bulb");
				addAnimator(new Animable() {
					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
					private BufferedImage glow = ResourceManager.loadImage("glow.png", 0).get(0);

					private void drawGlow(Graphics2D g) {
						Graphics2D gx = (Graphics2D) g.create();
						gx.translate(-glow.getWidth() / 2, -glow.getHeight() / 2);
						gx.drawImage(DeviceUI.applyAldebo(glow, color, intensity), -2, -2, null);
						gx.dispose();
					}

					@Override
					public void animate(Graphics g) {
						Graphics2D gx = (Graphics2D) g.create();
						gx.translate(50, 50);
						drawGlow(gx);

						gx.setColor(Color.white);
						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
						gx.dispose();
					}
				});
		this.setLocation(position);
//		super(canvas, position, "Bulb");
//		this.uiComp = new DeviceUI(canvas, "components/bulb.png", 100, 100, this,
//				new Point[] { new Point(45, 0), new Point(-45, 0) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//					private BufferedImage glow = ResourceManager.loadImage("glow.png", 0).get(0);
//
//					private void drawGlow(Graphics2D g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(-glow.getWidth() / 2, -glow.getHeight() / 2);
//						gx.drawImage(DeviceUI.applyAldebo(glow, color, intensity), -2, -2, null);
//						gx.dispose();
//					}
//
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(50, 50);
//						drawGlow(gx);
//
//						gx.setColor(Color.white);
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
//						gx.dispose();
//					}
//				});
//		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		updateAttributes(2.0);
		JLabel lol = new JLabel("Open: ");
		parent.add(lol);
		JCheckBox b = new JCheckBox();

		parent.add(b);

		setDefaultFormat(parent);
		parent.revalidate();
		parent.repaint();
		System.out.println("here");
	}

	@Override
	public void updateAttributes(Object... o) {
		// TODO Auto-generated method stub
		intensity = (double) o[0];
	}
}
