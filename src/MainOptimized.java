
import java.util.*;

public class MainOptimized {
    // Fixed seed for reproducibility during comparison, remove seed for random
    static Random rnd = new Random();
    public static int graphSize = 16;
    public static int lookoutWindow = 8;

    public static void main(String[] args) {
        // --- 1. EFFICIENT DATA GENERATION ---

        // Use primitive arrays for core logic (Speed++)
        // Index i maps to node at index i
        String[] labels = new String[graphSize];

        // Generate Labels On-the-Fly or Pre-calc
        // We need 26*26 = 676 unique, but graphSize is small usually.
        // Replicating original logic of A..Z loop but stopping at graphSize
        int labelIdx = 0;
        char[] charBuf = new char[2];
        List<String> tempLabels = new ArrayList<>(676);
        for (char c1 = 'A'; c1 <= 'Z'; c1++) {
            for (char c2 = 'A'; c2 <= 'Z'; c2++) {
                charBuf[0] = c1;
                charBuf[1] = c2;
                tempLabels.add(new String(charBuf));
            }
        }
        Collections.shuffle(tempLabels, rnd);

        // Assign Special Labels
        labels[0] = "START";
        for (int i = 1; i < graphSize - 1; i++) {
            labels[i] = tempLabels.get(i - 1);
        }
        labels[graphSize - 1] = "END";

        // --- 2. FAST SHORTEST PATH (DP on DAG) ---
        // Since nodes only connect I -> (I+1...I+K), it is a DAG sorted topologically.
        // We can solve in O(N * K) using Linear DP.

        long[] dist = new long[graphSize];
        int[] prev = new int[graphSize];
        // Store weights to reconstruct graph objects for dotMaker later
        // weights[i][j] stores weight from node i to node i+(j+1)
        long[][] edgeWeights = new long[graphSize][lookoutWindow];

        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[0] = 0;

        // Forward Pass: Generate Edges & Relax w/ DP
        for (int i = 0; i < graphSize - 1; i++) {
            // Logic match: loop j from 1 to lookoutWindow
            for (int k = 1; k <= lookoutWindow; k++) {
                int targetIdx = i + k;
                if (targetIdx >= graphSize)
                    break;

                // Copying logic: rnd.nextInt(100 - 20) + 20
                long weight = rnd.nextInt(8) + 2;

                // Store for later (Object reconstruction)
                edgeWeights[i][k - 1] = weight;

                // Relaxation
                if (dist[i] != Long.MAX_VALUE) {
                    long newDist = dist[i] + weight;
                    if (newDist < dist[targetIdx]) {
                        dist[targetIdx] = newDist;
                        prev[targetIdx] = i;
                    }
                }
            }
        }

        // --- 3. PATH RECONSTRUCTION (Primitive) ---

        // Backtrack
        // We need a list of labels for dotMaker
        // Using LinkedList for efficient prepend, or just reverse ArrayList
        List<String> shortestPathLabels = new ArrayList<>();
        int curr = graphSize - 1; // END node

        if (dist[curr] == Long.MAX_VALUE) {
            System.out.println("No path found to END.");
        } else {
            while (curr != -1) {
                shortestPathLabels.add(labels[curr]);
                curr = prev[curr];
            }
            Collections.reverse(shortestPathLabels);
        }

        // --- 4. LEGACY OBJECT CREATION (For dotMaker support) ---
        // The dotMaker expects ArrayList<Node> and the path list.
        // We reconstruct this ONLY for output, keeping the algo fast.

        ArrayList<Node> nodesList = new ArrayList<>(graphSize);
        // Create all Node objects
        for (String lbl : labels) {
            nodesList.add(new Node(lbl));
        }

        // Re-connect them using the stored weights
        for (int i = 0; i < graphSize - 1; i++) {
            Node src = nodesList.get(i);
            for (int k = 1; k <= lookoutWindow; k++) {
                int targetIdx = i + k;
                if (targetIdx >= graphSize)
                    break;

                long w = edgeWeights[i][k - 1];
                // Node.connect(label, weight)
                src.connect(nodesList.get(targetIdx).getLabel(), w);
            }
        }

        // --- 5. VISUALIZATION ---

        dotMakerNeato DOT = new dotMakerNeato(nodesList, shortestPathLabels);
        DOT.generateDotOutput();
    }
}
