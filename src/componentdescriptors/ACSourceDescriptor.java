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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Backend.api.Components.ACSource;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class ACSourceDescriptor extends DeviceUI {
	private double amplitude = 100d;
	private double frequency = 1;
	private double phase = 0;
	private double current = 0;

	public ACSourceDescriptor(RenderingCanvas canvas, int ID) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public ACSourceDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		super(canvas, "components/acsource.png", 100, 100, new Point[] { new Point(45, 0), new Point(-45, 0) },
				"ACSource");
		addAnimator(new Animable() {
			private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(amplitude, 4) + "V", Animable.globalFont, gx,
						new Point(0, 40));
				Animable.writeCenteredText(NumericUtilities.getPrefixed(Math.abs(current), 4) + "A",
						Animable.globalFont, gx, new Point(0, -50));
				Animable.drawArrow(gx, 0, -33, current, 0.0);
				gx.dispose();
			}
		});

		this.setLocation(position);

	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel ampTag = new JLabel("Amplitude: ");
		parent.add(ampTag);
		LogarithmicSlider ampval = new LogarithmicSlider(-9, 9, 4);
		ampval.setLogValue(amplitude);
		ampval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				ACSourceDescriptor.this.amplitude = NumericUtilities.getRounded(ampval.getLogValue(), 4);
				canvas.Render();
			}
		});
		parent.add(ampval);

		JLabel fretag = new JLabel("Frequency: ");
		parent.add(fretag);
		LogarithmicSlider freval = new LogarithmicSlider(-9, 9, 4);
		freval.setLogValue(frequency);
		freval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				ACSourceDescriptor.this.frequency = NumericUtilities.getRounded(freval.getLogValue(), 4);
				canvas.Render();
			}
		});
		parent.add(freval);

		JLabel phtag = new JLabel("Phase: ");
		parent.add(phtag);
		JSlider phVal = new JSlider(0, 359);
		phVal.setValue((int) phase);
		phVal.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				ACSourceDescriptor.this.phase = phVal.getValue();
				canvas.Render();
			}
		});
		parent.add(phVal);

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
			evt.sim.setProperty(getID(), ACSource.AMPLITUDE, amplitude);
			evt.sim.setProperty(getID(), ACSource.FREQUENCY, frequency);

			evt.sim.setProperty(getID(), ACSource.PHASE, phase);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
