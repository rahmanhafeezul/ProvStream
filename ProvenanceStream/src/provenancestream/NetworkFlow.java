package provenancestream;

/**
 *
 * @author HafeezulRahman
 */

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.SinkAdapter;


public class NetworkFlow extends SinkAdapter implements DynamicAlgorithm {
	public static final String PREFIX = "__NS_";
	protected static final int INFINITE_CAPACITY = -1;
	public static enum PricingStrategy {
		FIRST_NEGATIVE,
		MOST_NEGATIVE
	}

	/**
	 * The status of the current solution.
	 */
	public static enum SolutionStatus {

		UNDEFINED,
		OPTIMAL,
		INFEASIBLE,
		UNBOUNDED
	}

	protected String supplyName;
	protected String capacityName;
	protected String costName;
	protected PricingStrategy pricingStrategy = PricingStrategy.MOST_NEGATIVE;
	protected Graph graph;
	protected Map<String, NSNode> nodes;
	protected Map<String, NSArc> arcs;
	protected Set<NSArc> nonBasicArcs;
	protected NSNode root;
	protected BigMNumber objectiveValue;
	protected SolutionStatus solutionStatus = SolutionStatus.UNDEFINED;
	protected NSArc enteringArc;
	protected NSNode join;
	protected NSNode first;
	protected NSNode second;
	protected NSNode oldSubtreeRoot;
	protected NSNode newSubtreeRoot;
	protected BigMNumber cycleFlowChange;
	protected NSArc leavingArc;
	protected BigMNumber work1;
	protected BigMNumber work2;
	protected long animationDelay = 0;
	protected boolean fromSink = false;
	protected int logFreq = 0;
	protected PrintStream log = System.err;
	public NetworkFlow(String supplyName, String capaciyName, String costName) {
		this.supplyName = supplyName;
		this.capacityName = capaciyName;
		this.costName = costName;

		objectiveValue = new BigMNumber();
		cycleFlowChange = new BigMNumber();
		work1 = new BigMNumber();
		work2 = new BigMNumber();
	}
	protected void cloneGraph() {
		nodes = new HashMap<String, NSNode>(4 * graph.getNodeCount() / 3 + 2);
		for (Node node : graph) {
			NSNode copy = new NSNode(node);
			nodes.put(copy.id, copy);
		}

		int arcCount = graph.getEdgeCount();
		for (Edge edge : graph.getEachEdge())
			if (!edge.isDirected())
				arcCount++;
		arcs = new HashMap<String, NSArc>(4 * arcCount / 3 + 1);
		for (Edge edge : graph.getEachEdge()) {
			NSArc copy = new NSArc(edge, true);
			arcs.put(copy.id, copy);
			if (!edge.isDirected()) {
				copy = new NSArc(edge, false);
				arcs.put(copy.id, copy);
			}
		}
	}

	protected void createInitialBFS() {
		nonBasicArcs = new HashSet<NSArc>(4 * arcs.size() / 3 + 1);
		for (NSArc arc : arcs.values()) {
			arc.flow = 0;
			arc.status = ArcStatus.NONBASIC_LOWER;
			nonBasicArcs.add(arc);
			if (animationDelay > 0)
				arc.setUIClass();
		}

		root = new NSNode();
		root.id = PREFIX + "ROOT";
		root.potential.set(0);
		root.parent = root;
		root.thread = root;
		root.depth = 0;
		root.supply = 0;
		root.artificialArc = null;

		objectiveValue.set(0);
		for (NSNode node : nodes.values())
			node.createArtificialArc();
		solutionStatus = SolutionStatus.UNDEFINED;
	}

	protected void selectEnteringArcFirstNegative() {
		enteringArc = null;
		BigMNumber reducedCost = work1;

		for (NSArc arc : nonBasicArcs) {
			arc.computeReducedCost(reducedCost);
			if (reducedCost.isNegative()) {
				enteringArc = arc;
				return;
			}
		}
		
		if (!objectiveValue.isInfinite())
			return;

		for (NSNode node : nodes.values()) {
			NSArc arc = node.artificialArc;
			if (arc.status == ArcStatus.NONBASIC_LOWER) {
				arc.computeReducedCost(reducedCost);
				if (reducedCost.isNegative()) {
					enteringArc = arc;
					return;
				}
			}
		}
	}

