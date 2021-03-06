package algorithm.pagerank;

import graph.DirectedGraph;
import graph.NodePartition;

public class PageRankInit extends PageRank {
    double initialValue;
    double stopSurfValue;
    boolean isFirst;

    PageRankInit(DirectedGraph graph, double dampingFactor) {
        super(graph, dampingFactor);
        initialValue = getInitPageRankValue(0); //initial PageRank Value
        stopSurfValue = getInitPageRankValue(dampingFactor);
        isFirst = true;
    }

    @Override
    public void execute(int partitionId) {
        if (!isFirst) {
            initialValue = stopSurfValue;
        }
        else {
            initNextTable(partitionId);
        }

        NodePartition partition = graphPartition.getPartition(partitionId);
        int partitionSize = partition.getSize();

        for (int i = 0; i < partitionSize; i++) {
            int nodeId = graphPartition.getNodeNumberInPart(partitionId, i);
            srcNode = graph.getNode(nodeId);

            if (srcNode != null) {
                partition.setVertexValue(i, initialValue);
            }
        }

        if (!isFirst) {
            partition.initializedCallback();
        }
        isFirst = false;
    }

    public void initNextTable(int partitionId) {
        NodePartition partition = graphPartition.getPartition(partitionId);
        int partitionSize = partition.getSize();

        for (int i = 0; i < partitionSize; i++) {
            int nodeId = graphPartition.getNodeNumberInPart(partitionId, i);
            srcNode = graph.getNode(nodeId);

            if (srcNode != null) {
                partition.setNextVertexValue(i, stopSurfValue);
            }
        }
    }

    public double getInitPageRankValue(double dampingFactor) {
        return (1 - dampingFactor) / (double) graph.getNumNodes();
    }

    public void reset(int taskId) {
        isFirst = true;
        initialValue = getInitPageRankValue(0);
        partition.reset();
    }
}
