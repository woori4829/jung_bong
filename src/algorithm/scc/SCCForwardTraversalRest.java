package algorithm.scc;

import graph.DirectedGraph;
import graph.GraphAlgorithmInterface;
import graph.Node;
import graph.partition.IntegerGraphPartition;
import graph.partition.IntegerNodePartition;

public class SCCForwardTraversalRest implements GraphAlgorithmInterface {
    DirectedGraph graph;
    IntegerGraphPartition graphPartition;
    boolean[] isInActive;

    public SCCForwardTraversalRest(DirectedGraph<IntegerGraphPartition> graph, boolean[] isInActive) {
        this.graph = graph;
        this.isInActive = isInActive;
        graphPartition = graph.getPartitionInstance();
    }

    @Override
    public void execute(int partitionId) {
        int partitionSize = graphPartition.getPartition(partitionId).getSize();
        int offset = partitionId * partitionSize;
        IntegerNodePartition partition = graphPartition.getPartition(partitionId);

        for (int i = 0; i < partitionSize; i++) {
            int nodeId = offset + i;
            int colorId = partition.getVertexValue(i);

            if (!isInActive[nodeId]) {
                Node node = graph.getNode(nodeId);

                if (node != null) {
                    int neighborListSize = node.neighborListSize();

                    for (int j = 0; j < neighborListSize; j++) {
                        int neighborId = node.getNeighbor(j);

                        if (!isInActive[neighborId]) {
                            int nodeIdPositionInPart = graphPartition.getNodePositionInPart(neighborId);
                            partition.update(nodeIdPositionInPart, colorId);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reset(int partitionId) {

    }
}