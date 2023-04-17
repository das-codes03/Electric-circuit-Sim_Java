package componentdescriptors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import circuitlogic.solver.devices.Switch;
import simulatorgui.rendering.Animable;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.RenderingCanvas;

public class SwitchDescriptor extends DeviceUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 524692668486187094L;
	private boolean closed = true;
	private double current = 0;

	public SwitchDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public SwitchDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/switch.png", 100, 50, new Point[] { new Point(45, 0), new Point(-45, 0) }, "Switch");
		addAnimator(new Animable() {
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);

				if (closed) {
					gx.setStroke(new BasicStroke(4));
					gx.drawLine(-20, 0, 20, 0);
				} else {
					gx.setStroke(new BasicStroke(4));
					gx.drawLine(-20, 0, 15, -15);
					gx.fillOval(15, -21, 8, 8);
				}
				if (closed) {
					Animable.drawArrow(gx, 0, -15, current, 0.0);
				}
				gx.dispose();
			}
		});
		this.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel title = new JLabel("SWITCH");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);
		{

			JCheckBox closedVal = new JCheckBox("Switch is " + (closed ? "closed" : "open"));
			closedVal.setAlignmentX(CENTER_ALIGNMENT);
			closedVal.setSelected(closed);
			closedVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					SwitchDescriptor.this.closed = closedVal.isSelected();
					canvas.Render();
					closedVal.setText("Switch is " + (closed ? "closed" : "open"));
				}
			});
			parent.add(closedVal);
		}
		parent.revalidate();
		parent.repaint();
	}

	@Override
	public void writeState(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(Switch.CURRENT);
	}

	@Override
	public HashMap<String, Object> readProperties() {
		HashMap<String, Object> data = new HashMap<>();
		data.put(Switch.CLOSED, closed);
		return data;
	}

	@Override
	public void writeProperties(HashMap<String, Object> data) {
		closed = (boolean) data.get(Switch.CLOSED);
	}
}
