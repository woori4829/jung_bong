package graph;

public class DirectedGraph extends Graph {
    static DirectedGraph instance = null;

    public static DirectedGraph getInstance(int expOfPartitionSize) {
        if (instance == null) {
            instance = new DirectedGraph(expOfPartitionSize);
        }
        return instance;
    }

    public DirectedGraph(int expOfPartitionSize) {
        super(expOfPartitionSize);
    }

    public boolean addEdge(int srcNodeId, int destNodeId) {
        checkAndCreateNodes(srcNodeId, destNodeId);

        Node srcNode = nodes[srcNodeId];
        Node destNode = nodes[destNodeId];

        boolean isAdded = srcNode.addNeighborId(destNodeId); // Do not allow duplication

        if (isAdded) {
            srcNode.incrementOutDegree();
            destNode.incrementInDegree();
            numEdges++;
        }

        return isAdded;
    }
}

