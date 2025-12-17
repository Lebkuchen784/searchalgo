import java.util.ArrayList;
import java.util.List;

public class dotMakerNeato {

    private static ArrayList<Node> nodesList = new ArrayList<>();
    private static List<String> shortestPath = new ArrayList<>();

    public dotMakerNeato(ArrayList<Node> nodesList, List<String> shortestPath) {
        dotMakerNeato.nodesList = nodesList;
        dotMakerNeato.shortestPath = shortestPath;
    }

    // Generates the DOT language output
    // Online visualizer at https://dreampuf.github.io/GraphvizOnline
    public void generateDotOutput() {
        // Start DOT graph
        System.out.println("digraph G {");

        // Use 'neato' for distance-based layout
        System.out.println("    layout=\"neato\";");
        System.out.println("    overlap=\"scale\";"); // Helps prevent overlaps
        System.out.println("    splines=true;"); // Curved edges avoiding nodes

        // --- GLOBAL GRAPH STYLE (colors, theme) ---
        System.out.println("    bgcolor=\"#181818\";");

        System.out.println("    node [");
        System.out.println("        fontcolor=\"#e6e6e6\",");
        System.out.println("        style=filled,");
        System.out.println("        color=\"#e6e6e6\",");
        System.out.println("        fillcolor=\"#333333\"");
        System.out.println("    ];");

        System.out.println("    edge [");
        System.out.println("        color=\"#e6e6e6\",");
        System.out.println("        fontcolor=\"#e6e6e6\"");
        System.out.println("    ];");

        // Removed rankdir=LR as it conflicts with neato's physical model
        // System.out.println(" rankdir=LR;");

        System.out.println();

        // --- NODE DEFINITIONS ---
        for (Node node : nodesList) {

            String label = node.getLabel();
            String attributes;

            if (label.equals("START")) {
                attributes = "[shape=doublecircle, style=filled, fillcolor=green]";
            } else if (label.equals("END")) {
                attributes = "[shape=doublecircle, style=filled, fillcolor=red]";
            } else if (shortestPath.contains(label)) {
                attributes = "[shape=circle, style=filled, fillcolor=\"#FFBF00\", fontcolor=\"#000000\"]";
            } else {
                attributes = "[shape=circle]"; // inherits dark theme from global node[]
            }

            System.out.println("    \"" + label + "\" " + attributes + ";");
        }

        System.out.println();

        if (!nodesList.isEmpty() && nodesList.getLast().getLabel().equals("END") && nodesList.size() > 1
                && nodesList.get(nodesList.size() - 2).getLabel().equals("END")) {
            // Handle potential duplicate removal if logic was flaky, but original
            // removeLast was simplistic.
            // Leaving it safe:
            nodesList.removeLast();
        } else if (!nodesList.isEmpty()) {
            nodesList.removeLast();
        }

        // --- EDGE DEFINITIONS ---
        for (Node sourceNode : nodesList) {

            String source = sourceNode.getLabel();
            for (Edge edge : sourceNode.getForwardEdges()) {
                String dest = edge.destination();
                long weight = edge.weight();

                // Check if this edge is part of the shortest path
                String colorAttributes;
                if (edgeInShortestPath(source, dest)) {
                    colorAttributes = "color=\"#FFD700\", penwidth=3, fontcolor=\"#FFD700\"";
                } else {
                    colorAttributes = "";
                }

                // Scaling for neato 'len' (default is inches, 1.0 is standard).
                // Weights 2-10 -> 1.0 - 5.0 inches roughly.
                double neatoLen = weight * 0.5;

                String edgeAttributes = String.format(
                        "[label=\"%d\", len=%.2f, weight=1, %s]",
                        weight, neatoLen, colorAttributes);

                System.out.println("    \"" + source + "\" -> \"" + dest + "\" " + edgeAttributes + ";");
            }
        }

        System.out.println("}");
    }

    private boolean edgeInShortestPath(String source, String dest) {
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            if (shortestPath.get(i).equals(source) && shortestPath.get(i + 1).equals(dest)) {
                return true;
            }
        }
        return false;
    }
}
