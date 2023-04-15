package componentdescriptors;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Backend.api.Components.ACSource;
import Backend.api.Components.DCSource;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class DiodeDescriptor extends DeviceUI {

	private double current = 0;
	private DeviceUI uiComp;

	public DiodeDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public DiodeDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/diode.png", 100, 100, new Point[] { new Point(45, 0), new Point(-45, 0) }, "Diode");
		addAnimator(new Animable() {
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(50, 50);
				gx.setColor(Color.white);

				Animable.drawArrow(gx, 0, -30, current, 0.0);
//				Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//						new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
////				String dir = "+  -";
//				if(emf < 0) {
//					dir = "-  +";
//				}
//				Animable.writeCenteredText(dir,
//						new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
//				gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
				gx.dispose();
			}
		});
		this.setLocation(position);

//		super(canvas, position, "Diode");
//		this.uiComp= new DeviceUI(canvas, "components/diode.png", 100, 100, this,
//				new Point[] { new Point(45, 0), new Point(-45, 0) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(50, 50);
//						gx.setColor(Color.white);
//
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
////						String dir = "+  -";
////						if(emf < 0) {
////							dir = "-  +";
////						}
////						Animable.writeCenteredText(dir,
////								new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
//						gx.dispose();
//					}
//				});
//		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();

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
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
//		try {
//			evt.sim.setProperty(getID(), DCSource.EMF, emf);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
