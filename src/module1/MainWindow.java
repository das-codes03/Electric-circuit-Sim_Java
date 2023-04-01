package module1;

import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.miginfocom.swing.MigLayout;
import uiPackage.RenderingCanvas;
import java.awt.Font;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.ScrollPane;

public class MainWindow {

	/**
	 * @wbp.parser.entryPoint
	 */

	public static RenderingCanvas renderCanvas;
	public String playButtonImgPath = "/resources/playpause.png";
	public String stopButtonImgPath = "/resources/stop.png";
	public Color c1 = Color.black;
	public Color c2 = Color.DARK_GRAY;
	public Color c3 = Color.GRAY;
	public Color c4 = Color.lightGray;
	/**
	 * @wbp.parser.entryPoint
	 */
	public MainWindow() {
//		UIDefaults uiDefaults = UIManager.getDefaults();
////		uiDefaults.put("activeCaption", new javax.swing.plaf.ColorUIResource(Color.gray));
////		uiDefaults.put("activeCaptionText", new javax.swing.plaf.ColorUIResource(Color.white));
//		JFrame.setDefaultLookAndFeelDecorated(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("SplitPane.background", Color.DARK_GRAY);
			UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(1, 0,
			        0, 0));
			UIManager.put("TabbedPane.unselectedBackground", Color.GRAY);
//			UIManager.put("TabbedPane.borderHightlightColor",   Color.RED );
//			UIManager.put("TabbedPane.darkShadow",  Color.RED );
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame f = new JFrame();
		// f.setUndecorated(true);
		f.setBackground(Color.WHITE);
		f.getContentPane().setBackground(c2);
		f.getContentPane().setLayout(
				new MigLayout("ins 0, wrap 0, gap rel 0", "[455.00px,grow,fill]", "[775.00px,grow,shrink 0,fill]"));

		Panel contentPane = new Panel();
		contentPane.setBackground(c2);
		contentPane.setForeground(Color.RED);
		f.getContentPane().add(contentPane, "cell 0 0,grow");
		contentPane.setLayout(new MigLayout("ins 0, wrap 0, gap rel 0", "[grow]", "[grow]"));

		JTabbedPane simulationInstances = new JTabbedPane(JTabbedPane.TOP);
		simulationInstances.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		simulationInstances.setBackground(c2);
//		Insets insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
//		insets.top = -2;
//		insets.left=insets.right=insets.bottom  = 0;
//		UIManager.put("TabbedPane.contentBorderInsets", insets);

		contentPane.add(simulationInstances, "cell 0 0,grow");

		JPanel simSpace = new JPanel();
		simSpace.setBorder(null);
		simSpace.setBackground(Color.DARK_GRAY);
		simSpace.setForeground(new Color(0, 0, 0));
		simulationInstances.addTab("Simulator", null, simSpace, null);
//		simulationInstances.setTabComponentAt(0, new ButtonTabComponent(pane));
//	    pane.setBackgroundAt(i, Color.getHSBColor((float)i/tabNumber, 1, 1));
		simSpace.setLayout(new MigLayout("ins 0, wrap 0, gap rel 0", "[346.00,grow,fill]", "[grow]"));

		JSplitPane VerticalPane = new JSplitPane();
		VerticalPane.setBorder(null);
		VerticalPane.setDividerSize(5);

		VerticalPane.setForeground(Color.BLACK);
		// splitPane_1.setBorder(Border);
		VerticalPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		simSpace.add(VerticalPane, "cell 0 0,grow");

		JSplitPane HorizontalPane = new JSplitPane();
		HorizontalPane.setBorder(null);
		HorizontalPane.setDividerSize(5);
		VerticalPane.setLeftComponent(HorizontalPane);
		HorizontalPane.setBackground(Color.DARK_GRAY);
		HorizontalPane.setForeground(Color.DARK_GRAY);

