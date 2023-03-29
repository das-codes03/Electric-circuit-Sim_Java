package module1;

import java.awt.Color;

import javax.swing.*;
import java.awt.Panel;
import net.miginfocom.swing.MigLayout;
import uiPackage.RenderingCanvas;
import java.awt.Font;

public class MainWindow {

	/**
	 * @wbp.parser.entryPoint
	 */
	
	public static RenderingCanvas renderCanvas;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void start(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame f = new JFrame();
		// f.setUndecorated(true);
		f.setBackground(Color.WHITE);
		f.getContentPane().setBackground(Color.DARK_GRAY);
		f.getContentPane().setLayout(
				new MigLayout("ins 0, wrap 0, gap rel 0", "[455.00px,grow,fill]", "[0.00px,grow,shrink 0,fill]"));

		Panel contentPane = new Panel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setForeground(Color.RED);
		f.getContentPane().add(contentPane, "cell 0 0,grow");
		contentPane.setLayout(new MigLayout("ins 0, wrap 0, gap rel 0", "[grow]", "[grow]"));

		JTabbedPane simulationInstances = new JTabbedPane(JTabbedPane.TOP);
		simulationInstances.setBorder(null);
		simulationInstances.setBackground(Color.DARK_GRAY);

		contentPane.add(simulationInstances, "cell 0 0,grow");

		JPanel simSpace = new JPanel();
		simSpace.setBackground(Color.DARK_GRAY);
		simSpace.setForeground(new Color(0, 0, 0));
		simulationInstances.addTab("Simulator", null, simSpace, null);
		simSpace.setLayout(new MigLayout("ins 0, wrap 0, gap rel 0", "[346.00,grow,fill]", "[grow]"));

		JSplitPane VerticalPane = new JSplitPane();
		VerticalPane.setForeground(Color.BLACK);
		// splitPane_1.setBorder(Border);
		VerticalPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		simSpace.add(VerticalPane, "cell 0 0,grow");

		JSplitPane HorizontalPane = new JSplitPane();
		VerticalPane.setLeftComponent(HorizontalPane);
		HorizontalPane.setBackground(Color.DARK_GRAY);
		HorizontalPane.setForeground(Color.DARK_GRAY);

		JPanel ToolboxPane = new JPanel();
		ToolboxPane.setBackground(Color.DARK_GRAY);
		HorizontalPane.setLeftComponent(ToolboxPane);
		ToolboxPane.setLayout(new MigLayout("ins 0, wrap 0, gap rel 0", "[100px:n,grow]", "[204.00,grow]"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.DARK_GRAY);
		ToolboxPane.add(scrollPane, "cell 0 0,grow");

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(51, 51, 51));
		scrollPane.setViewportView(panel_2);
		panel_2.setLayout(new MigLayout("", "[grow]", "[]"));

		JLabel lblNewLabel = new JLabel("Properties");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.GREEN);
		lblNewLabel.setBackground(new Color(51, 51, 51));
		panel_2.add(lblNewLabel, "cell 0 0");

		JPanel RenderingArea = new JPanel();
		RenderingArea.setBackground(Color.BLACK);
		HorizontalPane.setRightComponent(RenderingArea);
		RenderingArea.setLayout(
				new MigLayout("ins 0, wrap 0, gap rel 0", "[288.00,grow][200:n:200,grow,fill]", "[281.00,grow,fill]"));

		renderCanvas = new RenderingCanvas();
		RenderingArea.add(renderCanvas, "cell 0 0,grow");
		renderCanvas.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		RenderingArea.add(panel_3, "cell 1 0,grow");
		panel_3.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTree tree = new JTree();
		tree.setFont(new Font("Tahoma", Font.BOLD, 11));
		tree.setForeground(new Color(51, 255, 0));
		tree.setBackground(new Color(0, 51, 51));
		panel_3.add(tree, "cell 0 0,grow");

		JPanel GraphingArea = new JPanel();
		GraphingArea.setBackground(Color.DARK_GRAY);
		GraphingArea.setBorder(null);
		VerticalPane.setRightComponent(GraphingArea);

		Panel graphing = new Panel();
		simulationInstances.addTab("Graphs", null, graphing, null);

		f.setSize(512, 512);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setForeground(Color.DARK_GRAY);
		f.setTitle("Sim");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		
	}
}