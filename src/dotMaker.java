import java.util.ArrayList;
import java.util.List;

public class dotMaker {

    private static ArrayList<Node> nodesList = new ArrayList<>();
    private static List<String> shortestPath = new ArrayList<>();

    public dotMaker(ArrayList<Node> nodesList, List<String> shortestPath) {
        dotMaker.nodesList = nodesList;
        dotMaker.shortestPath = shortestPath;
    }

    // Generates the DOT language output
    // Online visualizer at https://dreampuf.github.io/GraphvizOnline
    public void generateDotOutput() {
        // Start DOT graph
        System.out.println("digraph G {");

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

        // Graph layout direction
        System.out.println("    rankdir=LR;");

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

        nodesList.removeLast();

        // --- EDGE DEFINITIONS ---
        for (Node sourceNode : nodesList) {
            String source = sourceNode.getLabel();

            for (Edge edge : sourceNode.getForwardEdges()) {
                String dest = edge.destination();
                long weight = edge.weight();

                // Check if this edge is part of the shortest path
                String edgeAttributes;
                if (edgeInShortestPath(source, dest)) {
                    edgeAttributes = "[label=\"" + weight + "\", color=\"#FFD700\", penwidth=3, fontcolor=\"#FFD700\"]";
                } else {
                    edgeAttributes = "[label=\"" + weight + "\"]";
                }

                System.out.println(" \"" + source + "\" -> \"" + dest + "\" " + edgeAttributes + ";");
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
