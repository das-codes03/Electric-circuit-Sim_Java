package module1;

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

import componentdescriptors.ResistorDescriptor;
import uiPackage.NodeUI;
import uiPackage.RenderingCanvas;
import uiPackage.DeviceUI;
import uiPackage.ComponentDescriptor;
import uiPackage.Wire;

public class SimUiManager {

	private static ArrayList<ComponentDescriptor> components;
	private static MainWindow mainWin;

	public static void addComponent(String typeName, Point screenPos) {
		try {
			Class<ComponentDescriptor> act = (Class<ComponentDescriptor>) Class
					.forName("componentdescriptors." + typeName + "Descriptor");
			try {
				act.getConstructor(RenderingCanvas.class, Point.class).newInstance(mainWin.renderCanvas,
						screenPos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(mainWin,"Component " + typeName + " doesn't exist in inventory. Make sure your application is updated or send feedback!", "Component not found", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		mainWin.renderCanvas.Render();
	}

	public static void addComponent(String typeName) {
		addComponent(typeName, new Point(0, 0));
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatMacDarkLaf());
			UIManager.put("RootPane.background", new Color(20, 20, 20));
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Couldn't set look and feel");
		}
		mainWin = new MainWindow();
		HashMap<Integer, String> nameMap = new HashMap<>();
		nameMap.put(0, "Capacitor");
		nameMap.put(1, "Inductor");
		nameMap.put(2, "Resistor");
		nameMap.put(3, "DCSource");
		nameMap.put(4, "ACSource");
		nameMap.put(5, "Transformer");
		nameMap.put(6, "Diode");
		nameMap.put(7, "Switch");
		
		for(int i = 0; i < 3000; ++i) {
			addComponent(nameMap.get(i%8), new Point((int)(Math.random() * 10000),(int)(Math.random() * 10000)));
		}
	}

}
