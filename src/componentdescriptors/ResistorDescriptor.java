package componentdescriptors;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Backend.simulator.components.ACSource;
import Backend.simulator.components.Inductor;
import Backend.simulator.components.Resistor;
import frontend.SimUiManager;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class ResistorDescriptor extends DeviceUI {
	private double resistance = 0.1d;
	private double current = 0;
	private DeviceUI uiComp;
	public ResistorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0,0));
	}
	public ResistorDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		
		super(canvas, "components/resistor.png", 100, 50, new Point[] { new Point(45, 0), new Point(-45, 0) }, "Resistor");
		addAnimator(new Animable() {
			
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth()/2, getHeight()/2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(resistance, 4) + "Ω",
						Animable.globalFont, gx, new Point(0, 30));
				Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
						Animable.globalFont, gx, new Point(0, -40));
//				String dir = "+  -";
//				if(emf < 0) {
//					dir = "-  +";
//				}
//				Animable.writeCenteredText(dir,
//						new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
//				gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -40, null);
				Animable.drawArrow(gx,0,-25,current,0.0);
				gx.dispose();
			}
		});
		this.setLocation(position);
		
//		super(canvas, position, "Resistor");
//		this.uiComp= new DeviceUI(canvas, "components/resistor.png", 100, 50, this,
//				new Point[] { new Point(45, 0), new Point(-45, 0) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(50, 25);
//						gx.setColor(Color.white);
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(resistance, 4) + "Ω",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, 30));
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -40));
////						String dir = "+  -";
////						if(emf < 0) {
////							dir = "-  +";
////						}
////						Animable.writeCenteredText(dir,
////								new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -40, null);
//						gx.dispose();
//					}
//				});
//		uiComp.setLocation(position);
////		uiComp.setRotation(45);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
//		parent.addlis
		
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		LogarithmicSlider resval = new LogarithmicSlider(-9,9,4);

		resval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				ResistorDescriptor.this.resistance= NumericUtilities.getRounded(resval.getLogValue(),4);
				canvas.Render();
			}
		});
		parent.add(resval);

		
//		setDefaultFormat(parent);
		parent.revalidate();
		parent.repaint();

	}
	@Override
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current =(double) data.get(ACSource.CURRENT);
	}
	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Resistor.RESISTANCE, resistance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
