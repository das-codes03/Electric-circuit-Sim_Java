package frontend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import Backend.api.SimulatorAPI;
import uiPackage.DeviceUI;
import uiPackage.NodeUI;
import uiPackage.RenderingCanvas;
import uiPackage.Wire;

public class SimulationEvent implements Runnable {
	public SimulationEvent(ArrayList<DeviceUI> devices, ArrayList<Wire> wires) {
		this.devices = devices;
		this.wires = wires;
		sim = new SimulatorAPI();
		// gather the components
		for (var c : devices) {
			try {
				sim.addComponent(c.getID(), c.getBackendClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// gather connection nodes
		Set<Wire> visitedWires = new HashSet<>();
		for (var w : wires) {
			if (visitedWires.contains(w))
				continue;
			ArrayList<int[]> data = new ArrayList<>();
			Stack<Wire> wireStack = new Stack<>();
			wireStack.push(w);
			visitedWires.add(w);
			while (!wireStack.isEmpty()) {
				var curr = wireStack.pop();

				for (var n : curr.nodes) {
					if (n.parentDevice != null) {
						data.add(new int[] { n.parentDevice.getID(), n.getNodeIndex() });
					}
					for (var x : n.incidentWires) {
						if (!visitedWires.contains(x)) {
							wireStack.push(x);
							visitedWires.add(x);
						}
					}
				}
			}
			sim.connect(data);
		}
	}

	private final ArrayList<DeviceUI> devices;
	private final ArrayList<Wire> wires;
	public final SimulatorAPI sim;
	public boolean running = true;

	@Override
	public void run() {
		Thread thr = new Thread(sim);
		thr.start();

		int framesPerSecond = 120;
		int stepMS = (int) (1000.0 / framesPerSecond);
		double lastCaptured = 0;
		while (running && thr.isAlive()) {
			if (thr.isAlive() == false) {
				System.out.println("Died");
			}
			var t1 = System.nanoTime();
			// STEP 1: get updated properties from devices
			for (var c : devices) {
				var data = c.readProperties();
				for (var d : data.keySet()) {
					sim.setProperty(c.getID(), d, data.get(d));
				}
			}
			boolean flag = false;
			synchronized (sim) {
				if (lastCaptured < sim.state.getT()) {
					lastCaptured = sim.state.getT();
					flag = true;
					for (var k : sim.state.stateData.keySet()) {
						devices.get(k).writeState(sim.state.stateData.get(k));
					}
				}
			}
			if (flag)
				Driver.getDriver().Render();
			var t2 = System.nanoTime();

			if (t2 - t1 < stepMS * 1000000) {

				try {
					Thread.currentThread().sleep(stepMS - (t2 - t1) / 1000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		sim.stop();
	}

}
