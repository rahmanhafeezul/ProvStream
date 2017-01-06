
package provenancestream;

/**
 *
 * @author HafeezulRahman
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

public class ShortestPathAlgo extends NetworkFlow {
	protected String sourceId;

	public ShortestPathAlgo(String costName) {
		super(null, null, costName);
	}

	public String getSource() {
		return sourceId;
	}

	public void setSource(String sourceId) {
		this.sourceId = sourceId;
		if (nodes != null)
			for (NSNode node : nodes.values())
				changeSupply(node, node.id.equals(sourceId) ? nodes.size() - 1
						: -1);
	}

	@Override
	protected void cloneGraph() {
		super.cloneGraph();
		for (NSNode node : nodes.values())
			node.supply = node.id.equals(sourceId) ? nodes.size() - 1 : -1;
	}
	
	protected void bfsFromDijkstra() {
		// see if Dijkstra can be applied
		if (nodes.get(sourceId) == null)
			return;
		for (NSArc arc : arcs.values())
			if (arc.cost.isNegative())
				return;
		
		// compute Dijkstra
		Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, costName);
		dijkstra.init(graph);
		dijkstra.setSource(graph.getNode(sourceId));
		dijkstra.compute();
		
		Map<NSNode, NSNode> last = new HashMap<NSNode, NSNode>(4 * (nodes.size() + 1) / 3 + 1);
		last.put(root, root);
		root.thread = root;
		for (NSNode node : nodes.values()) {
			last.put(node, node);
			node.artificialArc.status = ArcStatus.NONBASIC_LOWER;
			node.artificialArc.flow = 0;
			node.thread = node;
		}
		
		// restore parent and thread
		for (NSNode node : nodes.values()) {
			Node gNode = graph.getNode(node.id);
			Node gParent = dijkstra.getParent(gNode);
			NSNode parent = gParent == null ? root : nodes.get(gParent.getId());
			node.parent = parent;
			NSArc arc = node.artificialArc;
			if (gParent != null) {
				Edge gEdge = dijkstra.getEdgeFromParent(gNode);
				if (gEdge.getSourceNode() == gParent)
					arc = arcs.get(gEdge.getId());
				else
					arc = arcs.get(PREFIX + "REVERSE_" + gEdge.getId());
			}
			node.arcToParent = arc;
			arc.status = ArcStatus.BASIC;
			nonBasicArcs.remove(arc);
			NSNode nodeLast = last.get(node);
			nodeLast.thread = parent.thread;
			parent.thread = node;
			for (NSNode x = parent; last.get(x) == parent; x = x.parent)
				last.put(x, nodeLast);
		}
		last.clear();
		dijkstra.clear();
		
		// compute depths, potentials, flows and objective value
		for (NSNode node = root.thread; node != root; node = node.thread) {
			node.depth = node.parent.depth + 1;
			node.computePotential();
			for (NSNode x = node; x != root; x = x.parent)
				x.arcToParent.flow++;
		}
		NSArc arc = nodes.get(sourceId).arcToParent;
		arc.flow = nodes.size() - arc.flow;
		
		objectiveValue.set(0);
		for (NSNode node = root.thread; node != root; node = node.thread)
			objectiveValue.plusTimes(node.arcToParent.flow, node.arcToParent.cost);
	}
	
	
	
	@Override
	public void init(Graph graph) {
		this.graph = graph;
		cloneGraph();
		createInitialBFS();
		bfsFromDijkstra();
		graph.addSink(this);		
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		NSNode node = new NSNode(graph.getNode(nodeId));
		if (nodeId.equals(this.sourceId)) {
			node.supply = nodes.size();
			addNode(node);
		} else {
			node.supply = -1;
			addNode(node);
			NSNode source = nodes.get(this.sourceId);
			if (source != null)
				changeSupply(source, nodes.size() - 1);
		}
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		removeNode(nodes.get(nodeId));
		if (!nodeId.equals(this.sourceId)) {
			NSNode source = nodes.get(this.sourceId);
			if (source != null)
				changeSupply(source, nodes.size() - 1);
		}
	}


	protected class NodeIterator<T extends Node> implements Iterator<T> {
		protected NSNode nextNode;

		protected NodeIterator(NSNode target) {
			if (target.id.equals(sourceId) || target.parent != root)
				nextNode = target;
			else
				nextNode = root;
		}

                @Override
		public boolean hasNext() {
			return nextNode != root;
		}

                @Override
		public T next() {
			if (nextNode == root)
				throw new NoSuchElementException();
			T node = graph.getNode(nextNode.id);
			nextNode = nextNode.parent;
			return node;
		}

                @Override
		public void remove() {
			throw new UnsupportedOperationException(
					"This iterator does not support remove");
		}
	}

	protected class EdgeIterator<T extends Edge> implements Iterator<T> {
		protected NSNode nextNode;

		protected EdgeIterator(NSNode target) {
			nextNode = target;
		}

                @Override
		public boolean hasNext() {
			return nextNode.parent != root;
		}

                @Override
		public T next() {
			if (nextNode.parent == root)
				throw new NoSuchElementException();
			T edge = graph.getEdge(nextNode.arcToParent.getOriginalId());
			nextNode = nextNode.parent;
			return edge;
		}

                @Override
		public void remove() {
			throw new UnsupportedOperationException(
					"This iterator does not support remove");
		}
	}

	public long getPathLength(Node node) {
		NSNode nsNode = nodes.get(node.getId());
		if (nsNode.id.equals(sourceId))
			return 0;
		if (nsNode.parent == root)
			return Long.MAX_VALUE;
		return -nsNode.potential.small;
	}

	
	public <T extends Node> Iterator<T> getPathNodesIterator(Node target) {
		return new NodeIterator<T>(nodes.get(target.getId()));
	}

	
	public <T extends Node> Iterable<T> getPathNodes(final Node target) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getPathNodesIterator(target);
			}
		};
	}

	
	public <T extends Edge> Iterator<T> getPathEdgesIterator(Node target) {
		return new EdgeIterator<T>(nodes.get(target.getId()));
	}

	
	public <T extends Edge> Iterable<T> getPathEdges(final Node target) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getPathEdgesIterator(target);
			}

		};
	}

	
	public Path getPath(Node target) {
		Path path = new Path();
		if (getPathLength(target) == Long.MAX_VALUE)
			return path;
		Stack<Edge> stack = new Stack<Edge>();
		for (Edge e : getPathEdges(target))
			stack.push(e);
		path.setRoot(graph.getNode(sourceId));
		while (!stack.isEmpty())
			path.add(stack.pop());
		return path;
	}
}
