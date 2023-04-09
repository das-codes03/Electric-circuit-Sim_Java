package Backend.simulator;

import java.util.*;

//import javax.measure.converter.Float64Converter;

import org.apache.commons.math3.linear.*;

import Backend.simulator.Circuit.Loop.segType;

public class Circuit {

	private Set<Component> components;
	private Set<Segment> segments;
	private Set<Node> nodes;
	private ArrayList<Loop> loops;

	private RealMatrix _r_mat;
	private RealMatrix _l_mat;
	private RealMatrix _e_mat;
	private RealMatrix _i_mat;
	private LUDecomposition solver = null;
	private static final double SHORT_CIRCUIT_CURRENT = 1e50d;
	private static final double MIN_RESISTANCE = 1e-10;

	public class Node {
		private Set<Segment> incidentSegments;
		private boolean visited;
		private int depth;
		private Segment backtrackSeg;

		public static int compareDepth(Node o1, Node o2) {
			// TODO Auto-generated method stub
			if (o1.depth == o2.depth)
				return 0;
			else if (o1.depth < o2.depth)
				return -1;
			else
				return 1;
		}

		public Node() {
			visited = false;
			depth = 0;
			backtrackSeg = null;
			incidentSegments = new HashSet<>();
		}

		public Node(Node node) {
			node.incidentSegments = this.incidentSegments;
			node.visited = this.visited;
			node.depth = this.depth;
			node.backtrackSeg = this.backtrackSeg;
		}
	}

	public class Segment {
		boolean Closed;
		boolean visited;

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

		ArrayList<Integer> belongsToLoops;
		public double resistance, inductance, capacitance, emf, charge, current;

		public Segment(Node n0, Node n1) {

			this.n0 = n0;
			this.n1 = n1;
			this.resistance = 0;
			this.inductance = 0;
			this.capacitance = 0;
			this.emf = 0;
			this.charge = 0;
			this.current = 0;
			belongsToLoops = new ArrayList<>(0);
			Closed = true;
			visited = false;
		}

	}

	public class Loop {
		ArrayList<Segment> Lsegments;
		ArrayList<Segment> Rsegments;
		boolean Closed = true;

		enum segType {
			LEFT, RIGHT, IGNORED
		}

		void includeSegment(Segment seg, int index, segType sType) {
			seg.belongsToLoops.add(index);
			if (sType == segType.LEFT)
				this.Lsegments.add(seg);
			else
				this.Rsegments.add(seg);
		}

		public Loop() {
			super();
			this.Lsegments = new ArrayList<>(0);
			this.Rsegments = new ArrayList<>(0);

			Closed = true;
		}
	}

	Node AddNode() {
		Node n = new Node();
		nodes.add(n);
		return n;
	}

	public Segment AddSegment() {
		return AddSegment(null, null);
	}

	public Segment AddSegment(Node n0, Node n1) {
		if (n0 == null) {
			n0 = AddNode();
		}
		if (n1 == null) {
			n1 = AddNode();
		}
		Segment s = new Segment(n0, n1);
		segments.add(s);
		s.n0.incidentSegments.add(s);
		s.n1.incidentSegments.add(s);
		return s;
	}

	public Component addComponent(Component comp) {
		components.add(comp);
		return comp;
	}

	public Circuit() {
		super();
		this.components = new HashSet<>();
		this.segments = new HashSet<>();
		this.nodes = new HashSet<>();
		this.loops = new ArrayList<>();
	}

//	public void MakeConnection(Segment s0, int n0_index, Segment s1, int n1_index) throws Exception {
//		if (n0_index > 1 || n0_index < 0 || n1_index > 1 || n1_index < 0) {
//			throw new Exception("Node index can be only 0 or 1");
//		} else {
//			s0.nodes[n0_index] = s1.nodes[n1_index] = MergeNodes(new Node[] { s0.nodes[n0_index], s1.nodes[n1_index] });
//		}
//	}

