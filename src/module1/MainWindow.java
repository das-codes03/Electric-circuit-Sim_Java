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
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.GroupLayout.Alignment;
import com.jgoodies.forms.layout.FormLayout;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import module1.TelemetryScreen.runStatus;
import javax.swing.UIManager.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import com.jgoodies.forms.layout.FormSpecs;

public class MainWindow {

	/**
	 * @wbp.parser.entryPoint
	 */

	public RenderingCanvas renderCanvas;
	public TelemetryScreen teleScreen;
	public JPanel descriptionPanel;
	public String playButtonImgPath = "/resources/playpause.png";
	public String stopButtonImgPath = "/resources/stop.png";
	public Color c1 = Color.black;
	public Color c2 = new Color(30, 30, 30);
	public Color c3 = new Color(20,20,20);
	public Color c4 = Color.lightGray;
	public Color canvasBg = new Color(10,10,10);
	private JButton btnFullScreen;
	private JButton btnSnapshot;
	private JButton stopButton;
	private JButton playButton;
	private JSlider slider;
	private JLabel speedLabel;
	private JLabel runningLabel;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainWindow() {
		try {
	        UIManager.setLookAndFeel(new FlatMacDarkLaf());
	        UIManager.put("RootPane.background", new Color(20,20,20));
	    } catch (UnsupportedLookAndFeelException ex) {
	    }

		JFrame mainFrame = new JFrame();
		//mainFrame.getContentPane().setBackground(c2);
		mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));

		JPanel simSpace = new JPanel();
//		simSpace.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		//simSpace.setBackground(Color.DARK_GRAY);
		mainFrame.getContentPane().add(simSpace);
		simSpace.setLayout(new BoxLayout(simSpace, BoxLayout.X_AXIS));

		JSplitPane VerticalPane = new JSplitPane();
//		VerticalPane.setBorder(null);
		VerticalPane.setDividerSize(8);
		VerticalPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		simSpace.add(VerticalPane);

		JSplitPane HorizontalPane = new JSplitPane();
//		HorizontalPane.setBorder(null);
		HorizontalPane.setDividerSize(8);
		VerticalPane.setLeftComponent(HorizontalPane);

		JPanel ToolboxPane = new JPanel();
		ToolboxPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		HorizontalPane.setLeftComponent(ToolboxPane);
		ToolboxPane.setLayout(new BoxLayout(ToolboxPane, BoxLayout.Y_AXIS));

		JPanel RenderingArea = new JPanel();
		HorizontalPane.setRightComponent(RenderingArea);
		RenderingArea.setLayout(new BorderLayout(0, 0));
		JPanel controlPanelRender = new JPanel();
		controlPanelRender.setBackground(c2);
		RenderingArea.add(controlPanelRender, BorderLayout.NORTH);
		controlPanelRender.setLayout(new BoxLayout(controlPanelRender, BoxLayout.X_AXIS));

		btnFullScreen = new JButton("Full screen");
		btnFullScreen.setBackground(c3);
		controlPanelRender.add(btnFullScreen);

		btnSnapshot = new JButton("Snapshot");
		btnSnapshot.setBackground(c3);
		controlPanelRender.add(btnSnapshot);
		renderCanvas = new RenderingCanvas(this);
		renderCanvas.setBackground(canvasBg);
		RenderingArea.add(renderCanvas, BorderLayout.CENTER);
		renderCanvas.setLayout(null);

		JPanel GraphingArea = new JPanel();
		GraphingArea.setBackground(new Color(0, 0, 0));
//		GraphingArea.setBorder(null);
		VerticalPane.setRightComponent(GraphingArea);

		JScrollPane toolboxScroll = new JScrollPane();
	//	toolboxScroll.setBackground(new Color(0, 0, 0));
		ToolboxPane.add(toolboxScroll);
		JPanel toolboxViewport = new JPanel();
		//toolboxViewport.setBackground(c2);
		toolboxViewport.setAlignmentY(Component.TOP_ALIGNMENT);
		toolboxScroll.setViewportView(toolboxViewport);
		toolboxViewport.setLayout(new BoxLayout(toolboxViewport, BoxLayout.Y_AXIS));

		JPanel simControlsPanel = new JPanel();

		simControlsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//		simControlsPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
//		simControlsPanel.setBackground(c2);
		toolboxViewport.add(simControlsPanel);
		simControlsPanel.setLayout(new BoxLayout(simControlsPanel, BoxLayout.Y_AXIS));

		JLabel lbl_Simulator_Controls = new JLabel("Simulator Controls");
		lbl_Simulator_Controls.setAlignmentX(Component.CENTER_ALIGNMENT);