	protected void selectEnteringArcMostNegative() {
		enteringArc = null;
		BigMNumber reducedCost = work1;
		BigMNumber bestReducedCost = work2;
		bestReducedCost.set(0);

		for (NSArc arc : nonBasicArcs) {
			arc.computeReducedCost(reducedCost);
			if (reducedCost.compareTo(bestReducedCost) < 0) {
				bestReducedCost.set(reducedCost);
				enteringArc = arc;
			}
		}
		if (enteringArc != null)
			return;
		
		// Skip the artificial arcs if the objective value is finite
		if (!objectiveValue.isInfinite())
			return;

		for (NSNode node : nodes.values()) {
			NSArc arc = node.artificialArc;
			if (arc.status == ArcStatus.NONBASIC_LOWER) {
				arc.computeReducedCost(reducedCost);
				if (reducedCost.compareTo(bestReducedCost) < 0) {
					bestReducedCost = reducedCost;
					enteringArc = arc;
				}
			}
		}
	}

	
	protected void selectEnteringArc() {
		switch (pricingStrategy) {
		case FIRST_NEGATIVE:
			selectEnteringArcFirstNegative();
			break;
		case MOST_NEGATIVE:
			selectEnteringArcMostNegative();
			break;
		}
	}

	
	protected void findJoinNode() {
		NSNode i = enteringArc.source;
		NSNode j = enteringArc.target;
		while (i.depth > j.depth)
			i = i.parent;
		while (j.depth > i.depth)
			j = j.parent;
		while (i != j) {
			i = i.parent;
			j = j.parent;
		}
		join = i;
	}

	
	protected void selectLeavingArc() {
		findJoinNode();
		if (enteringArc.status == ArcStatus.NONBASIC_LOWER) {
			first = enteringArc.source;
			second = enteringArc.target;
		} else {
			first = enteringArc.target;
			second = enteringArc.source;
		}

		enteringArc.computeAllowedFlowChange(first, cycleFlowChange);
		leavingArc = enteringArc;

		NSArc arc;
		BigMNumber arcFlowChange = work1;

		for (NSNode node = second; node != join; node = node.parent) {
			arc = node.arcToParent;
			arc.computeAllowedFlowChange(node, arcFlowChange);
			if (arcFlowChange.compareTo(cycleFlowChange) <= 0) {
				cycleFlowChange.set(arcFlowChange);
				oldSubtreeRoot = node;
				newSubtreeRoot = second;
				leavingArc = arc;
			}
		}

		for (NSNode node = first; node != join; node = node.parent) {
			arc = node.arcToParent;
			arc.computeAllowedFlowChange(node.parent, arcFlowChange);
			if (arcFlowChange.compareTo(cycleFlowChange) < 0) {
				cycleFlowChange.set(arcFlowChange);
				oldSubtreeRoot = node;
				newSubtreeRoot = first;
				leavingArc = arc;
			}
		}
	}

	/**
	 * Changes the flows on the arcs belonging to the cycle and updates the
	 * objective value.
	 */
	protected void changeFlows() {
		int delta = (int) cycleFlowChange.getSmall();
		if (delta == 0)
			return;

		enteringArc.computeReducedCost(work1);
		objectiveValue.plusTimes(delta, work1);

		enteringArc.changeFlow(delta, first);
		for (NSNode node = second; node != join; node = node.parent)
			node.arcToParent.changeFlow(delta, node);
		for (NSNode node = first; node != join; node = node.parent)
			node.arcToParent.changeFlow(delta, node.parent);
	}

	protected void updateBFS() {
		NSNode stopNode = oldSubtreeRoot.parent;

		NSNode currentNode = newSubtreeRoot;
		NSNode oldParent = currentNode.parent;
		NSNode newParent = enteringArc.getOpposite(currentNode);
		NSArc oldArc = currentNode.arcToParent;
		NSArc newArc = enteringArc;
		while (currentNode != stopNode) {
			currentNode.changeParent(newParent, newArc);
			newParent = currentNode;
			currentNode = oldParent;
			oldParent = currentNode.parent;
			newArc = oldArc;
			oldArc = currentNode.arcToParent;
		}
	}
	protected void pivot() {
		if (animationDelay > 0 && !fromSink)
			try {
				Thread.sleep(animationDelay);
			} catch (InterruptedException e) {
			}

		changeFlows();
		if (enteringArc == leavingArc) {
			if (enteringArc.status == ArcStatus.NONBASIC_LOWER)
				enteringArc.status = ArcStatus.NONBASIC_UPPER;
			else
				enteringArc.status = ArcStatus.NONBASIC_LOWER;
		} else {
			enteringArc.status = ArcStatus.BASIC;
			nonBasicArcs.remove(enteringArc);
			if ((newSubtreeRoot == first && oldSubtreeRoot == leavingArc.target)
					|| (newSubtreeRoot == second && oldSubtreeRoot == leavingArc.source))
				// The leaving arc is in the direction of the cycle
				leavingArc.status = ArcStatus.NONBASIC_UPPER;
			else
				leavingArc.status = ArcStatus.NONBASIC_LOWER;
			if (!leavingArc.isArtificial())
				nonBasicArcs.add(leavingArc);
			updateBFS();
		}
		if (animationDelay > 0) {
			enteringArc.setUIClass();
			leavingArc.setUIClass();
		}
	}

