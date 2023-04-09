package componentdescriptors;

import java.awt.BasicStroke;
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

import Backend.simulator.components.ACSource;
import Backend.simulator.components.Inductor;
import frontend.SimulationEvent;
import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.CanvasDrawable;

import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class SwitchDescriptor extends DeviceUI {
	private boolean open = false;
	private double current = 0;
	private DeviceUI uiComp;

	public SwitchDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0, 0));
	}

	public SwitchDescriptor(RenderingCanvas canvas, Point position) throws IOException {

		super(canvas, "components/switch.png", 100, 50, new Point[] { new Point(45, 0), new Point(-45, 0) }, "Switch");
		addAnimator( new Animable() {
			private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);

			@Override
			public void animate(Graphics g) {
				Graphics2D gx = (Graphics2D) g.create();
				gx.translate(50, 25);
				gx.setColor(Color.white);
				
//				String dir = "+  -";
//				if(emf < 0) {
//					dir = "-  +";
//				}
				if(!open) {
					gx.setStroke(new BasicStroke(4));
					gx.drawLine(-20, 0, 20, 0);
				}else {
					gx.setStroke(new BasicStroke(4));
					gx.drawLine(-20, 0, 15, -15);
					gx.fillOval(15, -21, 8, 8);
				}
				if(!open) {
				Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
						new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -30));
//				Animable.writeCenteredText(dir,
//						new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
				gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -30, null);
				}
				gx.dispose();
			}
		});
		this.setLocation(position);
//		super(canvas, position, "Switch");
//		this.uiComp = new DeviceUI(canvas, "components/switch.png", 100, 50, this,
//				new Point[] { new Point(45, 0), new Point(-45, 0) }, new Animable() {
//					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
//
//					@Override
//					public void animate(Graphics g) {
//						Graphics2D gx = (Graphics2D) g.create();
//						gx.translate(50, 25);
//						gx.setColor(Color.white);
//						
////						String dir = "+  -";
////						if(emf < 0) {
////							dir = "-  +";
////						}
//						if(!open) {
//							gx.setStroke(new BasicStroke(4));
//							gx.drawLine(-20, 0, 20, 0);
//						}else {
//							gx.setStroke(new BasicStroke(4));
//							gx.drawLine(-20, 0, 15, -15);
//							gx.fillOval(15, -21, 8, 8);
//						}
//						if(!open) {
//						Animable.writeCenteredText(NumericUtilities.getPrefixed(current, 4) + "A",
//								new Font(Font.SANS_SERIF, Font.PLAIN, 15), gx, new Point(0, -30));
////						Animable.writeCenteredText(dir,
////								new Font(Font.SANS_SERIF, Font.PLAIN, 25), gx, new Point(0, 0));
//						gx.drawImage(arrow.getScaledInstance(60, 30, Image.SCALE_SMOOTH), -30, -30, null);
//						}
//						gx.dispose();
//					}
//				});
//		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		parent.removeAll();


		JLabel lol = new JLabel("Open: ");
		parent.add(lol);
		JCheckBox b = new JCheckBox();
		b.setSelected(open);
		b.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				SwitchDescriptor.this.open = b.isSelected();
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
		current =(double) data.get(ACSource.CURRENT);
	}
	@Override
	public void revalidateProperties(SimulationEvent evt) {
//		try {
//			evt.sim.setProperty(getID(), Inductor.INDUCTANCE, inductance);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
