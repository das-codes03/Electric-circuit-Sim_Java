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

public class SimUiManager {

	public static ArrayList<DeviceUI> components = new ArrayList<>();
	public static ArrayList<Wire> wires = new ArrayList<>();
	private static MainWindow mainWin;
	public static SimulationEvent s = null;

	public static void addComponent(String typeName, Point screenPos) {
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
		mainWin.renderCanvas.Render();
	}

	public static void addComponent(String typeName) {
		addComponent(typeName, new Point(0, 0));
	}

	public static void StartSimulation() {
		// Assign IDs to components
		for (int i = 0; i < components.size(); ++i) {
			components.get(i).setID(i);
		}
		
		s = new SimulationEvent(components, wires, mainWin.renderCanvas);
		Thread t = new Thread(s);
		t.start();
	}

	public static void stopSimulation() {
		if (s != null) {
			s.running = false;
			s = null;
		}
	}

	

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatMacDarkLaf());
			UIManager.put("RootPane.background", new Color(20, 20, 20));
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Couldn't set look and feel");
		}
		mainWin = new MainWindow();
	}

}
