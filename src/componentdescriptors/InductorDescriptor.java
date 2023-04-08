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

public class InductorDescriptor extends DeviceUI {
	private double inductance = 100d;
	private double current = 0;
	private DeviceUI uiComp;

	public InductorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public InductorDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/inductor.png", 100, 50, new Point[] { new Point(45, 0), new Point(-45, 0) }, "Inductor");
		addAnimator(new Animable() {
			private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(50, 25);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(inductance, 4) + "H",
						new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 20));
				Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
						new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
				gx.scale(-1, 1);
				gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
				gx.dispose();
			}
		});
		this.setLocation(position);
//		super(canvas, position, "Diode");
//		this.uiComp = new DeviceUI(canvas, "components/inductor.png", 200, 100, this,
//				new Point[] { new Point(95, 0), new Point(-95, 0) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(100, 50);
//						gx.setColor(Color.white);
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(inductance, 4) + "H",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 20));
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
//						gx.scale(-1, 1);
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
//						gx.dispose();
//					}
//				});
//		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		JTextField resval = new JTextField();
		resval.setText(Double.toString(inductance));
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
	@Override
	public void updateAttributes(Object...o) {
		// TODO Auto-generated method stub
		inductance = (double) o[0];
	}
}
