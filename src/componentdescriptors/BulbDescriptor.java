package componentdescriptors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Backend.api.Components.Bulb;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.LogarithmicSlider;
import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class BulbDescriptor extends DeviceUI {
	private Color color = new Color(0, 255, 0);
	private double intensity = 0;
	private double current = 0;
	private double ratedVoltage = 1;
	private double ratedWattage = 1;

	public BulbDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public BulbDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		super(canvas, "components/bulb.png", 100, 100, new Point[] { new Point(45, 0), new Point(-45, 0), }, "Bulb");
		addAnimator(new Animable() {
			private BufferedImage glow = ResourceManager.loadImage("glow.png", 0).get(0);

			private void drawGlow(Graphics2D g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(-glow.getWidth() / 2, -glow.getHeight() / 2);
				gx.drawImage(DeviceUI.applyAldebo(glow, color, intensity), -2, -2, null);
				gx.dispose();
			}

			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(getWidth() / 2, getHeight() / 2);
				drawGlow(gx);
				gx.setColor(Color.white);
				Animable.writeCenteredText(
						NumericUtilities.getPrefixed(ratedWattage, 3) + "W @"
								+ NumericUtilities.getPrefixed(ratedVoltage, 3) + "V",
						Animable.globalFont, gx, new Point(0, 40));
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
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		LogarithmicSlider resval = new LogarithmicSlider(-9, 9, 4);

		resval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				BulbDescriptor.this.ratedWattage = NumericUtilities.getRounded(resval.getLogValue(), 4);
				canvas.Render();
			}
		});
		parent.add(resval);
		parent.revalidate();
		parent.repaint();
		System.out.println("here");
	}

	@Override
	public void updateAttributes(HashMap<String, Object> data) {
		current = (double) data.get(Bulb.CURRENT);
		intensity = (double) data.get(Bulb.INTENSITY);
	}

	@Override
	public void revalidateProperties(SimulationEvent evt) {
		try {
			evt.sim.setProperty(getID(), Bulb.RATED_VOLTAGE, ratedVoltage);
			evt.sim.setProperty(getID(), Bulb.RATED_WATTAGE, ratedWattage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}