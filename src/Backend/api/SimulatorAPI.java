package Backend.api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.directory.AttributeInUseException;

import org.apache.commons.math3.linear.RealMatrix;

import Backend.simulator.Circuit;
import Backend.simulator.Component;

public class SimulatorAPI implements Runnable {

	public SimulatorAPI(double dt) {
		this.dt = dt;
		sim = new Simulation(dt, 1);
	}

	private Simulation sim;

	enum mode {
		RUNNING, PAUSED, TERMINATED
	}

	private mode CurrMode = mode.TERMINATED;
	private double dt;

	public void play() {
		CurrMode = mode.RUNNING;
	}

	public void pause() {
		CurrMode = mode.PAUSED;
	}

	public void stop() {
		CurrMode = mode.TERMINATED;
	}

	public void setTimeStep(double dt) {
		this.dt = dt;
	}
	public void addComponent(int ID, String typeName) {
		try {
			sim.addComponent(typeName, ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void generateConnections(HashMap<Integer, String> componentData) throws Exception {
		for (var s : componentData.keySet()) {
			sim.addComponent(componentData.get(s), s);
		}
	}
	/** Connect set of nodes. Format is [{iden1, n1}...] */
	public void connect(ArrayList<int[]> data) {
		try {
			var temp = new ArrayList<Circuit.Node>();
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).length != 2)
					throw new IllegalArgumentException("Data array must contain as format {iden1, n1}");
				var node = sim.identifiers.get(data.get(i)[0]).getExternalNode(data.get(i)[1]);
				temp.add(node);
			}
			sim.c.MergeNodes(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run() {

		// make connections first
		play();
		while (CurrMode != mode.TERMINATED) {
			while (CurrMode == mode.PAUSED)
				; // wait while paused
			sim.simulateStep(dt, 1);
		}
	}
	/**Connect two nodes. Format is iden1, n1, iden2, n2.*/
	public void connect(int iden1, int n1, int iden2, int n2) {
		try {
			var temp = new ArrayList<Circuit.Node>();
			var node1 = sim.identifiers.get(iden1).getExternalNode(n1);
			var node2 = sim.identifiers.get(iden2).getExternalNode(n2);
			temp.add(node1);
			temp.add(node2);
			sim.c.MergeNodes(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Simulation {
		public class simState {
			public double timestamp;

		}

		private final Map<Integer, Component> identifiers;
		private boolean initialized = false;
		private double timeElapsed = 0;

		public void simulateStep(double dt, int substeps) {
			int t = substeps;
			double rDt = dt / substeps;

			RealMatrix r = null;
			while (t-- > 0) {
				if (!initialized) {
					c.initialiseCircuit();
					initialized = true;
				}

				c.GenerateEmfMatrix();

				c.GenerateResistanceMatrix(rDt);

				c.GenerateInductanceMatrix();

				r = c.solveCurrent(rDt);
				try {
					c.updateSegments(rDt);
				} catch (Exception e) {
					e.printStackTrace();
				}
				timeElapsed += rDt;
				for (var comp : identifiers.values()) {
					comp.updateState(timeElapsed, rDt);
				}
			}
			System.out.println("Current: " + r);
		}

		public Simulation(double dt, double timeScale) {
			super();
			this.c = new Circuit();
			identifiers = new HashMap<Integer, Component>();
			this.dt = dt;
			this.timeScale = timeScale;
		}

		Circuit c;
		double dt;
		double timeScale;

		
		
		public Component addComponent(String componentName, int identifier) throws Exception {
			if (identifiers.containsKey(identifier)) {
				throw new AttributeInUseException("Key " + identifier + " already exists");
			}
			try {
				Class<Component> x = (Class<Component>) Class.forName("Backend.simulator.components." + componentName);
				try {
					var comp = c.addComponent(x.getDeclaredConstructor(new Class[] { Circuit.class }).newInstance(c));
					identifiers.put(identifier, comp);
					return comp;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Simulation newSim() {
		Simulation sim = new Simulation(0.01d, 1d);
		return sim;
	}

}
