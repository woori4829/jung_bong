package graph;

import atomic.AtomicDoubleArray;

import java.util.function.DoubleBinaryOperator;

public class NodePartition {
    public static DoubleBinaryOperator updateFunction;
    public static void setUpdateFunction(DoubleBinaryOperator function) {
        updateFunction = function;
    }

    AtomicDoubleArray[] tables;
    final int numValuesPerNode;
    final int asyncRangeSize;

    int partitionId;
    int partitionSize;
    int tablePos;

    NodePartition(int partitionId, int maxNodeId, int partitionSize, int numValuesPerNode, int asyncRangeSize) {
        this.partitionId = partitionId;
        this.partitionSize = partitionSize;
        if ((partitionId + 1) * this.partitionSize > maxNodeId) {
            this.partitionSize = (maxNodeId % partitionSize) + 1;
        }
        this.numValuesPerNode = numValuesPerNode;
        this.asyncRangeSize = asyncRangeSize;

        initializeTable();
    }

    public final void initializeTable() {
        tables = new AtomicDoubleArray[numValuesPerNode];
        for (int i = 0; i < numValuesPerNode; i++) {
            tables[i] = new AtomicDoubleArray(partitionSize);
        }
    }

    public final void initializedCallback() {
        swapConsecutiveTwoTables();
    }

    public final void setVertexValue(int entry, double value) {
        tables[tablePos].asyncSet(entry, value);
    }

    public final void setNextVertexValue(int entry, double value) {
        tables[tablePos+1].asyncSet(entry, value);
    }

    public final double getVertexValue(int entry) {
        return tables[tablePos].asyncGet(entry);
    }

    public final void update(int entry, double value) {
        update(tablePos, entry, value);
    }

    public final void update(int pos, int entry, double value) {
        if (entry < asyncRangeSize) { // TODO : think about multiple ranges in a single partition
            tables[pos].asyncGetAndAccumulate(entry, value, updateFunction);
        } else {
            tables[pos].getAndAccumulate(entry, value, updateFunction);
        }
    }

    public final void updateNextTable(int entry, double value) {
        update(tablePos + 1, entry, value);
    }

    public final void swapConsecutiveTwoTables() {
        AtomicDoubleArray tmp = tables[tablePos];
        tables[tablePos] = tables[tablePos + 1];
        tables[tablePos + 1] = tmp;
    }

    public void reset() {
        initializeTable();
    }
    public int getSize() {
        return partitionSize;
    }
}

