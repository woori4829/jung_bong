package graph.partition;

import atomic.AtomicDoubleArray;

import java.util.function.DoubleBinaryOperator;

public class DoubleNodePartition extends NodePartition {
    public static DoubleBinaryOperator updateFunction;

    public static void setUpdateFunction(DoubleBinaryOperator function) {
        updateFunction = function;
    }

    AtomicDoubleArray[] tables;

    DoubleNodePartition(int partitionId, int maxNodeId, int partitionSize, int numValuesPerNode, int asyncRangeSize) {
        super(partitionId, maxNodeId, partitionSize, numValuesPerNode, asyncRangeSize);
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
        tables[tablePos + 1].asyncSet(entry, value);
    }

    public final double getVertexValue(int entry) {
        return tables[tablePos].asyncGet(entry);
    }

    public final void update(int entry, double value) {
        if(level != (byte) value) {
            level = (byte) value;
        }

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
        level = 1;
    }

    public int getSize() {
        return partitionSize;
    }

    public byte getLevel() {
        return level;
    }
}