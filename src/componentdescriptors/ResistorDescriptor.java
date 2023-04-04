package componentdescriptors;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import uiPackage.Animable;
import uiPackage.DeviceUI;
import uiPackage.ICanvasDrawable;
import uiPackage.IComponentDescriptor;
import uiPackage.RenderingCanvas;
import uiPackage.ResourceManager;
import utilities.NumericUtilities;

public class ResistorDescriptor extends IComponentDescriptor {
	private double resistance = 0.00001000000d;
	private double current = 0;
	private DeviceUI uiComp;
	public ResistorDescriptor(RenderingCanvas canvas) throws IOException {
		this(canvas, new Point(0,0));
	}
	public ResistorDescriptor(RenderingCanvas canvas, Point position) throws IOException {
		super(canvas);
		this.uiComp= new DeviceUI(canvas, "components/resistor.png", 200, 100, this,
				new Point[] { new Point(95, 0), new Point(-95, 0) }, new Animable() {
					private BufferedImage arrow = ResourceManager.loadImage("arrow.png", 0).get(0);
					@Override
					public void animate(Graphics g) {

						Graphics2D gx = (Graphics2D) g.create();
						//set center point
						gx.translate(100, 50);
						//***********
						gx.setColor(Color.white);
						gx.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
						
						Animable.writeCenteredText("hi friends", new Font(Font.SANS_SERIF,Font.PLAIN, 25), gx, new Point(0,0));
//						gx.drawString(NumericUtilities.getPrefixed(current, 4) + "A", 40, 110);
//						arrow

						gx.drawImage(arrow,0,0,null);
						gx.dispose();
					}
				});
		uiComp.setLocation(position);
	}

	@Override
	public void displayProperties(JComponent parent) {
		// TODO Auto-generated method stub
		parent.removeAll();
		JLabel restag = new JLabel("Resistance: ");
		parent.add(restag);
		JTextField resval = new JTextField();
		resval.setText(Double.toString(resistance));
		parent.add(resval);

		JLabel lol = new JLabel("Open: ");
		parent.add(lol);
		JCheckBox b = new JCheckBox();

		parent.add(b);

		setDefaultFormat(parent);
		parent.revalidate();
		parent.repaint();
		System.out.println("here");
	}

}
