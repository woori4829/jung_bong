package algorithm.bfs;

import graph.DirectedGraph;
import graph.GraphAlgorithmInterface;
import graph.Node;
import graph.partition.IntegerPartition;

public class BFSExecutor implements GraphAlgorithmInterface {

    DirectedGraph<IntegerPartition> graph;
    IntegerPartition partition;
    static int currentLevel;

    BFSExecutor(DirectedGraph<IntegerPartition> graph) {
        this.graph = graph;
    }

    public static void updateLevel() {
        currentLevel++;
    }

    public static void setLevel(int level) {
        currentLevel = level;
    }

    public static int getLevel() {
        return currentLevel;
    }

    @Override
    public void execute(int partitionId) {
        partition = graph.getPartition(partitionId);
        int partitionSize = partition.getSize();
        int expOfPartitionSize = graph.getExpOfPartitionSize();
        int offset = partitionId << expOfPartitionSize;

        for (int i = 0; i < partitionSize; i++) {
            int nodeId = offset + i;
            int nodePositionInPart = graph.getNodePositionInPart(nodeId);

            if (partition.getVertexValue(nodePositionInPart) == currentLevel) {
                Node srcNode = graph.getNode(nodeId);

                if (srcNode != null) {
                    update(srcNode);
                }
            }
        }
    }

    public void update(Node srcNode) {
        int neighborListSize = srcNode.neighborListSize();

        for (int j = 0; j < neighborListSize; j++) {
            int destId = srcNode.getNeighbor(j);
            int destPartitionId = graph.getPartitionId(destId);
            IntegerPartition destPartition = graph.getPartition(destPartitionId);
            int destPosition = graph.getNodePositionInPart(destId);
            double destLevel = destPartition.getVertexValue(destPosition);    //vertexValue is level

            if (destLevel == 0) {
                int updateLevel = currentLevel + 1;
                destPartition.update(destPosition, updateLevel);
                destPartition.setPartitionActiveValue((byte) (updateLevel));
            }
        }
    }

    @Override
    public void reset(int taskId) {

    }
}