		JPanel ToolboxPane = new JPanel();
		ToolboxPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		ToolboxPane.setBackground(new Color(64, 66, 88));
		HorizontalPane.setLeftComponent(ToolboxPane);
		ToolboxPane.setLayout(new BoxLayout(ToolboxPane, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBackground(c2);
		ToolboxPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel lblNewLabel = new JLabel("Simulator Controls");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		lblNewLabel.setBackground(new Color(255, 128, 64));
		lblNewLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		lblNewLabel.setForeground(new Color(255, 128, 64));
		panel.add(lblNewLabel);

		JPanel panel_3 = new JPanel();
		panel_3.setSize(new Dimension(80, 40));
		panel_3.setMinimumSize(new Dimension(60, 30));
		panel_3.setMaximumSize(new Dimension(80, 40));
		panel_3.setPreferredSize(new Dimension(80, 40));
		panel_3.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel_3.setBackground(c2);
		panel.add(panel_3);

		// JButton stopButton = new JButton("");
		BufferedImage buttonIcon = null;
		try {
			buttonIcon = ImageIO.read(getClass().getResource(stopButtonImgPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		panel_3.setLayout(new GridLayout(0, 2, 0, 0));
		//panel_3.setLayout(null);
		JButton stopButton = new JButton(new ImageIcon( buttonIcon.getScaledInstance(33,30,BufferedImage.SCALE_FAST)));
		stopButton.setBorder(null);
		stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		stopButton.setContentAreaFilled(false);
		stopButton.setBounds(0, 0, 30, 30);
		stopButton.setBorderPainted(false);
		//stopButton.setContentAreaFilled(false);
		stopButton.setMinimumSize(new Dimension(30, 30));
		stopButton.setMaximumSize(new Dimension(30, 30));

		stopButton.setBackground(c2);
		stopButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		stopButton.setForeground(new Color(128, 0, 0));
		panel_3.add(stopButton);

		try {
			buttonIcon = ImageIO.read(getClass().getResource(playButtonImgPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JButton playButton = new JButton(new ImageIcon( buttonIcon.getScaledInstance(33,30,BufferedImage.SCALE_FAST)));
		playButton.setBorder(null);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		playButton.setContentAreaFilled(false);
		playButton.setBounds(30, 0, 30, 30);
		playButton.setBorderPainted(false);
		//playButton.setContentAreaFilled(false);
		playButton.setMinimumSize(new Dimension(30, 30));
		playButton.setMaximumSize(new Dimension(30, 30));
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.setBackground(c2);
		playButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		playButton.setForeground(new Color(128, 0, 0));
		panel_3.add(playButton);

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(c2);
		panel.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));

		JLabel lblNewLabel_1 = new JLabel("Simulation Speed");
		lblNewLabel_1.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		panel_4.add(lblNewLabel_1);

		JSlider slider = new JSlider();
		slider.setMajorTickSpacing(200);
		slider.setMinorTickSpacing(50);
		slider.setPaintTicks(true);
		slider.setMaximum(1000);
		slider.setValue(500);
		
		slider.setBackground(c2);
		panel_4.add(slider);
		
		JLabel lblNewLabel_3 = new JLabel("Speed : 1.00x");
		lblNewLabel_3.setForeground(new Color(0, 255, 0));
		lblNewLabel_3.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setHorizontalTextPosition(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_3);

		JPanel panel_5 = new JPanel();
		panel_5.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel_5.setBackground(c2);
		panel.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_4 = new JLabel("RUNNING");
		lblNewLabel_4.setFont(new Font("Verdana", Font.BOLD, 15));
		lblNewLabel_4.setForeground(new Color(0, 255, 0));
		lblNewLabel_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_5.add(lblNewLabel_4);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		ToolboxPane.add(panel_1);
		panel_1.setBackground(c2);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_2 = new JLabel("Add Device");
		lblNewLabel_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_2.setFont(new Font("Verdana", Font.BOLD, 11));
		lblNewLabel_2.setForeground(new Color(255, 255, 255));
		panel_1.add(lblNewLabel_2);
		
		JPanel panel_6 = new JPanel();
		panel_1.add(panel_6);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_6.add(scrollPane);
		
		JTree tree = new JTree();
		tree.setBackground(new Color(0, 0, 0));
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("JTree") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("colors");
						node_1.add(new DefaultMutableTreeNode("blue"));
						node_1.add(new DefaultMutableTreeNode("violet"));
						node_1.add(new DefaultMutableTreeNode("red"));
						node_1.add(new DefaultMutableTreeNode("yellow"));
						node_1.add(new DefaultMutableTreeNode("Pink"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("sports");
						node_1.add(new DefaultMutableTreeNode("basketball"));
						node_1.add(new DefaultMutableTreeNode("soccer"));
						node_1.add(new DefaultMutableTreeNode("football"));
						node_1.add(new DefaultMutableTreeNode("hockey"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("food");
						node_1.add(new DefaultMutableTreeNode("hot dogs"));
						node_1.add(new DefaultMutableTreeNode("pizza"));
						node_1.add(new DefaultMutableTreeNode("ravioli"));
						node_1.add(new DefaultMutableTreeNode("bananas"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("sports");
						node_1.add(new DefaultMutableTreeNode("basketball"));
						node_1.add(new DefaultMutableTreeNode("soccer"));
						node_1.add(new DefaultMutableTreeNode("football"));
						node_1.add(new DefaultMutableTreeNode("hockey"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("food");
						node_1.add(new DefaultMutableTreeNode("hot dogs"));
						node_1.add(new DefaultMutableTreeNode("pizza"));
						node_1.add(new DefaultMutableTreeNode("ravioli"));
						node_1.add(new DefaultMutableTreeNode("bananas"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("sports");
						node_1.add(new DefaultMutableTreeNode("basketball"));
						node_1.add(new DefaultMutableTreeNode("soccer"));
						node_1.add(new DefaultMutableTreeNode("football"));
						node_1.add(new DefaultMutableTreeNode("hockey"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("food");
						node_1.add(new DefaultMutableTreeNode("hot dogs"));
						node_1.add(new DefaultMutableTreeNode("pizza"));
						node_1.add(new DefaultMutableTreeNode("ravioli"));
						node_1.add(new DefaultMutableTreeNode("bananas"));
					add(node_1);
				}
			}
		));
		if (tree.getCellRenderer() instanceof DefaultTreeCellRenderer)
		{
		    final DefaultTreeCellRenderer renderer = 
		        (DefaultTreeCellRenderer)(tree.getCellRenderer());
		    renderer.setBackgroundNonSelectionColor(Color.black);
		    renderer.setBackgroundSelectionColor(Color.DARK_GRAY);
		    renderer.setTextNonSelectionColor(Color.white);
		    renderer.setTextSelectionColor(Color.green);
		}
		else
		{
		    System.err.println("Sorry, no special colors today.");
		}
		scrollPane.setViewportView(tree);
		
		JPanel panel_2 = new JPanel();
		panel_2.setMinimumSize(new Dimension(100, 100));
		panel_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBackground(c2);
		ToolboxPane.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_5 = new JLabel("Description");
		lblNewLabel_5.setFont(new Font("Verdana", Font.BOLD, 11));
		lblNewLabel_5.setForeground(new Color(255, 255, 255));
		lblNewLabel_5.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblNewLabel_5);
		
		Panel panel_7 = new Panel();
		panel_2.add(panel_7);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setMinimumSize(new Dimension(100, 100));
		panel_7.add(scrollPane_1);
		
		Panel panel_8 = new Panel();
		panel_8.setPreferredSize(new Dimension(10, 200));
		panel_8.setMinimumSize(new Dimension(2, 200));
		panel_8.setBackground(Color.DARK_GRAY);
		scrollPane_1.setViewportView(panel_8);
		panel_8.setLayout(new BoxLayout(panel_8, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_6 = new JLabel("Select any device to edit its properties.");
		lblNewLabel_6.setForeground(Color.WHITE);
		lblNewLabel_6.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_8.add(lblNewLabel_6);

		JPanel RenderingArea = new JPanel();
		RenderingArea.setBackground(Color.BLACK);
		HorizontalPane.setRightComponent(RenderingArea);
		RenderingArea.setLayout(new MigLayout("ins 0, wrap 0, gap rel 0", "[288.00,grow]", "[693.00,grow,fill]"));

		renderCanvas = new RenderingCanvas();
		RenderingArea.add(renderCanvas, "cell 0 0,grow");
		renderCanvas.setLayout(null);

		JPanel GraphingArea = new JPanel();
		GraphingArea.setBackground(new Color(0, 0, 0));
		GraphingArea.setBorder(null);
		VerticalPane.setRightComponent(GraphingArea);
		
		
		Panel graphing = new Panel();
		simulationInstances.addTab("Graphs", null, graphing, null);

		f.setSize(1024, 1024);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setForeground(Color.DARK_GRAY);
		f.setTitle("Sim");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.DARK_GRAY);
		f.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		mnNewMenu.setBackground(Color.DARK_GRAY);
		menuBar.add(mnNewMenu);
		//pane.setTabComponentAt(i, new ButtonTabComponent(pane));
	    simulationInstances.setBackgroundAt(1, Color.red);
		f.setVisible(true);

	}
}