//		lbl_Simulator_Controls.setFont(new Font("Verdana", Font.BOLD, 11));
//		lbl_Simulator_Controls.setBackground(new Color(255, 128, 64));
		lbl_Simulator_Controls.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//		lbl_Simulator_Controls.setForeground(new Color(255, 128, 64));
		simControlsPanel.add(lbl_Simulator_Controls);

		JPanel simCtrlPnl = new JPanel();
		simCtrlPnl.setSize(new Dimension(80, 40));
		simCtrlPnl.setMinimumSize(new Dimension(60, 30));
		simCtrlPnl.setMaximumSize(new Dimension(80, 40));
		simCtrlPnl.setPreferredSize(new Dimension(80, 40));
		simCtrlPnl.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//		simCtrlPnl.setBackground(c2);
		simControlsPanel.add(simCtrlPnl);
		simCtrlPnl.setLayout(new GridLayout(0, 2, 0, 0));

		BufferedImage buttonIcon = null;
		try {
			buttonIcon = ImageIO.read(getClass().getResource(stopButtonImgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		stopButton = new JButton(new ImageIcon(buttonIcon.getScaledInstance(33, 30, BufferedImage.SCALE_FAST)));
		stopButton.setToolTipText("Stop Simulation");
//		stopButton.setBorder(null);
		stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//		stopButton.setContentAreaFilled(false);
		stopButton.setBounds(0, 0, 30, 30);
//		stopButton.setBorderPainted(false);
		stopButton.setMinimumSize(new Dimension(30, 30));
		stopButton.setMaximumSize(new Dimension(30, 30));

		stopButton.setBackground(c2);
		stopButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		stopButton.setForeground(new Color(128, 0, 0));
		simCtrlPnl.add(stopButton);
		try {
			buttonIcon = ImageIO.read(getClass().getResource(playButtonImgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		playButton = new JButton(new ImageIcon(buttonIcon.getScaledInstance(33, 30, BufferedImage.SCALE_FAST)));
		playButton.setToolTipText("Run/pause Simulation");
//		playButton.setBorder(null);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
//		playButton.setContentAreaFilled(false);
		playButton.setBounds(30, 0, 30, 30);
//		playButton.setBorderPainted(false);
		playButton.setMinimumSize(new Dimension(30, 30));
		playButton.setMaximumSize(new Dimension(30, 30));
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.setBackground(c2);
		playButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		playButton.setForeground(new Color(128, 0, 0));
		simCtrlPnl.add(playButton);

		JPanel simSpeedPnl = new JPanel();
//		simSpeedPnl.setBackground(c2);
		simControlsPanel.add(simSpeedPnl);
		simSpeedPnl.setLayout(new BoxLayout(simSpeedPnl, BoxLayout.Y_AXIS));

		JLabel lbl_Simulation_Speed = new JLabel("Simulation Speed");
		lbl_Simulation_Speed.setFont(new Font("Verdana", Font.PLAIN, 11));
//		lbl_Simulation_Speed.setForeground(new Color(255, 255, 255));
		lbl_Simulation_Speed.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_Simulation_Speed.setVerticalAlignment(SwingConstants.TOP);
		simSpeedPnl.add(lbl_Simulation_Speed);

		slider = new JSlider();
		slider.setMajorTickSpacing(200);
		slider.setMinorTickSpacing(50);
		slider.setPaintTicks(true);
		slider.setMaximum(1000);
		slider.setValue(500);

//		slider.setBackground(c2);
		simSpeedPnl.add(slider);

		speedLabel = new JLabel("Speed : 1.00x");
//		speedLabel.setForeground(new Color(0, 255, 0));
		speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
		speedLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		simSpeedPnl.add(speedLabel);

		runningLabel = new JLabel("RUNNING");
		runningLabel.setForeground(Color.GREEN);
//		runningLabel.setFont(new Font("Verdana", Font.BOLD, 15));
		runningLabel.setAlignmentX(0.5f);
		simSpeedPnl.add(runningLabel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		toolboxViewport.add(tabbedPane);
//		tabbedPane.setBackground(c2);

		JPanel devicesPanel = new JPanel();
		devicesPanel.setName("devices");
//		devicesPanel.setBackground(Color.DARK_GRAY);

		tabbedPane.addTab("Add devices", null, devicesPanel, null);

		devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
		JLabel lbl_Add_Device = new JLabel("Add Device");
		lbl_Add_Device.setAlignmentX(Component.CENTER_ALIGNMENT);
//		lbl_Add_Device.setFont(new Font("Verdana", Font.BOLD, 11));
//		lbl_Add_Device.setForeground(new Color(255, 255, 255));
		devicesPanel.add(lbl_Add_Device);

		JScrollPane deviceScrollPane = new JScrollPane();
		devicesPanel.add(deviceScrollPane);

		JTree tree = new JTree();
		tree.setBackground(new Color(20, 20, 20));
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("JTree") {
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
		}));
		deviceScrollPane.setViewportView(tree);

//								cardPanel.add(devicesPanel, devicesPanel.getName());

		JPanel telemetryPanel = new JPanel();
		telemetryPanel.setName("telemetry");
//		telemetryPanel.setBackground(c2);
		tabbedPane.addTab("Telemetry", null, telemetryPanel, null);

//										cardPanel.add(telemetryPanel, telemetryPanel.getName());
		telemetryPanel.setLayout(new BoxLayout(telemetryPanel, BoxLayout.Y_AXIS));

		JLabel lbl_TELEMETRY = new JLabel("TELEMETRY");
//		lbl_TELEMETRY.setForeground(Color.ORANGE);
//		lbl_TELEMETRY.setFont(new Font("Dialog", Font.BOLD, 14));
		lbl_TELEMETRY.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_TELEMETRY.setHorizontalAlignment(SwingConstants.CENTER);
		telemetryPanel.add(lbl_TELEMETRY);

		teleScreen = new TelemetryScreen(telemetryPanel);
		teleScreen.updateValues(0, 0, 0, 0, 0, runStatus.STOPPED);
//		((CardLayout) cardPanel.getLayout()).show(cardPanel, "telemetry");

		JPanel descriptionParent = new JPanel();
		descriptionParent.setMinimumSize(new Dimension(100, 100));
//		descriptionParent.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
//		descriptionParent.setBackground(c2);
		toolboxViewport.add(descriptionParent);
		descriptionParent.setLayout(new BoxLayout(descriptionParent, BoxLayout.Y_AXIS));

		JLabel lbl_Description = new JLabel("Description");
//		lbl_Description.setFont(new Font("Verdana", Font.BOLD, 11));
//		lbl_Description.setBackground(new Color(255, 255, 255));
		lbl_Description.setAlignmentX(Component.CENTER_ALIGNMENT);
		descriptionParent.add(lbl_Description);
		descriptionPanel = new JPanel();
		descriptionPanel.setMinimumSize(new Dimension(0, 100));
//		descriptionPanel.setBackground(Color.DARK_GRAY);
		descriptionParent.add(descriptionPanel);
		descriptionPanel.setLayout(new GridLayout(0, 2, 0, 0));
		if (tree.getCellRenderer() instanceof DefaultTreeCellRenderer) {
			final DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) (tree.getCellRenderer());
			renderer.setBackgroundNonSelectionColor(Color.black);
			renderer.setBackgroundSelectionColor(Color.DARK_GRAY);
			renderer.setTextNonSelectionColor(Color.white);
			renderer.setTextSelectionColor(Color.green);
		}

		mainFrame.setSize(1024, 1024);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		mainFrame.setForeground(Color.DARK_GRAY);
		mainFrame.setTitle("Simulator");
		mainFrame.setIconImage(buttonIcon);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
//		menuBar.setBackground(c2);
		mainFrame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
//		mnNewMenu.setBackground(Color.DARK_GRAY);
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Save");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("New");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Open");
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Exit");
		mnNewMenu.add(mntmNewMenuItem_3);
		
		JMenu mnNewMenu_2 = new JMenu("View");
		menuBar.add(mnNewMenu_2);
		
		JMenu mnNewMenu_3 = new JMenu("Theme");
		mnNewMenu_2.add(mnNewMenu_3);
		ButtonGroup themeGrp = new ButtonGroup();
		JRadioButtonMenuItem rdbtnmntmNewRadioItem_1 = new JRadioButtonMenuItem("Light");
		mnNewMenu_3.add(rdbtnmntmNewRadioItem_1);
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem = new JRadioButtonMenuItem("Dark");
		rdbtnmntmNewRadioItem.setSelected(true);
		mnNewMenu_3.add(rdbtnmntmNewRadioItem);
		themeGrp.add(rdbtnmntmNewRadioItem);
		themeGrp.add(rdbtnmntmNewRadioItem_1);
		
		JMenu mnNewMenu_1 = new JMenu("Simulate");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Run");
		mnNewMenu_1.add(mntmNewMenuItem_4);
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Pause");
		mnNewMenu_1.add(mntmNewMenuItem_5);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Stop");
		mnNewMenu_1.add(mntmNewMenuItem_6);

		mainFrame.setVisible(true);

	}
}