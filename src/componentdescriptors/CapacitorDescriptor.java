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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Backend.simulator.components.ACSource;
import Backend.simulator.components.Capacitor;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class CapacitorDescriptor extends DeviceUI {
	private double capacitance = 100d;
	private double current = 0;
	private DeviceUI uiComp;

	public CapacitorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public CapacitorDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		

		super(canvas, "components/capacitor.png", 100, 100, 
				new Point[] { new Point(45, 0), new Point(-45, 0) 
				}, "Capacitor");
				addAnimator(new Animable() {
					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

					@Override
					public void animate(Graphics g) {
						Graphics2D gx = (Graphics2D) g.create();
						gx.translate(50, 50);
						gx.setColor(Color.white);
						Animable.writeCenteredText(NumericUtilities.getPrefixed(capacitance, 4) + "C",
								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 40));
						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -50));
						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -50, null);
						gx.dispose();
					}
				});
		this.setLocation(position);
		
//		super(canvas, position, "Capacitor");
//		this.uiComp = new DeviceUI(canvas, "components/capacitor.png", 100, 100, this,
//				new Point[] { new Point(45, 0), new Point(-45, 0) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(50, 50);
//						gx.setColor(Color.white);
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(capacitance, 4) + "C",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 40));
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
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		LogarithmicSlider resval = new LogarithmicSlider(-9,9,4);

		resval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				CapacitorDescriptor.this.capacitance= NumericUtilities.getRounded(resval.getLogValue(),4);
				canvas.Render();
			}
		});
		parent.add(resval);


		setDefaultFormat(parent);
		parent.revalidate();
		parent.repaint();
		System.out.println("here");
	}
	@Override
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current =(double) data.get(ACSource.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Capacitor.CAPACITANCE, capacitance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