	/**
	 * The main simplex method loop. Selects leaving and entering arc and
	 * performs a pivot. Loops until there are no more candidates or until
	 * absorbing cycle is found.
	 */
	protected void simplex() {
		int pivots = 0;
		if (logFreq > 0) {
			log.println("Starting simplex...");
			log.printf("%10s%30s%30s%10s%10s%10s%n", "pivot", "entering",
					"leaving", "delta", "cost", "infeas.");
		}
		while (true) {
			selectEnteringArc();
			if (enteringArc == null) {
				if (objectiveValue.isInfinite())
					solutionStatus = SolutionStatus.INFEASIBLE;
				else
					solutionStatus = SolutionStatus.OPTIMAL;
				break;
			}
			selectLeavingArc();
			if (cycleFlowChange.isInfinite()) {
				solutionStatus = SolutionStatus.UNBOUNDED;
				break;
			}
			pivot();
			pivots++;
			if (logFreq > 0 && pivots % logFreq == 0)
				log.printf("%10d%30s%30s%10d%10d%10d%n", pivots,
						enteringArc.id, leavingArc.id, cycleFlowChange.small,
						objectiveValue.small, objectiveValue.big);
		}
		if (logFreq > 0)
			log.printf(
					"Simplex finished (%d pivots). Cost: %d. Status: %s%n%n",
					pivots, objectiveValue.small, solutionStatus);
	}

	public String getSupplyName() {
		return supplyName;
	}

	public String getCapacityName() {
		return capacityName;
	}

	
	public String getCostName() {
		return costName;
	}

	
	public PricingStrategy getPricingStrategy() {
		return pricingStrategy;
	}

	
	public void setPricingStrategy(PricingStrategy pricingStrategy) {
		this.pricingStrategy = pricingStrategy;
	}

	
	public void setAnimationDelay(long millis) {
		animationDelay = millis;
	}

	
	public Graph getGraph() {
		return graph;
	}

	
	public void setLogFrequency(int pivots) {
		logFreq = pivots;
	}

	
	public void setLogStream(PrintStream log) {
		this.log = log;
	}

	public int getNetworkBalance() {
		return -root.supply;
	}

	public SolutionStatus getSolutionStatus() {
		return solutionStatus;
	}

	public long getSolutionCost() {
		return objectiveValue.getSmall();
	}

	
	public long getSolutionInfeasibility() {
		return objectiveValue.big;
	}

	
	public int getInfeasibility(Node node) {
		NSArc artificial = nodes.get(node.getId()).artificialArc;
		return artificial.target == root ? artificial.flow : -artificial.flow;
	}

	
	public <T extends Edge> T getEdgeFromParent(Node node) {
		NSArc arc = nodes.get(node.getId()).arcToParent;
		if (arc.isArtificial())
			return null;
		return graph.getEdge(arc.getOriginalId());
	}

	public <T extends Node> T getParent(Node node) {
		NSNode nsNode = nodes.get(node.getId());
		if (nsNode == root)
			return null;
		return graph.getNode(nsNode.parent.id);
	}

	
	public int getFlow(Edge edge, boolean sameDirection) {
		if (edge.isDirected())
			return sameDirection ? arcs.get(edge.getId()).flow : 0;
		else
			return arcs.get((sameDirection ? "" : PREFIX + "REVERSE_")
					+ edge.getId()).flow;
	}

	
	public int getFlow(Edge edge) {
		return getFlow(edge, true);
	}

