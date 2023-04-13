package componentdescriptors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

import Backend.api.Components.ACSource;
import Backend.api.Components.Switch;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.RenderingCanvas;
import utilities.NumericUtilities;

public class SwitchDescriptor extends DeviceUI {
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
					Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
							new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -30));
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

		JLabel lol = new JLabel("Open: ");
		parent.add(lol);
		JCheckBox b = new JCheckBox();
		b.setSelected(closed);
		b.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SwitchDescriptor.this.closed = b.isSelected();
				canvas.Render();
			}
		});

		parent.add(b);

		setDefaultFormat(parent);
		parent.revalidate();
		parent.repaint();
		System.out.println("here");
	}

	@Override
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(Switch.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Switch.CLOSED, closed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
