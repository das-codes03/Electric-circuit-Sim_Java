package componentdescriptors;

import java.awt.Color;
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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Backend.api.Components.Transformer;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class TransformerDescriptor extends DeviceUI {
	private int primary = 100;
	private int secondary = 1000;
	private double primarycurrent = 0;
	private double secondarycurrent = 0;
	private double primInductance = 0.1;
	private DeviceUI uiComp;

	public TransformerDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public TransformerDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/transformer.png", 200, 200,
				new Point[] { new Point(-95, -87), new Point(-95, 87), new Point(95, 87), new Point(95, -87) },
				"Transformer");
		addAnimator(new Animable() {
			private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(100, 100);
				gx.setColor(Color.orange);
				
				Animable.drawArrow(gx, -70,0,primarycurrent, 270);
				Animable.drawArrow(gx, 70,0,secondarycurrent, 90);
				gx.rotate(Math.toRadians(90));
				Animable.writeCenteredText(primary + ":" + secondary,globalFont, gx, new Point(0,0));
				gx.dispose();
			}
		});
		this.setLocation(position);

//		super(canvas, position, "Transformer");
//		this.uiComp = new DeviceUI(canvas, "components/transformer.png", 200, 200, this,
//				new Point[] { new Point(95, -87), new Point(-95, -87), new Point(95, 87), new Point(-95, 87) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(100, 100);
//						gx.setColor(Color.white);
////						Animable.writeCenteredText(NumericUtilities.getPrefixed(emf, 4) + "V",
////								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 40));
////						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
////								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
//						gx.rotate(Math.toRadians(90));
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -25, -100, null);
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -25, 70, null);
//
//						gx.dispose();
//					}
//				});
//		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel title = new JLabel("TRANSFORMER");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);
		{
			JLabel primTag = new JLabel("Primary turns = " + primary);
			primTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(primTag);
			JSlider primVal = new JSlider(1, 1000);
			primVal.setValue(primary);
			primVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					TransformerDescriptor.this.primary = primVal.getValue();
					canvas.Render();
					primTag.setText("Primary turns = " + primary);
				}
			});
			parent.add(primVal);
		}
		{
			JLabel secTag = new JLabel("Secondary turns = " + secondary);
			secTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(secTag);
			JSlider secVal = new JSlider(1, 1000);
			secVal.setValue(secondary);
			secVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					TransformerDescriptor.this.secondary = secVal.getValue();
					canvas.Render();
					secTag.setText("Secondary turns = " + secondary);
				}
			});
			parent.add(secVal);
		}
		{
			JLabel indTag = new JLabel("Primary inductance = " + NumericUtilities.getPrefixed(primInductance, 4) + "H");
			indTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(indTag);
			LogarithmicSlider indVal = new LogarithmicSlider(-9, 6, 4, "H");
			indVal.setLogValue(primInductance);
			indVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					TransformerDescriptor.this.primInductance = NumericUtilities.getRounded(indVal.getLogValue(), 4);
					canvas.Render();
					indTag.setText("Primary inductance = " + NumericUtilities.getPrefixed(primInductance, 4) + "H");
				}
			});
			parent.add(indVal);
		}
		parent.revalidate();
		parent.repaint();
	}

	@Override
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		primarycurrent =(double) data.get(Transformer.PRIMARY_CURRENT);
		secondarycurrent =(double) data.get(Transformer.SECONDARY_CURRENT);
		
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Transformer.PRIMARY_WINDINGS, primary);
			evt.sim.setProperty(getID(), Transformer.SECONDARY_WINDINGS, secondary);
			evt.sim.setProperty(getID(), Transformer.PRIMARY_INDUCTANCE, primInductance);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
