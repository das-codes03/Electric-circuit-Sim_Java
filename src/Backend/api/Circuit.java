package Backend.api;

import java.util.*;
import org.apache.commons.math3.linear.*;

public class Circuit {

	// "Building blocks" of the circuit
	private ArrayList<Component> components;
	private ArrayList<Circuit.Segment> segments;
	private ArrayList<Circuit.Node> nodes;
	private ArrayList<Circuit.Loop> loops;
	// ********************************

	// Math related stuff
	private RealMatrix _r_mat; // resistance matrix [nxn]
	private RealMatrix _l_mat; // inductance matrix [nxn]
	private RealMatrix _e_mat; // emf vector [nx1]
	private RealMatrix _i_mat; // current vector [nx1]
	private LUDecomposition solver = null; // solver
	// ********************************
	private static final double MIN_RESISTANCE = 1e-50;
	private static final double MIN_INDUCTANCE = 1e-50;
	private static final double MIN_EMF = 1e-50;

	public class Node {
		private Set<Segment> incidentSegments;
		private boolean visited;
		private int depth;
		private Segment backtrackSeg;

		private Node() {
			visited = false;
			depth = 0;
			backtrackSeg = null;
			incidentSegments = new HashSet<>();
		}
	}

	public class Segment {
		private boolean visited;
		private Node n0;
		private Node n1;

		public Node getNode(int index) {
			if (index < 0 || index > 1)
				throw new IndexOutOfBoundsException("Node index must be 0 or 1");
			else if (index == 0)
				return n0;
			else
				return n1;
		}

		private ArrayList<Integer> belongsToLoops;
		private double resistance, inductance, capacitance, emf, charge, current, breakdownVoltage;

		public double getCurrent() {
			return current;
		}

		public double getEmf() {
			return emf;
		}

		public double getResistance() {
			return resistance;
		}

		public double getInductance() {
			return inductance;
		}

		public double getCapacitance() {
			return capacitance;
		}

		public double getCharge() {
			return charge;
		}

		public void setEMF(double e) {
			this.emf = e;
		}

		public void setResistance(double r) {
			this.resistance = r;
		}

		public void setInductance(double l) {
			this.inductance = l;
		}

		public void setCapacitance(double c) {
			this.capacitance = c;
		}

		private Segment(Node n0, Node n1) {
			this.n0 = n0;
			this.n1 = n1;
			this.resistance = 0;
			this.inductance = 0;
			this.capacitance = Double.POSITIVE_INFINITY;
			this.emf = 0;
			this.charge = 0;
			this.current = 0;
			this.breakdownVoltage = 0;
			this.belongsToLoops = new ArrayList<>(0);
			this.visited = false;
		}

		public void setBreakdown(double d) {
			this.breakdownVoltage = d;
		}
	}

	public class Loop {
		private Map<Segment, Boolean> segments;

		private void includeSegment(Segment seg, int index, boolean rev) {
			seg.belongsToLoops.add(index);
			segments.put(seg, rev);
		}

		private Loop() {
			this.segments = new LinkedHashMap<Segment, Boolean>();
		}
	}

	private Node addNode() {
		Node n = new Node();
		nodes.add(n);
		return n;
	}

	public Segment addSegment() {
		return addSegment(null, null);
	}

	public Segment addSegment(Node n0, Node n1) {
		if (n0 == null) {
			n0 = addNode();
		}
		if (n1 == null) {
			n1 = addNode();
		}
		Segment s = new Segment(n0, n1);
		segments.add(s);
		s.n0.incidentSegments.add(s);
		s.n1.incidentSegments.add(s);
		return s;
	}

	Component addComponent(Component comp) {
		components.add(comp);
		return comp;
	}

	Circuit() {
		super();
		this.components = new ArrayList<>();
		this.segments = new ArrayList<>();
		this.nodes = new ArrayList<>();
		this.loops = new ArrayList<>();
	}

	public Node mergeNodes(Node n0, Node n1) {
		var temp = new ArrayList<Node>();
		temp.add(n0);
		temp.add(n1);
		return mergeNodes(temp);
	}

