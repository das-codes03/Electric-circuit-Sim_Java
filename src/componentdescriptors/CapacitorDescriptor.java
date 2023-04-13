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
import Backend.api.Components.Capacitor;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class CapacitorDescriptor extends DeviceUI {
	private double capacitance = 100d;
	private double breakdownVoltage = 1e6;
	private double current = 0;

	public CapacitorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public CapacitorDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/capacitor.png", 100, 100, new Point[] { new Point(45, 0), new Point(-45, 0) },
				"Capacitor");
		addAnimator(new Animable() {
			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				gx.setColor(Color.white);
				Animable.writeCenteredText(NumericUtilities.getPrefixed(capacitance, 3) + "F", Animable.globalFont, gx,
						new Point(0, 40));
		
				Animable.drawArrow(gx, 0, -33, current, 0.0);
				gx.dispose();
			}
		});
		this.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();
		JLabel title = new JLabel("CAPACITOR");
		title.setForeground(Color.green);
		title.setAlignmentX(CENTER_ALIGNMENT);
		parent.add(title);
		{
			JLabel capTag = new JLabel("Capacitance = " + NumericUtilities.getPrefixed(capacitance, 4) + "F");
			capTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(capTag);
			LogarithmicSlider capVal = new LogarithmicSlider(-12, 6, 4, "F");
			capVal.setLogValue(capacitance);
			capVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					CapacitorDescriptor.this.capacitance = NumericUtilities.getRounded(capVal.getLogValue(), 4);
					canvas.Render();
					capTag.setText("Capacitance = " + NumericUtilities.getPrefixed(capacitance, 4) + "F");
				}
			});
			parent.add(capVal);
		}
		{
			JLabel bkdnTag = new JLabel("Breakdown Voltage = " + NumericUtilities.getPrefixed(breakdownVoltage, 4) + "V");
			bkdnTag.setAlignmentX(CENTER_ALIGNMENT);
			parent.add(bkdnTag);
			LogarithmicSlider bkdnVal = new LogarithmicSlider(-5, 12, 4, "V");
			bkdnVal.setLogValue(breakdownVoltage);
			bkdnVal.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					CapacitorDescriptor.this.breakdownVoltage = NumericUtilities.getRounded(bkdnVal.getLogValue(), 4);
					canvas.Render();
					bkdnTag.setText("Breakdown Voltage =  = " + NumericUtilities.getPrefixed(breakdownVoltage, 4) + "V");
				}
			});
			parent.add(bkdnVal);
		}
		parent.revalidate();
		parent.repaint();
	}

	@Override
	public void updateAttributes(HashMap<String, Object> data) {
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Capacitor.CAPACITANCE, capacitance);
			evt.sim.setProperty(getID(), Capacitor.BREAKDOWN_VOLTAGE, breakdownVoltage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
