package frontend;

import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import uiPackage.LogarithmicSlider;
import uiPackage.RenderingCanvas;
import utilities.NumericUtilities;

import java.awt.Font;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import frontend.TelemetryScreen.runStatus;

import java.awt.BorderLayout;

public class MainWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	public Color c3 = new Color(20, 20, 20);
	public Color c4 = Color.lightGray;
	public Color canvasBg = new Color(10, 10, 10);
	public JTree tree;
	private JButton btnFullScreen;
	private JButton btnSnapshot;
	private JButton stopButton;
	private JButton playButton;
	private LogarithmicSlider slider;
	private JLabel speedLabel;
	private JLabel runningLabel;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainWindow() {

		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		JPanel simSpace = new JPanel();
		this.getContentPane().add(simSpace);
		simSpace.setLayout(new BoxLayout(simSpace, BoxLayout.X_AXIS));

		JSplitPane VerticalPane = new JSplitPane();
		VerticalPane.setDividerSize(8);
		VerticalPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		simSpace.add(VerticalPane);

		JSplitPane HorizontalPane = new JSplitPane();
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
		
		JButton fitBtn = new JButton("Fit to screen");
		fitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderCanvas.zoomToFit();
				
			}
		});
		controlPanelRender.add(fitBtn);
		renderCanvas = new RenderingCanvas(this);
		renderCanvas.setBackground(canvasBg);
		RenderingArea.add(renderCanvas, BorderLayout.CENTER);
		renderCanvas.setLayout(null);

		JPanel GraphingArea = new JPanel();
		GraphingArea.setBackground(new Color(0, 0, 0));
		VerticalPane.setRightComponent(GraphingArea);

		JScrollPane toolboxScroll = new JScrollPane();
		ToolboxPane.add(toolboxScroll);
		JPanel toolboxViewport = new JPanel();
		toolboxViewport.setAlignmentY(Component.TOP_ALIGNMENT);
		toolboxScroll.setViewportView(toolboxViewport);
		toolboxViewport.setLayout(new BoxLayout(toolboxViewport, BoxLayout.Y_AXIS));

		JPanel simControlsPanel = new JPanel();

		simControlsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		toolboxViewport.add(simControlsPanel);
		simControlsPanel.setLayout(new BoxLayout(simControlsPanel, BoxLayout.Y_AXIS));

		JLabel lbl_Simulator_Controls = new JLabel("Simulator Controls");
		lbl_Simulator_Controls.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_Simulator_Controls.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		simControlsPanel.add(lbl_Simulator_Controls);

		JPanel simCtrlPnl = new JPanel();
		simCtrlPnl.setSize(new Dimension(80, 40));
		simCtrlPnl.setMinimumSize(new Dimension(60, 30));
		simCtrlPnl.setMaximumSize(new Dimension(80, 40));
		simCtrlPnl.setPreferredSize(new Dimension(80, 40));
		simCtrlPnl.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
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
		stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		stopButton.setBounds(0, 0, 30, 30);
		stopButton.setMinimumSize(new Dimension(30, 30));
		stopButton.setMaximumSize(new Dimension(30, 30));

		stopButton.setBackground(c2);
		stopButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		simCtrlPnl.add(stopButton);
		try {
			buttonIcon = ImageIO.read(getClass().getResource(playButtonImgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		playButton = new JButton(new ImageIcon(buttonIcon.getScaledInstance(33, 30, BufferedImage.SCALE_FAST)));
		playButton.setToolTipText("Run/pause Simulation");
		playButton.setBounds(30, 0, 30, 30);
		playButton.setMinimumSize(new Dimension(30, 30));
		playButton.setMaximumSize(new Dimension(30, 30));
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.setBackground(c2);
		playButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		simCtrlPnl.add(playButton);

		JPanel simSpeedPnl = new JPanel();
		simControlsPanel.add(simSpeedPnl);
		simSpeedPnl.setLayout(new BoxLayout(simSpeedPnl, BoxLayout.Y_AXIS));

		JLabel lbl_Simulation_Speed = new JLabel("Simulation Speed");
		lbl_Simulation_Speed.setFont(new Font("Verdana", Font.PLAIN, 11));
		lbl_Simulation_Speed.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_Simulation_Speed.setVerticalAlignment(SwingConstants.TOP);
		simSpeedPnl.add(lbl_Simulation_Speed);

		slider = new LogarithmicSlider(-9, 1, 3, "s");

		slider.setPaintTicks(true);
		simSpeedPnl.add(slider);

		speedLabel = new JLabel("Speed : 1.00x");
		speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
		speedLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		simSpeedPnl.add(speedLabel);

		runningLabel = new JLabel("RUNNING");
		runningLabel.setForeground(Color.GREEN);
		runningLabel.setAlignmentX(0.5f);
		simSpeedPnl.add(runningLabel);
		JPanel descriptionParent = new JPanel();
		descriptionParent.setMinimumSize(new Dimension(100, 100));
		toolboxViewport.add(descriptionParent);
		descriptionParent.setLayout(new GridLayout());
		descriptionParent.setBorder(new BevelBorder(BevelBorder.LOWERED));

		descriptionPanel = new JPanel();
		descriptionPanel.setMinimumSize(new Dimension(0, 100));
		descriptionParent.add(descriptionPanel);
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		toolboxViewport.add(tabbedPane);
		JPanel devicesPanel = new JPanel();
		devicesPanel.setName("devices");
		tabbedPane.addTab("Add devices", null, devicesPanel, null);

		devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
		JLabel lbl_Add_Device = new JLabel("Add Device");
		lbl_Add_Device.setAlignmentX(Component.CENTER_ALIGNMENT);
		devicesPanel.add(lbl_Add_Device);

		JScrollPane deviceScrollPane = new JScrollPane();
		devicesPanel.add(deviceScrollPane);
		HashMap<String, String[]> mapping = new HashMap<>();
		mapping.put("General", new String[] { "Resistor", "Inductor", "Capacitor" });
		mapping.put("Power Source", new String[] { "DCSource", "ACSource" });
		mapping.put("Load", new String[] { "Bulb" });
		mapping.put("Switch", new String[] { "Switch" });
		mapping.put("AC devices", new String[] { "Transformer" });
		mapping.put("Semiconductor devices", new String[] { "Diode", "Led" });
		mapping.put("Displays", new String[] { "SevenSegDsp" });
		setComponentList(mapping);

		deviceScrollPane.setViewportView(tree);
		JPanel telemetryPanel = new JPanel();
		telemetryPanel.setName("telemetry");
		tabbedPane.addTab("Telemetry", null, telemetryPanel, null);
		telemetryPanel.setLayout(new BoxLayout(telemetryPanel, BoxLayout.Y_AXIS));

		JLabel lbl_TELEMETRY = new JLabel("TELEMETRY");
		lbl_TELEMETRY.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_TELEMETRY.setHorizontalAlignment(SwingConstants.CENTER);
		telemetryPanel.add(lbl_TELEMETRY);

		teleScreen = new TelemetryScreen(telemetryPanel);
		teleScreen.updateValues(0.0, 0.0, 0.0, 0.0, 0.0, runStatus.STOPPED);

		if (tree.getCellRenderer() instanceof DefaultTreeCellRenderer) {
			final DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) (tree.getCellRenderer());
			renderer.setBackgroundNonSelectionColor(Color.black);
			renderer.setBackgroundSelectionColor(Color.DARK_GRAY);
			renderer.setTextNonSelectionColor(Color.white);
			renderer.setTextSelectionColor(Color.green);
		}

		this.setSize(1024, 1024);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("Simulator");
		this.setIconImage(buttonIcon);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem saveMenuBtn = new JMenuItem("Save");
		saveMenuBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Driver.getDriver().save();
			}
		});
		fileMenu.add(saveMenuBtn);

		JMenuItem newMenuBtn = new JMenuItem("New");
		fileMenu.add(newMenuBtn);

		JMenuItem openMenuBtn = new JMenuItem("Open");
		openMenuBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Driver.getDriver().open();

			}
		});
		fileMenu.add(openMenuBtn);

		JMenuItem exitMenuBtn = new JMenuItem("Exit");
		fileMenu.add(exitMenuBtn);

		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		JMenu mnNewMenu_3 = new JMenu("Theme");
		viewMenu.add(mnNewMenu_3);
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
		JMenu marketPlacemenu = new JMenu("Marketplace");
		menuBar.add(marketPlacemenu);
		
		JMenuItem importBtn = new JMenuItem("Import circuit");
		importBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new MarketplaceWindow();				
			}
		});
		marketPlacemenu.add(importBtn);
		JMenuItem uploadBtn = new JMenuItem("Upload circuit");
		uploadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new UploadCircuitWizard();				
			}
		});
		marketPlacemenu.add(uploadBtn);
		this.setVisible(true);
		setListeners();
		slider.setLogValue(Driver.getDriver().speed);
	}

	public void refreshDescription() {
		descriptionPanel.removeAll();
		descriptionPanel.getParent().revalidate();
		descriptionPanel.repaint();
	}

	void setComponentList(HashMap<String, String[]> mapping) {
		tree = new JTree();
		tree.setBackground(new Color(20, 20, 20));
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Devices") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				for (var m : mapping.keySet()) {
					DefaultMutableTreeNode node_1 = new DefaultMutableTreeNode(m);
					for (var c : mapping.get(m)) {
						node_1.add(new DefaultMutableTreeNode(c));
					}
					add(node_1);
				}
			}
		}));
	}

	void setListeners() {
		btnFullScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fullScreenButton();
			}
		});
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopButton();
			}
		});
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				speedChanged();
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				System.out.println("Node: " + tree.getLastSelectedPathComponent());
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				var clickedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (clickedNode != null)
					if (clickedNode.getChildCount() == 0) {
						if (e.getClickCount() == 2) {
							Driver.getDriver().addComponent(clickedNode.toString(),
									renderCanvas.screenToLocalPoint(
											NumericUtilities.addPoint(new Point(200, renderCanvas.getHeight() / 2),
													renderCanvas.getLocationOnScreen())));
						}
					}
				renderCanvas.Render();
			}
		});

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runButton();
			}
		});
	}

	protected void speedChanged() {
		Driver.getDriver().speed = slider.getLogValue();
		speedLabel.setText("1 sec = " + NumericUtilities.getPrefixed(Driver.getDriver().speed, 3) + "s");
	}

	protected void stopButton() {
		System.out.println("Stop button clicked");
		Driver.getDriver().stopSimulation();
	}

	protected void snapShotButton() {
		System.out.println("Snapshot button clicked");
	}

	public void runButton() {
		System.out.println("Run button clicked");
		Driver.getDriver().StartSimulation();
	}

	public void fullScreenButton() {
		System.out.println("Run button clicked");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
	}

}