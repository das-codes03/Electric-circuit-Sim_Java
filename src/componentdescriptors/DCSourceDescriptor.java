package componentdescriptors;

import java.awt.Color;
import java.awt.Font;
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
import circuitlogic.solver.devices.DCSource;
import simulatorgui.rendering.Animable;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.LogarithmicSlider;
import simulatorgui.rendering.RenderingCanvas;
import utilities.NumericUtilities;

public class DCSourceDescriptor extends DeviceUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2976824204947422602L;
	private double emf = 100d;
	private double current = 0;
	public DCSourceDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public DCSourceDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/dcsource.png", 100, 100, new Point[] { new Point(45, 0), new Point(-45, 0) },
				"DCSource");
		addAnimator(new Animable() {
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(emf, 4) + "v", Animable.globalFont, gx,
						new Point(0, 40));
				Animable.drawArrow(gx, 0, -33, current, 0.0);
				String dir = "+  -";
				if (emf > 0) {
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
		JLabel title = new JLabel("DC Source");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);
		{
			JLabel emfTag = new JLabel("Potential = " + NumericUtilities.getPrefixed(emf, 4) + "V");
			emfTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(emfTag);
			LogarithmicSlider emfVal = new LogarithmicSlider(-8, 8, 4, "V");
			emfVal.setLogValue(emf);
			emfVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					DCSourceDescriptor.this.emf = NumericUtilities.getRounded(emfVal.getLogValue(), 4);
					canvas.Render();
					emfTag.setText("Potential = " + NumericUtilities.getPrefixed(emf, 4) + "V");
				}
			});
			parent.add(emfVal);
		}
		parent.revalidate();
		parent.repaint();
	}

	@Override
	public void writeState(HashMap<String, Object> data, double t) {
		// TODO Auto-generated method stub
		current = (double) data.get(ACSource.CURRENT);
		this.t = t;
	}

	@Override
	public HashMap<String, Object> readProperties() {
		HashMap<String, Object> data = new HashMap<>();
		data.put(DCSource.EMF, emf);
		return data;
	}

	@Override
	public void writeProperties(HashMap<String, Object> data) {
		emf = (double) data.get(DCSource.EMF);
//		emf = (double) data.get("hi");
	}
}