	public ArcStatus getStatus(Edge edge, boolean sameDirection) {
		if (edge.isDirected())
			return sameDirection ? arcs.get(edge.getId()).status : null;
		else
			return arcs.get((sameDirection ? "" : PREFIX + "REVERSE_")
					+ edge.getId()).status;
	}

	
	public ArcStatus getStatus(Edge edge) {
		return getStatus(edge, true);
	}


	public void setUIClasses() {
		for (Node node : graph)
			nodes.get(node.getId()).artificialArc.setUIClass();
		for (Edge edge : graph.getEachEdge()) {
			NSArc arc = arcs.get(edge.getId());
			if (!edge.isDirected() && arc.status != ArcStatus.BASIC)
				arc = arcs.get(PREFIX + "REVERSE_" + edge.getId());
			arc.setUIClass();
		}
	}

	// DynamicAlgorithm methods

        @Override
	public void init(Graph graph) {
		this.graph = graph;
		cloneGraph();
		createInitialBFS();
		graph.addSink(this);
	}

        @Override
	public void compute() {
		fromSink = false;
		if (solutionStatus == SolutionStatus.UNDEFINED)
			simplex();
	}

	public void terminate() {
		graph.removeSink(this);
		solutionStatus = SolutionStatus.UNDEFINED;
	}

