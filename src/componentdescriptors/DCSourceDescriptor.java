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

import Backend.api.Components.ACSource;
import Backend.api.Components.DCSource;
import Backend.api.Components.Resistor;
import frontend.SimUiManager;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class DCSourceDescriptor extends DeviceUI {
	private double emf = 100d;
	private double current = 0;
	private DeviceUI uiComp;

	public DCSourceDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public DCSourceDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/dcsource.png", 100, 100, new Point[] { new Point(45, 0), new Point(-45, 0) },
				"DCSource");
		addAnimator(new Animable() {
			private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(
						NumericUtilities.getPrefixed(emf, 4) + "v",
						Animable.globalFont, gx, new Point(0, 40));
				Animable.writeCenteredText(NumericUtilities.getPrefixed(Math.abs(current), 4) + "A",
						Animable.globalFont, gx, new Point(0, -50));
				Animable.drawArrow(gx, 0, -33, current, 0.0);
				String dir = "+  -";
				if (emf < 0) {
					dir = "-  +";
				}
				Animable.writeCenteredText(dir, new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
				
				gx.dispose();
			}
		});
		this.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		LogarithmicSlider resval = new LogarithmicSlider(-9, 9, 4);

		resval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				DCSourceDescriptor.this.emf = NumericUtilities.getRounded(resval.getLogValue(), 4);
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
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), DCSource.EMF, emf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
