package frontend;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import frontend.CircuitData.PackedData;
import uiPackage.NodeUI;
import uiPackage.RenderingCanvas;
import uiPackage.DeviceUI;

import uiPackage.Wire;

public class Driver {

	private final ArrayList<DeviceUI> components = new ArrayList<>();
	private final ArrayList<Wire> wires = new ArrayList<>();
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
		try {
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

	public void deleteComponent(DeviceUI comp) throws Exception {
		int i = components.indexOf(comp);
		if (i == -1)
			throw new Exception("Component " + comp.getBackendClassName() + " not found.");
		comp.remove();
		components.remove(comp);
	}

	public void addWire(Wire w) {
		wires.add(w);
	}

	public void deleteWire(Wire w) throws Exception {
		int i = wires.indexOf(w);
		if (i == -1)
			throw new Exception("Wire not found.");
		w.remove();
		wires.remove(w);
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

		s = new SimulationEvent(components, wires);
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
			Wire w = new Wire(getDriver().mainWin.renderCanvas);
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

	private void openFromFile(File f) {
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

	private void saveToFile(File f) {

		for (int i = 0; i < components.size(); ++i) {
			components.get(i).setID(i);
		}
		var toWrite = new PackedData(wires, components);
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

	public void open() {
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
			openFromFile(file);
			// load from file
		}

	}

	public void save() {
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
			saveToFile(file);
		}
	}
	public PackedData getPackedData() {
		return new PackedData(wires, components);
	}
	
	public static void main(String[] args) {

		new Driver();
		
			UserManager.login("dummy", "1234");
//			UploadCircuitWizard.uploadCircuit(UserManager.getUserId(), "test circuit", "this is a test circuit", new PackedData(driver.wires, driver.components), null, true);
getDriver().createFromData(MarketplaceWindow.getCircuit(3));
//		String test = "";
//		for (var s : args) {
//			test += s + "\n";
//		}
//		JOptionPane.showMessageDialog(null, test);

	}

}
