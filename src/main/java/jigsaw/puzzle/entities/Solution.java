package jigsaw.puzzle.entities;

public class Solution {
    private int rows;
    private int[] solutionPieces;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int[] getSolutionPieces() {
        return solutionPieces;
    }

    public void setSolutionPieces(int[] solutionPieces) {
        this.solutionPieces = solutionPieces;
    }

    public int[] toArray() {
        int[] retVal = new int[solutionPieces.length + 1];
        retVal[0] = rows;
        System.arraycopy(solutionPieces, 0, retVal, 1, solutionPieces.length);
        return retVal;
    }
}