	// Sink methods

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		if (attribute.equals(costName)) {
			double v = objectToDouble(value);
			if (Double.isNaN(v))
				v = 1;

			NSArc arc = arcs.get(edgeId);
			work1.set((int) v);
			changeCost(arc, work1);

			arc = arcs.get(PREFIX + "REVERSE_" + edgeId);
			if (arc != null) {
				work1.set((int) v);
				changeCost(arc, work1);
			}
		} else if (attribute.equals(capacityName)) {
			double v = objectToDouble(value);
			if (Double.isNaN(v) || v < 0)
				v = INFINITE_CAPACITY;
			NSArc arc = arcs.get(edgeId);
			changeCapacity(arc, (int) v);
			arc = arcs.get(PREFIX + "REVERSE_" + edgeId);
			if (arc != null)
				changeCapacity(arc, (int) v);
		}
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		edgeAttributeAdded(sourceId, timeId, edgeId, attribute, newValue);
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		edgeAttributeAdded(sourceId, timeId, edgeId, attribute, null);
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		if (attribute.equals(supplyName)) {
			double v = objectToDouble(value);
			if (Double.isNaN(v))
				v = 0;

			NSNode node = nodes.get(nodeId);
			changeSupply(node, (int) v);
		}
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		nodeAttributeAdded(sourceId, timeId, nodeId, attribute, newValue);
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		nodeAttributeAdded(sourceId, timeId, nodeId, attribute, null);
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		NSArc arc = new NSArc(graph.getEdge(edgeId), true);
		addArc(arc);
		if (!directed) {
			arc = new NSArc(graph.getEdge(edgeId), false);
			addArc(arc);
		}
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		NSArc arc = arcs.get(edgeId);
		removeArc(arc);
		arc = arcs.get(PREFIX + "REVERSE_" + edgeId);
		if (arc != null)
			removeArc(arc);
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		addNode(new NSNode(graph.getNode(nodeId)));
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		removeNode(nodes.get(nodeId));
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		clearGraph();
	}

	
	protected static double objectToDouble(Object o) {
		if (o != null) {
			if (o instanceof Number)
				return ((Number) o).doubleValue();

			if (o instanceof String) {
				try {
					return Double.parseDouble((String) o);
				} catch (NumberFormatException e) {
				}
			}
		}
		return Double.NaN;
	}

	protected void changeCost(NSArc arc, BigMNumber newCost) {
		if (arc.cost.compareTo(newCost) == 0)
			return;
		objectiveValue.plusTimes(-arc.flow, arc.cost);
		arc.cost.set(newCost);
		objectiveValue.plusTimes(arc.flow, arc.cost);

		if (arc.status == ArcStatus.BASIC) {
			NSNode subtreeRoot = arc.source.arcToParent == arc ? arc.source
					: arc.target;
			subtreeRoot.computePotential();
			for (NSNode node = subtreeRoot.thread; node.depth > subtreeRoot.depth; node = node.thread)
				node.computePotential();
			solutionStatus = SolutionStatus.UNDEFINED;
		} else {
			arc.computeReducedCost(work1);
			if (work1.isNegative())
				solutionStatus = SolutionStatus.UNDEFINED;
		}
	}

	protected void changeSupply(NSNode node, int newSupply) {
		if (node.supply == newSupply)
			return;
		NSArc artificial = node.artificialArc;
		// enter the artificial arc in the tree if not there
		if (artificial.status == ArcStatus.NONBASIC_LOWER) {
			enteringArc = artificial;
			selectLeavingArc();
			// if we are in infinite cycle, switch the direction
			if (cycleFlowChange.isInfinite()) {
				artificial.switchDirection();
				selectLeavingArc();
			}
			fromSink = true;
			pivot();
		}
		// now the artificial arc is basic and we can change its flow
		objectiveValue.plusTimes(-artificial.flow, artificial.cost);
		int delta = newSupply - node.supply;
		node.supply = newSupply;
		root.supply -= delta;
		if (node == artificial.source) {
			artificial.flow += delta;
		} else {
			artificial.flow -= delta;
		}
		if (artificial.flow < 0)
			artificial.switchDirection();

		objectiveValue.plusTimes(artificial.flow, artificial.cost);
		solutionStatus = SolutionStatus.UNDEFINED;

		if (animationDelay > 0)
			artificial.setUIClass();
	}

	protected void changeCapacity(NSArc arc, int newCapacity) {
		if (arc.capacity == newCapacity)
			return;
		if (arc.status == ArcStatus.NONBASIC_LOWER) {
			arc.capacity = newCapacity;
			return;
		}
		if (arc.status == ArcStatus.NONBASIC_UPPER) {
			enteringArc = arc;
			selectLeavingArc();
			fromSink = true;
			pivot();
			solutionStatus = SolutionStatus.UNDEFINED;
		}
		// now the arc is basic ...
		if (newCapacity == INFINITE_CAPACITY || arc.flow <= newCapacity) {
			arc.capacity = newCapacity;
			return;
		}
		// ... and the flow on it is greater than its new capacity
		int delta = arc.flow - newCapacity;
		arc.flow = arc.capacity = newCapacity;
		objectiveValue.plusTimes(-delta, arc.cost);
		arc.source.supply -= delta;
		arc.target.supply += delta;
		if (animationDelay > 0)
			arc.setUIClass();
		changeSupply(arc.source, arc.source.supply + delta);
		changeSupply(arc.target, arc.target.supply - delta);
	}

	protected void addArc(NSArc arc) {
		arc.flow = 0;
		arc.status = ArcStatus.NONBASIC_LOWER;
		arcs.put(arc.id, arc);
		nonBasicArcs.add(arc);
		arc.computeReducedCost(work1);
		if (work1.isNegative())
			solutionStatus = SolutionStatus.UNDEFINED;
		if (animationDelay > 0)
			arc.setUIClass();
	}

	protected void removeArc(NSArc arc) {
		changeCapacity(arc, 0);
		if (arc.status == ArcStatus.BASIC) {
			NSNode node = arc.source.arcToParent == arc ? arc.source
					: arc.target;
			enteringArc = node.artificialArc;
			if (enteringArc.source == root)
				enteringArc.switchDirection();
			selectLeavingArc();
			fromSink = true;
			pivot();
			solutionStatus = SolutionStatus.UNDEFINED;
		}
		arcs.remove(arc.id);
		nonBasicArcs.remove(arc);
	}

	protected void addNode(NSNode node) {
		nodes.put(node.id, node);
		node.createArtificialArc();
		solutionStatus = SolutionStatus.UNDEFINED;
	}

	protected void removeNode(NSNode node) {
		node.previousInThread().thread = node.thread;
		NSArc artificial = node.arcToParent;
		objectiveValue.plusTimes(-artificial.flow, artificial.cost);
		root.supply += node.supply;
		nodes.remove(node.id);
		solutionStatus = SolutionStatus.UNDEFINED;
	}

	protected void clearGraph() {
		nodes.clear();
		arcs.clear();
		nonBasicArcs.clear();
		root.thread = root;
		root.supply = 0;
		objectiveValue.set(0);
		solutionStatus = SolutionStatus.OPTIMAL;
	}

	protected class NSNode {
		String id;
		int supply;
		BigMNumber potential;
		NSNode parent;
		NSNode thread;
		int depth;
		NSArc arcToParent;
		NSArc artificialArc;
		NSNode(Node node) {
			id = node.getId();
			double v = node.getNumber(supplyName);
			if (Double.isNaN(v))
				v = 0;
			supply = (int) v;
			potential = new BigMNumber();
		}
		NSNode() {
			potential = new BigMNumber();
		}
		void createArtificialArc() {
			artificialArc = new NSArc();
			artificialArc.id = id;
			artificialArc.capacity = INFINITE_CAPACITY;
			artificialArc.cost.set(0, 1);
			artificialArc.status = ArcStatus.BASIC;
			if (supply > 0) {
				artificialArc.source = this;
				artificialArc.target = root;
				artificialArc.flow = supply;
			} else {
				artificialArc.source = root;
				artificialArc.target = this;
				artificialArc.flow = -supply;
			}

			parent = root;
			thread = root.thread;
			root.thread = this;
			depth = 1;
			arcToParent = artificialArc;
			computePotential();

			root.supply -= supply;
			objectiveValue.plusTimes(artificialArc.flow, artificialArc.cost);

			if (animationDelay > 0)
				artificialArc.setUIClass();
		}

	
		NSNode previousInThread() {
			NSNode node;
			for (node = parent; node.thread != this; node = node.thread)
				;
			return node;
		}
		NSNode lastSuccessor() {
			NSNode node;
			for (node = this; node.thread.depth > depth; node = node.thread)
				;
			return node;
		}
		void computePotential() {
			potential.set(parent.potential);
			if (arcToParent.source == this)
				potential.plus(arcToParent.cost);
			else
				potential.minus(arcToParent.cost);
		}
		void changeParent(NSNode newParent, NSArc newArcToParent) {
			NSNode pred = previousInThread();
			NSNode succ = lastSuccessor();

			pred.thread = succ.thread;
			succ.thread = newParent.thread;
			newParent.thread = this;

			parent = newParent;
			arcToParent = newArcToParent;

			for (NSNode node = this; node != succ.thread; node = node.thread) {
				node.depth = node.parent.depth + 1;
				node.computePotential();
			}
		}
	}
	public static enum ArcStatus {
		
		BASIC,
		NONBASIC_LOWER,
		NONBASIC_UPPER;
	}
	protected class NSArc {
		String id;
		int capacity;
		BigMNumber cost;
		NSNode source;
		NSNode target;
		int flow;
		ArcStatus status;
		NSArc(Edge edge, boolean sameDirection) {
			if (edge.getId().startsWith(PREFIX))
				throw new IllegalArgumentException(
						"Edge ids must not start with " + PREFIX);
			id = (sameDirection ? "" : PREFIX + "REVERSE_") + edge.getId();

			double v = edge.getNumber(capacityName);
			if (Double.isNaN(v) || v < 0)
				v = INFINITE_CAPACITY;
			capacity = (int) v;

			v = edge.getNumber(costName);
			if (Double.isNaN(v))
				v = 1;
			cost = new BigMNumber((int) v);

			String sourceId = edge.getSourceNode().getId();
			String targetId = edge.getTargetNode().getId();
			source = nodes.get(sameDirection ? sourceId : targetId);
			target = nodes.get(sameDirection ? targetId : sourceId);
		}
		NSArc() {
			cost = new BigMNumber();
		}
		void computeReducedCost(BigMNumber reducedCost) {
			reducedCost.set(cost);
			reducedCost.minus(source.potential);
			reducedCost.plus(target.potential);
			if (status == ArcStatus.NONBASIC_UPPER)
				reducedCost.minus();
		}
		void computeAllowedFlowChange(NSNode first, BigMNumber flowChange) {
			if (first == source) {
				// the arc is in the direction of the cycle
				if (capacity == INFINITE_CAPACITY)
					flowChange.set(0, 1);
				else
					flowChange.set(capacity - flow);
			} else {
				flowChange.set(flow);
			}
		}

		void changeFlow(int delta, NSNode first) {
			if (first == source)
				flow += delta;
			else
				flow -= delta;
			if (animationDelay > 0)
				setUIClass();
		}
		NSNode getOpposite(NSNode node) {
			if (node == source)
				return target;
			if (node == target)
				return source;
			return null;
		}

		public boolean isArtificial() {
			return source == root || target == root;
		}
		String getOriginalId() {
			if (isArtificial())
				return null;
			if (id.startsWith(PREFIX + "REVERSE_"))
				return id.substring(PREFIX.length() + "REVERSE_".length());
			return id;
		}
		void setUIClass() {
			if (isArtificial()) {
				NSNode node = getOpposite(root);
				String uiClass = "trans";
				if (node.supply > 0)
					uiClass = "supply";
				else if (node.supply < 0)
					uiClass = "demand";
				uiClass += flow == 0 ? "_balanced" : "_unbalanced";
				Node x = graph.getNode(node.id);
				x.addAttribute("label", target == root ? flow : -flow);
				x.addAttribute("ui.class", uiClass);
			} else {
				String uiClass = "basic";
				if (status == ArcStatus.NONBASIC_LOWER)
					uiClass = "nonbasic_lower";
				else if (status == ArcStatus.NONBASIC_UPPER)
					uiClass = "nonbasic_upper";

				Edge e = graph.getEdge(getOriginalId());
				e.addAttribute("label", flow);
				e.addAttribute("ui.class", uiClass);
			}
		}
		void switchDirection() {
			NSNode tmp = source;
			source = target;
			target = tmp;
			flow = -flow;

			NSNode subtreeRoot = getOpposite(root);
			subtreeRoot.computePotential();
			for (NSNode node = subtreeRoot.thread; node.depth > subtreeRoot.depth; node = node.thread)
				node.computePotential();
		}
	}

	public void printBFS(PrintStream ps) {
		ps.println("=== Nodes ===");
		ps.printf("%20s%10s%10s%20s%20s%10s%n", "id", "supply", "potential",
				"parent", "thread", "depth");
		ps.printf("%20s%10d%10s%20s%20s%10d%n", root.id, root.supply,
				root.potential, "-", root.thread.id, root.depth);
		for (NSNode node : nodes.values())
			ps.printf("%20s%10d%10s%20s%20s%10d%n", node.id, node.supply,
					node.potential, node.parent.id, node.thread.id, node.depth);
		ps.println();

		ps.println("=== Arcs ===");
		ps.printf("%20s%10s%10s%10s%10s%20s%n", "id", "capacity", "cost",
				"flow", "r. cost", "status");
		for (NSArc a : arcs.values()) {
			a.computeReducedCost(work1);
			ps.printf("%20s%10s%10s%10s%10s%20s%n", a.id,
					a.capacity == INFINITE_CAPACITY ? "Inf" : a.capacity,
					a.cost, a.flow, work1, a.status);
		}

		for (NSNode node : nodes.values()) {
			NSArc a = node.artificialArc;
			a.computeReducedCost(work1);
			ps.printf("%20s%10s%10s%10s%10s%20s%n", a.id,
					a.capacity == INFINITE_CAPACITY ? "Inf" : a.capacity,
					a.cost, a.flow, work1, a.status);
		}

		ps.println();
		ps.printf("=== Objective value %s. Solution status %s ===%n%n",
				objectiveValue, solutionStatus);
	}
}
class BigMNumber {
	protected long small;
	protected long big;

