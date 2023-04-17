package simulatorgui;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import simulatorgui.CircuitData.PackedData;
import simulatorgui.frames.MainWindow;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.NodeUI;
import simulatorgui.rendering.RenderingCanvas;
import simulatorgui.rendering.WireUI;

public class Driver {

	private final ArrayList<DeviceUI> components = new ArrayList<>();
	private final ArrayList<WireUI> wireUIs = new ArrayList<>();
	public final MainWindow mainWin;
	public static final String descriptorSuffix = "Descriptor";
	public static final String descriptorPath = "componentdescriptors.";

	public SimulationEvent s = null;
	public double speed = 1;
	private boolean running;

	public boolean isRunning() {
		return running;
	}

	public DeviceUI addComponent(String typeName, Point screenPos) {
		if(isRunning()) {
			throw new RuntimeException("Can't add component while simulation is running");
		}
		try {
			@SuppressWarnings("unchecked")
			Class<DeviceUI> act = (Class<DeviceUI>) Class.forName(descriptorPath + typeName + descriptorSuffix);
			try {
				var comp = act.getConstructor(RenderingCanvas.class, Point.class).newInstance(mainWin.renderCanvas,
						screenPos);
				components.add(comp);
				Render();
				return comp;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(mainWin,
					"Component " + typeName
							+ " doesn't exist in inventory. Make sure your application is updated or send feedback!",
					"Component not found", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	public DeviceUI addComponent(String typeName) {
		return addComponent(typeName, new Point(0, 0));
	}

	public void deleteComponent(DeviceUI comp) {
		if(isRunning()) {
			throw new RuntimeException("Can't remove component while simulation is running");
		}
		int i = components.indexOf(comp);
		if (i == -1)
			throw new RuntimeException("Component " + comp.getBackendClassName() + " not found.");
		comp.remove();
		components.remove(comp);
	}

	public void addWire(WireUI w) {
		if(isRunning()) {
			throw new RuntimeException("Can't add wire while simulation is running");
		}
		wireUIs.add(w);
	}

	public void deleteWire(WireUI w) {
		if(isRunning()) {
			throw new RuntimeException("Can't remove wire while simulation is running");
		}
		int i = wireUIs.indexOf(w);
		if (i == -1)
			throw new RuntimeException("Wire not found.");
		w.remove();
		wireUIs.remove(w);
	}

	public void Render() {
		mainWin.renderCanvas.Render();
	}

	public void StartSimulation() {
		// Assign IDs to components
		if (isRunning()) {
			System.out.println("Simulation already running");
			return;
		}

		for (int i = 0; i < components.size(); ++i) {
			components.get(i).setID(i);
		}

		s = new SimulationEvent(components, wireUIs);
		Thread t = new Thread(s);
		t.start();
		running = true;
		System.out.println("Total threads: " + Thread.activeCount());
	}

	public void stopSimulation() {
		if (s != null) {
			s.running = false;
			s = null;
			running = false;
		}
	}

	private static Driver driver;

	public static Driver getDriver() {
		return driver;
	}

	public Driver() {
		try {
			UIManager.setLookAndFeel(new FlatMacDarkLaf());
			UIManager.put("RootPane.background", new Color(20, 20, 20));
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Couldn't set look and feel");
		}
		driver = this;
		mainWin = new MainWindow();
	}

	public void createFromData(PackedData data) {
		clearCircuit();
		boolean incompleteProperties = false;
		try {
		// put all components with their properties and location, position
		HashMap<Integer, DeviceUI> tempMap = new HashMap<>();
		for (var c : data.components.keySet()) {
			var compInfo = data.components.get(c);
			var comp = addComponent(compInfo.typeName, compInfo.position);
			comp.setID(c);
			comp.setRotation(compInfo.rotation);
			try {
			comp.writeProperties(compInfo.properties);
			}catch(NullPointerException e) {
				incompleteProperties = true;
			}
			// TODO: exception if already has same ID
			tempMap.put(c, comp);
		}

		// time to connect them using wires!
		ArrayList<NodeUI> tempArray = new ArrayList<>();
		for (var c : data.connections) {
			WireUI w = new WireUI(getDriver().mainWin.renderCanvas);
			for (var n : c) {
				if (n instanceof Object[]) { // this means this is a part of some device
					var temp = (Object[]) n;
					if (temp.length == 1) {
						try {
						w.addNode(tempArray.get((int)temp[0]));
						}catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						var compId = (int) ((Object[]) n)[0];
						var nodeIndex = (int) ((Object[]) n)[1];
						try {
						w.addNode(tempMap.get(compId).getNode(nodeIndex));
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				} else if (n instanceof Point) { // lone node so wire adds new node and takes it
					var pt = new NodeUI((Point) n, getDriver().mainWin.renderCanvas);
					tempArray.add(pt);
					w.addNode(pt);
				}
			}
			addWire(w);
		}
		}catch(Exception e) {
			JOptionPane.showMessageDialog(mainWin, "Error loading data");
		}
		// all wires are connected!
		if(incompleteProperties) {
			JOptionPane.showMessageDialog(null, "Some components were not properly imported. Please recheck circuit.");
		}
	}

	private void openFromFile(File f) throws Exception {
		try {
			FileInputStream fileIn = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			var data = (PackedData) in.readObject();
			in.close();
			fileIn.close();
			createFromData(data);
		} catch (IOException i) {
			i.printStackTrace();
			JOptionPane.showMessageDialog(mainWin, "Open failure");
			return;
		} catch (ClassNotFoundException c) {
			JOptionPane.showMessageDialog(mainWin, "Open failure");
			c.printStackTrace();
			return;
		}
	}

	private void saveToFile(File f) throws Exception {
		if(isRunning()) {
			throw new Exception("Can't save circuit while simulation is running.");
		}
		for (int i = 0; i < components.size(); ++i) {
			components.get(i).setID(i);
		}
		var toWrite = new PackedData(wireUIs, components);
		try {
			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(toWrite);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in " + f);
		} catch (IOException i) {
			i.printStackTrace();
			JOptionPane.showMessageDialog(mainWin, "Couldn't save circuit! Please try again.", "Saving failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void open() throws Exception {
		if(isRunning()) {
			throw new Exception("Can't open new circuit while simulation is running.");
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "Circuit (*.sim)";
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(".sim");
				}
			}
		});

		if (fileChooser.showOpenDialog(mainWin) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				openFromFile(file);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}

	}
	public void clearCircuit() {
		if(isRunning()) {
			throw new RuntimeException("Can't clear circuit while simulation is running");
		}
		mainWin.refreshDescription();
		while(!wireUIs.isEmpty()) {
			deleteWire(wireUIs.get(0));
		}
		while(!components.isEmpty()) {
			deleteComponent(components.get(0));
		}
	}
	public void save() throws Exception {
		if(isRunning()) {
			throw new Exception("Can't save circuit while simulation is running.");
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "Circuit (*.sim)";
			}
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(".sim");
				}
			}
		});
		if (fileChooser.showSaveDialog(mainWin) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if(!file.getName().endsWith(".sim")) {
				file = new File(file.getPath()+".sim");
			}
			try {
				saveToFile(file);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	public PackedData getPackedData() throws Exception {
		if(isRunning()) {
			throw new Exception("Can't pack data while simulation is running.");
		}
		return new PackedData(wireUIs, components);
	}
	
	public static void main(String[] args) {
		new Driver();
	}

}
