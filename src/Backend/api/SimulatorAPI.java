package Backend.api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.naming.directory.AttributeInUseException;
import javax.swing.JOptionPane;

import frontend.Driver;

public class SimulatorAPI implements Runnable {
	public class ShortCircuitException extends Exception{
	}
	private final HashMap<Integer, Component> identifiers;

	public class SimulationState {
		SimulationState() {
			stateData = new HashMap<>();
			timeStamp = 0;
		}
		public double getT() {
			return timeStamp;
		}
		private double timeStamp;
		public HashMap<Integer, HashMap<String, Object>> stateData;
	}

	public final SimulationState state;

	public SimulatorAPI() {
		this.timeScale = 1;
		this.c = new Circuit();
		this.state = new SimulationState();
		identifiers = new HashMap<Integer, Component>();
		this.timeScale = 1;
	}

	private HashMap<Integer, HashMap<String, Object>> getStateData() {
		HashMap<Integer, HashMap<String, Object>> data = new HashMap<>();
		for (var k : identifiers.keySet()) {
			data.put(k, identifiers.get(k).getAllStates());
		}
		return data;
	}

	private long simulateStep(double dt, int substeps) throws ShortCircuitException {
		long t1 = System.nanoTime();
		int t = substeps;
		double rDt = dt / substeps;
		while (t-- > 0) {
			if (!initialized) {
				c.initialiseCircuit();
				initialized = true;
				for (var data : identifiers.values()) {
					data.updateState(0, 0);
				}
			}
			c.generateEmfMatrix();
			c.generateResistanceMatrix(rDt);
			c.generateInductanceMatrix();
			var scTest = c.shortCircuitTest();
//			if(scTest.size() > 0) {
//				throw new ShortCircuitException();
//			}
			c.solveCurrent(rDt);
			c.updateSegments(rDt);
			timeElapsed += rDt;
			for (var data : identifiers.values()) {
				data.updateState(timeElapsed, rDt);
			}
		}
		state.stateData = getStateData();
		state.timeStamp = timeElapsed;
		long t2 = System.nanoTime();
		return t2 - t1;
	}
	private Circuit c;
	private double timeScale;
	public void addComponent(int identifier, String componentName) throws Exception {
		if (identifiers.containsKey(identifier)) {
			throw new AttributeInUseException("Key " + identifier + " already exists");
		}
		try {
			Class<Component> x = (Class<Component>) Class.forName("Backend.api.Components." + componentName);
			try {
				var comp = c.addComponent(x.getDeclaredConstructor(new Class[] { Circuit.class }).newInstance(c));
				identifiers.put(identifier, comp);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	enum mode {
		RUNNING, PAUSED, TERMINATED
	}
	private mode CurrMode = mode.TERMINATED;
	public void play() {
		CurrMode = mode.RUNNING;
	}
	public void pause() {
		CurrMode = mode.PAUSED;
	}
	public void stop() {
		CurrMode = mode.TERMINATED;
	}
	public void setTimeScale(double t) {
		this.timeScale = t;
	}
	/** Connect set of nodes. Format is [{iden1, n1}...] */
	public void connect(ArrayList<int[]> data) {
		try {
			var temp = new ArrayList<Circuit.Node>();
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).length != 2)
					throw new IllegalArgumentException("Data array must contain as format {iden1, n1}");
				var node = identifiers.get(data.get(i)[0]).getPin(data.get(i)[1]);
				temp.add(node);
			}
			c.mergeNodes(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(){
		play();
		int simulationRate = 100;
		int stepMS = (int) (1000.0 / simulationRate);
		long elapsed = 0;
		while (CurrMode != mode.TERMINATED) {
			while (CurrMode == mode.PAUSED)
				; // wait while paused
			timeScale = Driver.getDriver().speed;
			try {
				elapsed = simulateStep(timeScale/simulationRate, 10);
			} catch (ShortCircuitException e1) {
				System.err.println("SHORT");
				JOptionPane.showMessageDialog(null, "The circuit may be shorted! Please check circuit.", "Short circuit detected", JOptionPane.ERROR_MESSAGE);
				CurrMode = mode.TERMINATED;
				return;
			}
			if (elapsed < stepMS * 1000000) {
				try {
					Thread.currentThread();
					Thread.sleep(stepMS-(elapsed)/1000000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** Connect two nodes. Format is iden1, n1, iden2, n2. */
	public void connect(int iden1, int n1, int iden2, int n2) {
		try {
			var temp = new ArrayList<Circuit.Node>();
			var node1 = identifiers.get(iden1).getPin(n1);
			var node2 = identifiers.get(iden2).getPin(n2);
			temp.add(node1);
			temp.add(node2);
			c.mergeNodes(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setProperty(int componentID, String property, Object value) throws Exception {
		var comp = identifiers.get(componentID);
		if(comp == null) throw new Exception("Component ID not found: "+ componentID);
		else
			comp.setProperty(property, value);
	}
	private boolean initialized = false;
	private double timeElapsed = 0;
}