package uiPackage;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public interface IComponentDescriptor {
	public abstract void displayProperties(JComponent parent);

	default void setDefaultFormat(JComponent parent) {
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
