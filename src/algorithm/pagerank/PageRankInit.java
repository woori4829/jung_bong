package algorithm.pagerank;

import graph.Graph;
import graph.GraphAlgorithmInterface;
import graph.Node;
import graph.partition.PageRankPartition;

public class PageRankInit implements GraphAlgorithmInterface
{
    Graph<PageRankPartition> graph;
    PageRankPartition partition;
    Node srcNode;

    int partitionId;
    int partitionSize;
    double dampingFactor;
    double initialValue;
    double stopSurfValue;
    boolean isFirst;

    PageRankInit(int partitionId, Graph<PageRankPartition> graph, double dampingFactor) {
        this.partitionId = partitionId;
        this.graph = graph;
        this.dampingFactor = dampingFactor;

        partition = graph.getPartition(partitionId);
        partitionSize = partition.getSize();
        initialValue = getInitPageRankValue(0); //initial PageRank Value
        stopSurfValue = getInitPageRankValue(dampingFactor);
        isFirst = true;
    }

    @Override
    public void execute() {
        if (!isFirst) {
            initialValue = stopSurfValue;
        }
        else {
            initNextTable();
        }

        partition = graph.getPartition(partitionId);
        int partitionSize = partition.getSize();

        for (int i = 0; i < partitionSize; i++) {
            int nodeId = graph.getNodeNumberInPart(partitionId, i);
            srcNode = graph.getNode(nodeId);

            partition.setVertexValue(i, initialValue);
        }

        if (!isFirst) {
            partition.initializedCallback();
        }
        isFirst = false;
    }

    public void initNextTable() {
        for (int i = 0; i < partitionSize; i++) {
            int nodeId = graph.getNodeNumberInPart(partitionId, i);
            srcNode = graph.getNode(nodeId);
            partition.setNextVertexValue(i, stopSurfValue);
        }
    }

    public double getInitPageRankValue(double dampingFactor) {
        return (1 - dampingFactor) / (double) graph.getNumNodes();
    }

    public void reset() {
        isFirst = true;
        initialValue = getInitPageRankValue(0);
        if (partition != null) {
            partition.reset();
        }
    }
}
