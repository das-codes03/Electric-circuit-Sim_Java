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

public class TransformerDescriptor extends ComponentDescriptor {
	private int primary = 12;
	private int secondary = 220;
	private double primarycurrent = 0;
	private double secondarycurrent = 0;
	private DeviceUI uiComp;

	public TransformerDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public TransformerDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		super(canvas, position);
		this.uiComp = new DeviceUI(canvas, "components/transformer.png", 200, 200, this,
				new Point[] { new Point(95, -87), new Point(-95, -87), new Point(95, 87), new Point(-95, 87) }, new Animable() {
					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

					@Override
					public void animate(Graphics g) {
						Graphics2D gx = (Graphics2D) g.create();
						gx.translate(100, 100);
						gx.setColor(Color.white);
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(emf, 4) + "V",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 40));
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
						gx.rotate(Math.toRadians(90));
						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -25, -100, null);
						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -25, 70, null);

						gx.dispose();
					}
				});
		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		JTextField resval = new JTextField();
		resval.setText(Integer.toString(primary));
		parent.add(resval);

		JLabel lol = new JLabel("Open: ");
		parent.add(lol);
		JCheckBox b = new JCheckBox();

		parent.add(b);

		setDefaultFormat(parent);
		parent.revalidate();
		parent.repaint();
		System.out.println("here");
	}

}
