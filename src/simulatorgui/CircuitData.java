package simulatorgui;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.NodeUI;
import simulatorgui.rendering.WireUI;

public class CircuitData {

	static class ComponentInformation implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String typeName;
		public Point position;
		public double rotation;
		public HashMap<String, Object> properties;
	}

	public static class PackedData implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7400561811706111730L;
		public PackedData(ArrayList<WireUI> wireUIs, ArrayList<DeviceUI> data) {
			this.components = new HashMap<>();
			this.connections = new ArrayList<>();
			this.nodes = new ArrayList<>();
			for (var c : data) {
				ComponentInformation info = new ComponentInformation();
				info.typeName = c.getDescriptorName();
				System.out.println(info.typeName);
				info.position = c.getLocation();
				info.rotation = c.getRotation();
				info.properties = c.readProperties();
				components.put(c.getID(), info);
			}
			Map<NodeUI, Integer> nodeMap = new HashMap<>();
			for (int i = 0; i < wireUIs.size(); ++i) {
				WireUI w = wireUIs.get(i);
				connections.add(new ArrayList<>());
				var curr = connections.get(connections.size() - 1);
				for (var n : w.nodes) {
					if (n.parentDevice == null) {
						if (nodeMap.keySet().contains(n)) {
							int index = nodeMap.get(n);
							curr.add(new Object[] {  index });// wires.get(index).nodes.indexOf(n)});
						} else {
							var pt = new Point(n.getLocation());
							curr.add(pt);
							nodes.add(pt);
							nodeMap.put(n, nodes.size()-1);
						}
					} else {
						curr.add(new Object[] { n.parentDevice.getID(), n.getNodeIndex() });
					}
				}
			}

		}

		public HashMap<Integer, ComponentInformation> components; // ID->Type
		public ArrayList<Point> nodes;
		public ArrayList<ArrayList<Object>> connections; // Component ID, node ID if part of device, else just Location
															// of
															// node
	}
}
