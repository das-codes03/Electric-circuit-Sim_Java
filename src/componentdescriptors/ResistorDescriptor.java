package componentdescriptors;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Backend.api.Components.ACSource;
import Backend.api.Components.Inductor;
import Backend.api.Components.Resistor;
import frontend.Driver;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class ResistorDescriptor extends DeviceUI {
	private double resistance = 0.1d;
	private double current = 0;
	private DeviceUI uiComp;

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
	public void updateAttributes(HashMap<String, Object> data) {
		// TODO Auto-generated method stub
		current = (double) data.get(ACSource.CURRENT);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Resistor.RESISTANCE, resistance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
