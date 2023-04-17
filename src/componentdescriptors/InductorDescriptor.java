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
import circuitlogic.solver.devices.Inductor;
import simulatorgui.rendering.Animable;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.LogarithmicSlider;
import simulatorgui.rendering.RenderingCanvas;
import utilities.NumericUtilities;

public class InductorDescriptor extends DeviceUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4201550227967687816L;
	private double inductance = 100d;
	private double current = 0;
	public InductorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public InductorDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/inductor.png", 100, 50, new Point[] { new Point(45, 0), new Point(-45, 0) },
				"Inductor");
		addAnimator(new Animable() {
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(inductance, 3) + "H", Animable.globalFont, gx,
						new Point(0, 20));
				Animable.drawArrow(gx, 0, -25, current, 0.0);
				gx.dispose();
			}
		});
		this.setLocation(position);

	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel title = new JLabel("INDUCTOR");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);
		{
			JLabel indTag = new JLabel("Inductance = " + NumericUtilities.getPrefixed(inductance, 4) + "H");
			indTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(indTag);
			LogarithmicSlider indVal = new LogarithmicSlider(-9, 6, 4, "H");
			indVal.setLogValue(inductance);
			indVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					InductorDescriptor.this.inductance = NumericUtilities.getRounded(indVal.getLogValue(), 4);
					canvas.Render();
					indTag.setText("Inductance = " + NumericUtilities.getPrefixed(inductance, 4) + "H");
				}
			});
			parent.add(indVal);
		}
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
		data.put(Inductor.INDUCTANCE, inductance);
		return data;
	}

	@Override
	public void writeProperties(HashMap<String, Object> data) {
		inductance = (double) data.get(Inductor.INDUCTANCE);
	}
}