	public Node mergeNodes(ArrayList<Node> nArray) {
		if (nArray == null)
			throw new NullPointerException();
		Node nTemp = null;
		int i = 0;
		while (nTemp == null && i < nArray.size()) {
			nTemp = nArray.get(i);
			++i;
		}
		if (nTemp == null)
			return null;
		for (; i < nArray.size(); ++i) {
			var curr = nArray.get(i);
			if (curr == null)
				continue;
			nTemp.incidentSegments.addAll(curr.incidentSegments);
			for (Segment s : curr.incidentSegments) {
				if (s.n0 == curr) {
					s.n0 = nTemp;
				} else if (s.n1 == curr) {
					s.n1 = nTemp;
				}
			}
			nodes.remove(curr);
		}
		return nTemp;
	}

	private void generateSpanningTree(Node root, Set<Segment> ignoredEdges) {
		if (root == null || ignoredEdges == null) {
			throw new NullPointerException("Root is null");
		}
		Queue<Node> bfsQueue = new ArrayDeque<Node>();
		root.depth = 0;
		root.backtrackSeg = null;
		root.visited = true;
		bfsQueue.add(root);
		while (!bfsQueue.isEmpty()) {
			Node curr = bfsQueue.peek();
			bfsQueue.poll();
			for (Segment x : curr.incidentSegments) {
				if (x.visited)
					continue;

				x.visited = true;

				if (x == curr.backtrackSeg)
					continue;
				boolean found = false;

				if ((x.n0 != curr && (x.n0.visited)) || (x.n1 != curr && (x.n1.visited)) || (x.n0 == x.n1)) {
					found = true;
					ignoredEdges.add(x);
				}

				if (!found) {
					Node a = (((x.n0) == curr) ? (x.n1) : (x.n0));
					a.depth = Integer.max(x.n0.depth, x.n1.depth) + 1;
					a.backtrackSeg = x;
					a.visited = true;
					bfsQueue.add(a);
				}
			}
		}
		return;
	}

	private void getLoops(ArrayList<Loop> loops) {

		interface lowerDepth {
			Node GetLower(Segment a);
		}

		lowerDepth depth = (Segment a) -> {
			return (a.n0.depth <= a.n1.depth) ? a.n0 : a.n1;
		};

		Set<Segment> ignoredEdges = new HashSet<Segment>(); // Set of ignored edges will be stored here

		for (Node n : nodes) {
			if (!n.visited)
				generateSpanningTree(n, ignoredEdges);
		}
		for (Segment e : ignoredEdges) {

			Loop l = new Loop(); // new LOOP
			int index = loops.size();
			// Backtracking logic
			Segment LHS_Segment = e.n0.backtrackSeg;
			Segment RHS_Segment = e.n1.backtrackSeg;
			ArrayList<Segment> leftList = new ArrayList<>();
			ArrayList<Segment> rightList = new ArrayList<>();
			if (LHS_Segment == null) { // Means LHS segment doesn't exist, already at top of tree
				while (RHS_Segment != null) {
					rightList.add(RHS_Segment);
					RHS_Segment = depth.GetLower(RHS_Segment).backtrackSeg;
				}
			} else if (RHS_Segment == null) { // Means RHS segment doesn't exist, already at top of tree
				while (LHS_Segment != null) {
					leftList.add(LHS_Segment);
					LHS_Segment = depth.GetLower(LHS_Segment).backtrackSeg;
				}
			} else {
				while (depth.GetLower(RHS_Segment).depth > depth.GetLower(LHS_Segment).depth) {
					rightList.add(RHS_Segment);
					RHS_Segment = depth.GetLower(RHS_Segment).backtrackSeg;
				}

				while (depth.GetLower(LHS_Segment).depth > depth.GetLower(RHS_Segment).depth) {
					leftList.add(LHS_Segment);
					LHS_Segment = depth.GetLower(LHS_Segment).backtrackSeg;
				}

				while (LHS_Segment != RHS_Segment) {
					rightList.add(RHS_Segment);
					leftList.add(LHS_Segment);
					RHS_Segment = depth.GetLower(RHS_Segment).backtrackSeg;
					LHS_Segment = depth.GetLower(LHS_Segment).backtrackSeg;
				}
			}
			for (int i = 0; i < leftList.size(); ++i) {
				l.includeSegment(leftList.get(i), index, false);
			}
			for (int i = rightList.size() - 1; i >= 0; --i) {
				l.includeSegment(rightList.get(i), index, true);
			}

			l.includeSegment(e, index, e.n0.depth >= e.n1.depth); // Adds a segment to loop, and sets belonging loop
																	// index of segment
			loops.add(l);
		}
	}

