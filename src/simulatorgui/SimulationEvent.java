package simulatorgui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import circuitlogic.solver.SimulatorAPI;
import circuitlogic.solver.SimulatorAPI.mode;
import simulatorgui.rendering.DeviceUI;
import simulatorgui.rendering.WireUI;
import utilities.NumericUtilities;

public class SimulationEvent implements Runnable {
	public SimulationEvent(ArrayList<DeviceUI> devices, ArrayList<WireUI> wireUIs) {
		this.devices = devices;
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
		Set<WireUI> visitedWires = new HashSet<>();
		for (var w : wireUIs) {
			if (visitedWires.contains(w))
				continue;
			ArrayList<int[]> data = new ArrayList<>();
			Stack<WireUI> wireStack = new Stack<>();
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
	public final SimulatorAPI sim;
	public boolean running = true;

	@Override
	public void run() {
		sim.play();
		Thread thr = new Thread(sim);
		thr.start();

		int framesPerSecond = 30;
		int stepMS = (int) (1000.0 / framesPerSecond);
		double lastCaptured = 0;
		while (running && sim.getMode() != mode.TERMINATED) {
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
						devices.get(k).writeState(sim.state.stateData.get(k), lastCaptured);
					}
				}
			}
			if (flag)
				Driver.getDriver().Render();
			var t2 = System.nanoTime();

			if (t2 - t1 < stepMS * 1000000) {

				try {
					Thread.currentThread();
					Thread.sleep(stepMS - (t2 - t1) / 1000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Driver.getDriver().mainWin.graphs.currT = lastCaptured;
			Driver.getDriver().mainWin.runningLabel.setText("RUNNING | Time elapsed = " +NumericUtilities.getPrefixed(lastCaptured, 6) + "s");
			
		}
		sim.stop();
		Driver.getDriver().mainWin.runningLabel.setText("");
		Driver.getDriver().stopSimulation();	
	}
}
