package Backend.api;

import java.util.HashMap;

public abstract class Component {
	public Component(Circuit c) {
		super();
		this.c = c;
		this.t = 0;
	}

	protected Circuit.Segment[] segments;
	protected HashMap<String, Object> properties = new HashMap<>();
	protected HashMap<String, Object> states = new HashMap<>();
	protected Circuit c;//associated circuit
	protected double t;
	public Object getState(String s) throws NoSuchFieldException {
		var state = states.get(s);
		if (state == null)
			throw new NoSuchFieldException(this.getClass().getName() + " doesn't contain state " + s);
		return state;
	}
	public HashMap<String, Object> getAllStates(){
		HashMap<String, Object> temp = new HashMap<>();
		for(var k : states.keySet()) {
			temp.put(k, states.get(k));
		}
		return temp;
	}
	public void setProperty(String property,Object value) throws NoSuchFieldException {
		var x = properties.get(property);
		if(x == null) throw new NoSuchFieldException(this.getClass().getName() + " doesn't contain property " + property);
		else {
			properties.put(property, value);
			updateProperties();
		}
	}
	public Object getProperty(String property) throws NoSuchFieldException {
		var x = properties.get(property);
		if(x == null) throw new NoSuchFieldException(this.getClass().getName() + " doesn't contain property " + property);
		else
			return properties.get(property);
	}
	public void updateState( double t, double dt) {
		this.t = t;
	};
	public abstract void updateProperties();
	public abstract Circuit.Node getExternalNode(int index1) throws Exception;

}