	public Node MergeNodes(ArrayList<Node> nArray) throws Exception {
		Exception nodenotfound = new NullPointerException("No node found");
		if (nArray.size() == 0) {
			throw nodenotfound;
		}
		for (Node n : nArray) {
			if (n == null) {
				throw nodenotfound;
			}
		}
		Node nTemp = nArray.get(0);
		for (int i = 1; i < nArray.size(); ++i) {
			nTemp.incidentSegments.addAll(nArray.get(i).incidentSegments);

			for (Segment s : nArray.get(i).incidentSegments) {
				if (s.n0 == nArray.get(i)) {
					s.n0 = nTemp;
				} else {
					s.n1 = nTemp;
				}
			}
			nodes.remove(nArray.get(i));
		}
		return nTemp;
	}

	void GetSpanningTreeUtil(Node root, Set<Segment> ignoredEdges) {
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

	void GetLoops(ArrayList<Loop> loops) {

		interface lowerDepth {
			Node GetLower(Segment a);
		}

		lowerDepth depth = (Segment a) -> {
			return (a.n0.depth <= a.n1.depth) ? a.n0 : a.n1;
		};

		Set<Segment> ignoredEdges = new HashSet<Segment>(); // Set of ignored edges will be stored here

		for (Node n : nodes) {
			if (!n.visited)
				GetSpanningTreeUtil(n, ignoredEdges);
		}
		for (Segment e : ignoredEdges) {

			Loop l = new Loop(); // new LOOP
			int index = loops.size();
			l.includeSegment(e, index, segType.IGNORED); // Adds a segment to loop, and sets belonging loop index of
															// segment

			// Backtracking logic
			Segment LHS_Segment = e.n0.backtrackSeg;
			Segment RHS_Segment = e.n1.backtrackSeg;

			Set<Segment> leftList = new HashSet<Segment>();
			Set<Segment> rightList = new HashSet<Segment>();
			while (LHS_Segment != null) {
				leftList.add(LHS_Segment);
				LHS_Segment = depth.GetLower(LHS_Segment).backtrackSeg;
			}
			while (RHS_Segment != null) {
				rightList.add(RHS_Segment);
				RHS_Segment = depth.GetLower(RHS_Segment).backtrackSeg;
			}
			Set<Segment> symmetricDiff = new HashSet<Segment>(leftList);
			symmetricDiff.addAll(rightList);
			Set<Segment> tmp = new HashSet<Segment>(leftList);
			tmp.retainAll(rightList); // intersection of two lists
			symmetricDiff.removeAll(tmp); // remove the intersection
			for (var s : symmetricDiff) {
				l.includeSegment(s, index, (leftList.contains(s) ? segType.LEFT : segType.RIGHT));
			}
			loops.add(l);
		}
	}

	public RealMatrix solveCurrent(double dt) {
		var _lhs = (_r_mat.scalarMultiply(dt)).add(_l_mat);
		var _rhs = (_l_mat.multiply(_i_mat)).add(_e_mat.scalarMultiply(dt));
		solver = new LUDecomposition(_lhs, 0);
		_i_mat = solver.getSolver().solve(_rhs);
		return _i_mat;
	}

	public void updateSegments(double dt) {
		for (Segment s : segments) {
			s.current = 0;
		}
		for (int i = 0; i < _i_mat.getRowDimension(); ++i) {
			for (Segment s : loops.get(i).Lsegments) {
//				int mult = 1;
//				if (s.n0.depth <= s.n1.depth) {
//					mult = -1;
//				}
				s.current = s.current + _i_mat.getEntry(i, 0);// * mult;
//				if(s.current.isGreaterThan(SHORT_CIRCUIT_CURRENT)) {
//					throw new Exception("Circuit has short circuit. At segment: " + s.hashCode());
//				}
				if ((s.capacitance != 0) || !s.Closed)
					s.charge = s.charge + _i_mat.getEntry(i, 0) * dt;// * mult;
				else
					s.charge = 0;
			}
			for (Segment s : loops.get(i).Rsegments) {
//				int mult = 1;
//				if (s.n0.depth <= s.n1.depth) {
//					mult = -1;
//				}
				s.current = s.current + _i_mat.getEntry(i, 0) * -1;// * mult;
//				if(s.current.isGreaterThan(SHORT_CIRCUIT_CURRENT)) {
//					throw new Exception("Circuit has short circuit. At segment: " + s.hashCode());
//				}
				if ((s.capacitance != 0) || !s.Closed)
					s.charge = s.charge + _i_mat.getEntry(i, 0) * dt * -1;// * mult;
				else
					s.charge = 0;
			}
		}
	}

	public RealMatrix GenerateEmfMatrix() {
		for (int i = 0; i < _e_mat.getRowDimension(); ++i) {
			_e_mat.setEntry(i, 0, 0);
		}
		int i = 0;
		for (Loop l : loops) { // Iterate through all loops
			for (Segment s : l.Lsegments) { // Iterate through all segments of each loop
//				int mult = 1;
//				if (s.n0.depth <= s.n1.depth) {
//					mult = -1;
//				}

				_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) + s.emf);// * mult); // Add emf of every segment
				if (s.capacitance != 0)
					_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) - s.charge / s.capacitance);// * mult); // capactive emf
																							// too
				else if (!s.Closed)
					_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) - s.charge * 10000000000.0);// * mult); // TODO add OPEN
				// capacitance
				// property in
				// // GenerateResistanceMAtrix()
			}
			for (Segment s : l.Rsegments) { // Iterate through all segments of each loop
//				int mult = 1;
//				if (s.n0.depth <= s.n1.depth) {
//					mult = -1;
//				}

				_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) + s.emf * -1);// * mult); // Add emf of every segment
				if (s.capacitance != 0)
					_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) - s.charge / s.capacitance * -1);// * mult); //
																									// capactive emf too
				else if (!s.Closed)
					_e_mat.setEntry(i, 0, _e_mat.getEntry(i, 0) - s.charge * 10000000000.0 * -1);// * mult); // TODO add
																									// OPEN
				// capacitance
				// property in
				// // GenerateResistanceMAtrix()
			}
			++i;
		}
		return _e_mat;
	}

	public RealMatrix GenerateInductanceMatrix() {
		_l_mat = _l_mat.scalarMultiply(0);
		int i = 0;
		for (Loop l : loops) { // Iterate through all loops
			for (Segment s : l.Lsegments) { // Iterate through all segments of each loop
				for (int b : s.belongsToLoops) {
					_l_mat.setEntry(i, b, _l_mat.getEntry(i, b) + s.inductance);
				}
			}
			for (Segment s : l.Rsegments) { // Iterate through all segments of each loop
				for (int b : s.belongsToLoops) {
					_l_mat.setEntry(i, b, _l_mat.getEntry(i, b) + s.inductance);
				}
			}
			++i;
		}
		return _l_mat;
	}

	public RealMatrix GenerateResistanceMatrix(double dt) {
		_r_mat = _r_mat.scalarMultiply(0);
		int i = 0;
		for (Loop l : loops) { // Iterate through all loops
			for (Segment s : l.Lsegments) { // Iterate through all segments of each loop
				for (int b : s.belongsToLoops) {
					_r_mat.setEntry(i, b, _r_mat.getEntry(i, b) + s.resistance);
					if (s.capacitance != 0) {
						_r_mat.setEntry(i, b, _r_mat.getEntry(i, b) + dt / s.capacitance);
					}
				}
			}
			for (Segment s : l.Rsegments) { // Iterate through all segments of each loop
				for (int b : s.belongsToLoops) {
					_r_mat.setEntry(i, b, _r_mat.getEntry(i, b) + s.resistance);
					if (s.capacitance != 0) {
						_r_mat.setEntry(i, b, _r_mat.getEntry(i, b) + dt / s.capacitance);
					}
				}
			}
			++i;
		}
		return _r_mat;
	}

	void clearComponents() {
		components.clear();
	}

	public void initialiseCircuit() {
		// clean unused nodes
		Iterator<Node> n_itr = nodes.iterator();
		while (n_itr.hasNext()) {
			Node n = n_itr.next();
			if (n.incidentSegments.size() == 0) {
				n_itr.remove();
			}
		}
		GetLoops(loops);
		System.out.println("Circuit was initialised. " + loops.size() + " loops generated.");
		int n = loops.size();
		_r_mat = MatrixUtils.createRealMatrix(new double[n][n]);
		_l_mat = MatrixUtils.createRealMatrix(new double[n][n]);
		_i_mat = MatrixUtils.createRealMatrix(new double[n][1]);
		_e_mat = MatrixUtils.createRealMatrix(new double[n][1]);
	}
}
