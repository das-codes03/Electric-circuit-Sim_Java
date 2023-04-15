package frontend;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.management.AttributeNotFoundException;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import Backend.api.SimulatorAPI;
import componentdescriptors.ResistorDescriptor;
import uiPackage.NodeUI;
import uiPackage.RenderingCanvas;
import uiPackage.DeviceUI;

import uiPackage.Wire;

public class Driver {

	private final ArrayList<DeviceUI> components = new ArrayList<>();
	private final ArrayList<Wire> wires = new ArrayList<>();
	public final MainWindow mainWin;


	public SimulationEvent s = null;
	public double speed = 1;
	private boolean running;

	public boolean isRunning() {
		return running;
	}

	public void addComponent(String typeName, Point screenPos) {
		try {
			Class<DeviceUI> act = (Class<DeviceUI>) Class.forName("componentdescriptors." + typeName + "Descriptor");
			try {
				components.add(act.getConstructor(RenderingCanvas.class, Point.class).newInstance(mainWin.renderCanvas,
						screenPos));
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
		Render();
	}

	public void addComponent(String typeName) {
		addComponent(typeName, new Point(0, 0));
	}

	public void deleteComponent(DeviceUI comp) throws Exception {
		int i = components.indexOf(comp);
		if (i == -1)
			throw new Exception("Component " + comp.getTypeName() + " not found.");
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

	public static void main(String[] args) {
		new Driver();
	}

}
