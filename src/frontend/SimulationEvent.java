package frontend;

import java.util.ArrayList;

import Backend.api.SimulatorAPI;
import uiPackage.DeviceUI;
import uiPackage.NodeUI;
import uiPackage.RenderingCanvas;
import uiPackage.Wire;

public class SimulationEvent implements Runnable {
	public SimulationEvent(ArrayList<DeviceUI> devices, ArrayList<Wire> wires, RenderingCanvas canvas) {
		this.canvas = canvas;
		this.devices = devices;
		this.wires = wires;
		sim = new SimulatorAPI();
		// gather the components
		for (var c : devices) {
			try {
				sim.addComponent(c.getID(), c.getTypeName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// gather connection nodes
		for (var w : wires) {
			ArrayList<int[]> data = new ArrayList<>();
			for (var n : w.nodes) {
				if (n.parentDevice != null) {
					data.add(new int[] { n.parentDevice.getID(), n.getNodeIndex() });
				}
			}
			sim.connect(data);
		}
	}
	private ArrayList<DeviceUI> devices;
	private ArrayList<Wire> wires;
	public SimulatorAPI sim;
	private RenderingCanvas canvas;
	public boolean running = true;

	@Override
	public void run() {
		Thread thr = new Thread(sim);
		thr.start();

		int framesPerSecond = 120;
		int stepMS = (int) (1000.0 / framesPerSecond);
		double lastCaptured = 0;
		while (running) {
			var t1 = System.nanoTime();
			//STEP 1: get updated properties from devices
			for(var c : devices) {
				c.revalidateProperties(this);
			}
			boolean flag = false;
			synchronized (sim) {
				if (lastCaptured < sim.state.getT()) {
					lastCaptured = sim.state.getT();
					flag = true;
					for (var k : sim.state.stateData.keySet()) {
						SimUiManager.components.get(k).updateAttributes(sim.state.stateData.get(k));
					}
				}
			}
			if (flag)
				canvas.Render();
			var t2 = System.nanoTime();

			if (t2 - t1 < stepMS * 1000000) {
//				Thread.currentThread().sleep(framesPerSecond);
				try {
					Thread.currentThread().sleep(stepMS-(t2 - t1)/1000000 );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
//			Thread.currentThread().sleep(framesPerSecond);
		}
		sim.stop();
	}

}
