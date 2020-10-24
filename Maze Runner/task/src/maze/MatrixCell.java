package maze;

public class MatrixCell {
    private final int rowIndex;
    private final int columnIndex;

    public MatrixCell(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
}
