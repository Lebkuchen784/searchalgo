import java.util.HashSet;

public class Node {
    private final String label;
    private Node previous;
    private final HashSet<Edge> forwardEdges;

    public Node(String label) {
        this.label = label;
        this.previous = null;
        this.forwardEdges = new HashSet<>();
    }

    public void connect(String connectionPair, long weight) {
        Edge temp = new Edge(connectionPair, weight);
        this.forwardEdges.add(temp);
    }

    // Getter
    public String getLabel() {
        return this.label;
    }

    // Getter
    public HashSet<Edge> getForwardEdges() {
        if (this.forwardEdges.isEmpty()) {
            System.out.println("Forward edges set was empty for node " + this.label);
        }
        return forwardEdges;
    }

    // Set the previous node from the shortest path
    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Node getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        if (label.isEmpty()) return null; // Should be unreachable
        StringBuilder output = new StringBuilder("Node " + this.label + " is connected to: ");
        if (forwardEdges.isEmpty()) {
            output.append("nothing.");
        } else {
            for (Edge edge : forwardEdges) {
                output.append(edge.destination()).append(" ");
            }
            output.append("with weights: ");
            for (Edge edge : forwardEdges) {
                output.append(edge.weight()).append(" ");
            }
        }
        if (previous != null) {
            output.append(" The previous node in the shortest path is ").append(this.previous);
        } else {
            output.append(" This node has no previous nodes after the shortest path algorithm has been ran.");
        }
        return output.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) return false;
        return this.label.equals(((Node) other).getLabel());
    }
}