	RealMatrix solveCurrent(double dt) {
		var _lhs = (_r_mat.scalarMultiply(dt)).add(_l_mat);
		var _rhs = (_l_mat.multiply(_i_mat)).add(_e_mat.scalarMultiply(dt));
		solver = new LUDecomposition(_lhs, 0);
		_i_mat = solver.getSolver().solve(_rhs);
		for (int i = 0; i < _i_mat.getRowDimension(); ++i) {
			if (Double.isNaN(_i_mat.getEntry(i, 0))) {
				_i_mat.setEntry(i, 0, 0);
			}
		}
		return _i_mat;
	}

	void updateSegments(double dt) {
		for (Segment s : segments) {
			s.current = 0;
		}
		for (int i = 0; i < _i_mat.getRowDimension(); ++i) {
			for (Segment s : loops.get(i).segments.keySet()) {
				int mult = 1;
				if (loops.get(i).segments.get(s) == false) {
					mult = -1;
				}
				if (s.n0.depth >= s.n1.depth) {
					mult *= -1;
				}
				s.current += _i_mat.getEntry(i, 0) * mult;

			}
		}
		for (var s : segments) {
			s.charge += -s.current * dt;
			if (Math.abs(s.charge / s.capacitance) >= s.breakdownVoltage) {
				s.charge = 0;
			}
		}
	}

	RealMatrix generateEmfMatrix() {
		for (int i = 0; i < _e_mat.getRowDimension(); ++i) {
			_e_mat.setEntry(i, 0, 0);
		}
		for (int i = 0; i < loops.size(); ++i) { // Iterate through all loops
			var l = loops.get(i);
			for (Segment s : l.segments.keySet()) { // Iterate through all segments of each loop
				int mult = 1;
				if (loops.get(i).segments.get(s) == false) {
					mult = -1;
				}
				if (s.n0.depth >= s.n1.depth) {
					mult *= -1;
				}
				_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) + s.emf * mult); // Add emf of every segment
				_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) + s.charge / s.capacitance * mult); // capactive emf too
			}
		}
		return _e_mat;
	}

	RealMatrix generateInductanceMatrix() {
		_l_mat = _l_mat.scalarMultiply(0);
		for (int i = 0; i < loops.size(); ++i) { // Iterate through all loops
			var l = loops.get(i);
			for (Segment s : l.segments.keySet()) { // Iterate through all segments of each loop
				for (int b : s.belongsToLoops) {
					int mult = 1;
					if (loops.get(b).segments.get(s) != loops.get(i).segments.get(s)) {
						mult = -1;
					}
					_l_mat.setEntry(i, b, _l_mat.getEntry(i, b) + s.inductance * mult);
				}
			}
		}
		return _l_mat;
	}

	RealMatrix generateResistanceMatrix(double dt) {
		_r_mat = _r_mat.scalarMultiply(0);
		for (int i = 0; i < loops.size(); ++i) { // Iterate through all loops
			var l = loops.get(i);
			for (Segment s : l.segments.keySet()) { // Iterate through all segments of each loop
				for (int b : s.belongsToLoops) {
					int mult = 1;
					if (loops.get(b).segments.get(s) != loops.get(i).segments.get(s)) {
						mult = -1;
					}
					_r_mat.setEntry(i, b, _r_mat.getEntry(i, b) + s.resistance * mult);
					if (s.capacitance != 0) {
						_r_mat.setEntry(i, b, _r_mat.getEntry(i, b) + dt / s.capacitance * mult);
					}
				}
			}
		}
		return _r_mat;
	}

	ArrayList<Loop> shortCircuitTest() {
		ArrayList<Loop> scLoops = new ArrayList<>();
		for (int i = 0; i < this.loops.size(); ++i) {
			boolean flag = false;
			if (Math.abs(_e_mat.getEntry(i, 0)) > MIN_EMF)
				flag = true;
			if (flag) {
				if (_r_mat.getEntry(i, i) > MIN_RESISTANCE || _l_mat.getEntry(i, i) > MIN_INDUCTANCE)
					flag = false;
			}
			if (flag) {
				scLoops.add(loops.get(i));
			}
		}
		return scLoops;
	}

	void initialiseCircuit() {
		getLoops(loops);
		System.out.println("Circuit was initialised. " + loops.size() + " loops generated.");
		int n = loops.size();
		_r_mat = MatrixUtils.createRealMatrix(new double[n][n]);
		_l_mat = MatrixUtils.createRealMatrix(new double[n][n]);
		_i_mat = MatrixUtils.createRealMatrix(new double[n][1]);
		_e_mat = MatrixUtils.createRealMatrix(new double[n][1]);
	}
}
