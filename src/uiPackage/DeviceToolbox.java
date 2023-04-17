package uiPackage;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;
import frontend.Driver;

public class DeviceToolbox extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6594884275642585494L;
	private JButton rotLeftBtn;
	private JButton rotRightBtn;
	private JButton deleteBtn;
	private CanvasDrawable currSelected;
	public DeviceToolbox(CanvasDrawable device) {
		currSelected = device;
		this.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
		this.setLayout(new GridLayout());
		if(currSelected instanceof DeviceUI) {
			this.setSize(new Dimension(150, 50));
			var leftRotimg = ResourceManager.loadImage("rotateleft.png", 0).get(0).getScaledInstance(30, 30,
					Image.SCALE_SMOOTH);
			var rightRotimg = ResourceManager.loadImage("rotateright.png", 0).get(0).getScaledInstance(-30, 30,
					Image.SCALE_SMOOTH);
			var deleteImg = ResourceManager.loadImage("delete.png", 0).get(0).getScaledInstance(-30, 30,
					Image.SCALE_SMOOTH);
			rotLeftBtn = new JButton(new ImageIcon(leftRotimg));
			rotLeftBtn.setToolTipText("Rotate anticlockwise (45 degrees)");
			rotLeftBtn.setBackground(getBackground());
			rotRightBtn = new JButton(new ImageIcon(rightRotimg));
			rotRightBtn.setToolTipText("Rotate clockwise (45 degrees)");
			rotRightBtn.setBackground(getBackground());
			deleteBtn = new JButton(new ImageIcon(deleteImg));
			deleteBtn.setToolTipText("Discard component");
			deleteBtn.setBackground(getBackground());
			
			this.add(rotLeftBtn);
			rotLeftBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((DeviceUI) currSelected).setRotation((int)Math.round(((DeviceUI) currSelected).getRotation()-45));
					Driver.getDriver().Render();
				}
			});
			this.add(rotRightBtn);
			rotRightBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					((DeviceUI) currSelected).setRotation((int)Math.round(((DeviceUI) currSelected)
							.getRotation()+45));
					Driver.getDriver().Render();
				}
			});
			this.add(deleteBtn);
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Driver.getDriver().deleteComponent((DeviceUI) currSelected);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
					}
					currSelected = null;
					Driver.getDriver().Render();
					getParent().remove(DeviceToolbox.this);
					Driver.getDriver().mainWin.refreshDescription();
				}
			});
		}else if(currSelected instanceof Wire) {
			this.setSize(new Dimension(50, 50));
			var deleteImg = ResourceManager.loadImage("delete.png", 0).get(0).getScaledInstance(-30, 30,
					Image.SCALE_SMOOTH);
			deleteBtn = new JButton(new ImageIcon(deleteImg));
			deleteBtn.setToolTipText("Discard component");
			deleteBtn.setBackground(getBackground());
			this.add(deleteBtn);
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Driver.getDriver().deleteWire((Wire)currSelected);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
					}
					currSelected = null;
					Driver.getDriver().Render();
					getParent().remove(DeviceToolbox.this);
					Driver.getDriver().mainWin.refreshDescription();
				}
			});
		}
	}
}
