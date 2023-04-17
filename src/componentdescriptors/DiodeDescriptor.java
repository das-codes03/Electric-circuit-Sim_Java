package componentdescriptors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import circuitlogic.solver.devices.ACSource;
import simulatorgui.rendering.Animable;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.RenderingCanvas;

public class DiodeDescriptor extends DeviceUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6213115296806224488L;
	private double current = 0;
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
				gx.dispose();
			}
		});
		this.setLocation(position);
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
	public void writeState(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public HashMap<String, Object> readProperties() {
		HashMap<String, Object> data= new HashMap<>();
		return data;
	}
	@Override
	public void writeProperties(HashMap<String, Object> data) {
		
	}
}
