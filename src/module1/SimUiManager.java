package module1;

import java.awt.Point;
import java.util.Vector;

import uiPackage.NodeUI;
import uiPackage.DeviceUI;
import uiPackage.Wire;

public class SimUiManager {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		var mainWin = new MainWindow();
		for(int i = 0; i <10; ++i) {
			// temp =  new ComponentDescriptor(MainWindow.renderCanvas,"/resources/transparent.png",100,100);
			var temp = new DeviceUI(MainWindow.renderCanvas,"/resources/transparent.png",(int)(100),(int)(100));
			temp.setLocalPosition((int)(Math.random() * 1000), (int)(Math.random() * 1000)).setRotation(360 * Math.random());
			//MainWindow.renderCanvas.Render();
			MainWindow.renderCanvas.Render();
		}
//		
//		Wire w = new Wire(MainWindow.renderCanvas);
//		Vector<NodeUI> nodes = new Vector<>();
//		nodes.add(new NodeUI(new Point(0,100),MainWindow.renderCanvas));
//		nodes.add(new NodeUI(new Point(0,200),MainWindow.renderCanvas));
//		nodes.add(new NodeUI(new Point(-300,200),MainWindow.renderCanvas));
//		nodes.add(new NodeUI(new Point(-500,500),MainWindow.renderCanvas));
//		w.setWire(nodes);

		//Wire w = new Wire(MainWindow.renderCanvas);
//		w.nodes.add(new NodeUI(new Point(0, 0)));
//		w.nodes.add(new NodeUI(new Point(100, 100)));
//		w.nodes.add(new NodeUI(new Point(200, 100)));
//		w.nodes.add(new NodeUI(new Point(300, 100)));
//		w.nodes.add(new NodeUI(new Point(400, 0)));
//		w.nodes.add(new NodeUI(new Point(500, 0)));
		//w.getTransformedBounds();
		//MainWindow.renderCanvas.wires.add(w);
		//MainWindow.renderCanvas.componentGrid.store(w);
		
		// ComponentListUI.append((ComponentDescriptor) new
		// ComponentDescriptor(MainWindow.renderCanvas,"/resources/transparent.png",100,100).setLocalPosition(0,0).setRotation(0));

		System.out.println("done");
//		ComponentListUI.append((ComponentDescriptor) new ComponentDescriptor(MainWindow.renderCanvas,"/resources/transparent.png",100,100).setLocalPosition(100, 200).setRotation(35));
//		ComponentListUI.append((ComponentDescriptor) new ComponentDescriptor(MainWindow.renderCanvas,"/resources/transparent.png",100,100).setLocalPosition(100, 230).setRotation(5));
//		
	}

}
