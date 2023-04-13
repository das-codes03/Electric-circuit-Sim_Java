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
import Backend.api.Components.Inductor;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class InductorDescriptor extends DeviceUI {
	private double inductance = 100d;
	private double current = 0;
	private DeviceUI uiComp;

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
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Inductor.INDUCTANCE, inductance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
