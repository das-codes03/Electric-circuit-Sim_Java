package uiPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

public class DeviceToolbox extends JPanel {
	private JButton rotLeftBtn;
	private JButton rotRightBtn;
	private JButton deleteBtn;

	public DeviceToolbox() {
		this.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
		this.setSize(new Dimension(200, 50));
		this.setLayout(new GridLayout());
		var leftRotimg = ResourceManager.loadImage("rotateleft.png", 0).get(0).getScaledInstance(30, 30,
				Image.SCALE_SMOOTH);
		var rightRotimg = ResourceManager.loadImage("rotateright.png", 0).get(0).getScaledInstance(-30, 30,
				Image.SCALE_SMOOTH);
		var deleteImg = ResourceManager.loadImage("delete.png", 0).get(0).getScaledInstance(-30, 30,
				Image.SCALE_SMOOTH);
		var graphImg = ResourceManager.loadImage("graph.png", 0).get(0).getScaledInstance(-30, 30, Image.SCALE_SMOOTH);
//		Image
		rotLeftBtn = new JButton(new ImageIcon(leftRotimg));
		rotLeftBtn.setToolTipText("Rotate anticlockwise (45 degrees)");
		rotLeftBtn.setBackground(getBackground());
		rotRightBtn = new JButton(new ImageIcon(rightRotimg));
		rotRightBtn.setToolTipText("Rotate clockwise (45 degrees)");
		rotRightBtn.setBackground(getBackground());
		deleteBtn = new JButton(new ImageIcon(deleteImg));
		deleteBtn.setToolTipText("Discard component");
		deleteBtn.setBackground(getBackground());
		JToggleButton jtgl = new JToggleButton(new ImageIcon(graphImg));
		jtgl.setToolTipText("Plot graph");
		jtgl.setBackground(new Color(0,50,0));
	
		
		this.add(jtgl);
		jtgl.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		this.add(rotLeftBtn);
		rotLeftBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.add(rotRightBtn);
		rotRightBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.add(deleteBtn);
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