	public BigMNumber() {
		set(0, 0);
	}
	
	public BigMNumber(long small) {
		set(small, 0);
	}

	public void set(long small, long big) {
		this.small = small;
		this.big = big;
	}

	public void set(BigMNumber b) {
		set(b.small, b.big);
	}
	
	public void set(long small) {
		set(small, 0);
	}

	public void plus(BigMNumber b) {
		small += b.small;
		big += b.big;
	}

	public void minus(BigMNumber b) {
		small -= b.small;
		big -= b.big;
	}

	public void minus() {
		small = -small;
		big = -big;
	}
	
	public void plusTimes(int multiplier, BigMNumber b) {
		small += multiplier * b.small;
		big += multiplier * b.big;
	}
	
	public boolean isNegative() {
		return big < 0 || (big == 0 && small < 0);
	}
	
	public boolean isInfinite() {
		return big != 0;
	}
	
	public int compareTo(BigMNumber b) {
		if (big < b.big)
			return -1;
		if (big > b.big)
			return 1;
		if (small < b.small)
			return -1;
		if (small > b.small)
			return 1;
		return 0;
	}
	
	public long getSmall() {
		return small;
	}
	
	@Override
	public String toString() {
		if (big == 0)
			return small + "";
		String r = "";
		if (small != 0) {
			r += small;
			if (big > 0)
				r += "+";
		}
		if (big == -1)
			r += "-";
		else if (big != 1)
			r+= big;
		r += "M";
		return r;
	}

}

