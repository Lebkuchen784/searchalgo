import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static Random rnd = new Random();

    static void main(String[] args) {
        // --- SETUP & NODE GENERATION ---
        // -------------------------------
        // -------------------------------
        // -------------------------------

        int graphSize = Integer.parseInt(args[0]);
        int lookoutWindow;

        // Generate and save the labels
        Labels labels = new Labels();
        List<String> labelsArrayList = labels.getUnshuffledLabels();

        Collections.shuffle(labelsArrayList);

        ArrayList<Node> nodesList = new ArrayList<>(graphSize);

        Node startNode = new Node("START");
        nodesList.add(startNode);
        for (int i = 0; i < graphSize - 2; ++i) {
            nodesList.add(new Node(labelsArrayList.get(i)));
        }
        Node endNode = new Node("END");
        nodesList.add(endNode);

        // ---- CONNECTING THE NODES -----
        // -------------------------------
        // -------------------------------
        // -------------------------------

        Set<Integer> targets = new HashSet<>();
        for (int i = 0; i < nodesList.size() - 1; ++i) {
            targets.clear();
            // Generates a bound randomized lookout window for every node instead of a fixed number for all
            lookoutWindow = rnd.nextInt(graphSize / 4) + 1;
            for (int j = 1; j <= lookoutWindow; ++j) {
                // <= lookoutWindow and not < lookoutWindow because when the lookout window is exactly 1,
                // the graph becomes a one element chain and targets.add() right below does not get called
                // since j starts at 1
                if (i + j < nodesList.size())
                    targets.add(i + j);
            }

            if (targets.isEmpty()) throw new RuntimeException("The targets set is somehow empty again (not 'END').");

            for (int target : targets) {
                nodesList.get(i).connect(nodesList.get(target).getLabel(), rnd.nextInt(100 - 20) + 20);
            }
        }

        // -------- SHORTEST PATH --------
        // -----------not really----------
        // -------------------------------
        // -------------------------------

        // Stupid
        /*
        StringBuilder path = new StringBuilder("Shortest path (probably not): ");
        String nextNodeLabel = "START";
        do {
            int targetNodePosition = 0;
            if (!nextNodeLabel.equals("START")) {
                String finalNextNodeLabel = nextNodeLabel;
                List<Node> targetNodes = nodesList
                        .stream()
                        .filter(n -> n.getLabel().contains(finalNextNodeLabel))
                        .toList();
                targetNodePosition = nodesList.indexOf(targetNodes.getFirst());
            }
            HashSet<Edge> edges = nodesList.get(targetNodePosition).getForwardEdges();
            Edge[] edgesArray = edges.toArray(new Edge[0]);
            Edge minWeight = edgesArray[0];
            for (int j = 1; j < edges.size(); ++j) {
                if (edgesArray[j].weight() < minWeight.weight()) {
                    minWeight = edgesArray[j];
                }
            }
            path.append(nextNodeLabel).append(":").append(minWeight.weight()).append("->");
            nextNodeLabel = minWeight.destination();
        } while (!nextNodeLabel.equals("END"));
        path.append("END");
        System.out.println(path);
        */

        // -------- SHORTEST PATH --------
        // ------------DIJKSTRA-----------
        // -------------------------------
        // -------------------------------

        // Holds the shortest distance for each node to the START node
        Map<Node, Long> distanceMap = new HashMap<>();

        // Converting the nodesList into a Map for a faster lookup
        Map<String, Node> nodesMap =
                nodesList.stream()
                        .collect(Collectors.toMap(
                                Node::getLabel,
                                node -> node
                        ));

        // A set for all visited nodes to check against
        Set<Node> visited = new HashSet<>();

        // Some arbitrary value to denote infinity
        long infDistance = 999999999;

        // Initialize the map with the infinite values, add the start node
        // with distance value 0 (long)
        distanceMap.put(startNode, 0L);

        for (int i = 1; i < nodesList.size(); ++i) {
            distanceMap.put(nodesList.get(i), infDistance);
        }

        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingLong(distanceMap::get));

        priorityQueue.offer(startNode);

        // Benchmark start
        long startTime = System.nanoTime();

        // Dijkstra
        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            if (visited.contains(currentNode) || currentNode.getLabel().equals("END")) {
                continue;
            }
            visited.add(currentNode);

            for (Edge edge : currentNode.getForwardEdges()) {
                Node connectedNode = nodesMap.get(edge.destination());

                if (!visited.contains(connectedNode)) {
                    long newDistance = distanceMap.get(currentNode) + edge.weight();

                    if (newDistance < distanceMap.get(connectedNode)) {
                        distanceMap.put(connectedNode, newDistance);
                        connectedNode.setPrevious(currentNode);
                        priorityQueue.offer(connectedNode);
                    }
                }
            }
        }

        // Benchmark end
        long endTime = System.nanoTime();

        Node destination = nodesList.getLast();

        List<String> shortestPath = new ArrayList<>();
        Node current = destination;

        while (!current.getLabel().equals("START")) {
            shortestPath.add(current.getLabel());
            current = current.getPrevious();
        }
        shortestPath.add("START");

        Collections.reverse(shortestPath);

        System.out.print("Shortest path: ");
        for (int i = 0; i < shortestPath.size(); ++i) {
            System.out.print(shortestPath.get(i));
            if (i < shortestPath.size() - 1) {
                System.out.print("->");
            }
        }
        System.out.println();
        System.out.println("Finding the shortest path took: " + (endTime - startTime) / 1_000_000.0 + " ms.");

        // dotMaker DOT = new dotMaker(nodesList, shortestPath);
        // DOT.generateDotOutput();
    }
}