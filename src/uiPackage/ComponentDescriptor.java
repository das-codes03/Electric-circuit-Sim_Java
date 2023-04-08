package uiPackage;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public abstract class ComponentDescriptor {
	public ComponentDescriptor(RenderingCanvas canvas, Point pos, String typeName) {
		this.typeName = typeName;
		this.ID = 0;
		// TODO Auto-generated constructor stub
		this.canvas = canvas;
		
	}
	private final String typeName;
	public String getTypeName() {
		return typeName;
	}
	private final int ID;
	public abstract void updateAttributes(Object...o);
	private RenderingCanvas canvas;
	public abstract void displayProperties(JComponent parent);
	protected void setDefaultFormat(JComponent parent) {

		parent.add(new JLabel(""));

		var comps = parent.getComponents();
		
		for (var c : comps) {
			c.setBackground(parent.getBackground());
			c.setForeground(Color.green);
			if (c instanceof JTextField) {
				((JTextField) c).setCaretColor(Color.yellow);
				c.setBackground(Color.black);
			}	
		}
	}
}
