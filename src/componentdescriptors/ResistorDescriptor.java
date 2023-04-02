package componentdescriptors;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import uiPackage.IComponentDescriptor;

public class ResistorDescriptor implements IComponentDescriptor {
	private double resistance = 0.000001;
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
