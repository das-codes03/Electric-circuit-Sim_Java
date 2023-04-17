package componentdescriptors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import circuitlogic.solver.devices.ACSource;
import circuitlogic.solver.devices.Resistor;
import simulatorgui.rendering.Animable;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.LogarithmicSlider;
import simulatorgui.rendering.RenderingCanvas;
import utilities.NumericUtilities;

public class ResistorDescriptor extends DeviceUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4730436519437429724L;
	private double resistance = 0.1d;
	private double current = 0;
	public ResistorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public ResistorDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/resistor.png", 100, 50, new Point[] { new Point(45, 0), new Point(-45, 0) },
				"Resistor");
		addAnimator(new Animable() {

			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(resistance, 4) + "Ω", Animable.globalFont, gx,
						new Point(0, 30));
				Animable.drawArrow(gx, 0, -25, current, 0.0);
				gx.dispose();
			}
		});
		this.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel title = new JLabel("RESISTOR");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);

		JLabel restag = new JLabel("Resistance = " + NumericUtilities.getPrefixed(resistance, 4) + "Ω");
		restag.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(restag);
		LogarithmicSlider resval = new LogarithmicSlider(-9, 9, 4, "Ω");
		resval.setLogValue(resistance);
		resval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ResistorDescriptor.this.resistance = NumericUtilities.getRounded(resval.getLogValue(), 4);
				canvas.Render();
				restag.setText("Resistance = " + NumericUtilities.getPrefixed(resistance, 4) + "Ω");
			}
		});
		parent.add(resval);
		parent.revalidate();
		parent.repaint();
	}

	@Override
	public void writeState(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public HashMap<String, Object> readProperties() {
		HashMap<String, Object> data = new HashMap<>();
		data.put(Resistor.RESISTANCE, resistance);
		return data;
	}

	@Override
	public void writeProperties(HashMap<String, Object> data) {
		resistance = (double) data.get(Resistor.RESISTANCE);
	}
}
