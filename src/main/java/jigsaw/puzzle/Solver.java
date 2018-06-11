package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.util.*;

class Solver {
    private int[] size;
    private Piece[][] board;
    private List<Piece> pieces;
    private Report report;
    private int[] lastPosition;
    private boolean[] usedPieces;

    private static final int X = 0;
    private static final int Y = 1;


    Solver(Report report, Set<Piece> pieces) {
        this.pieces = new ArrayList<>(pieces);
        Collections.shuffle(this.pieces);
        this.report = report;
        usedPieces = new boolean[pieces.size() + 1];
    }

    void findSolution(int[] boardSize) {
        board = new Piece[boardSize[Y]][boardSize[X]];
        size = boardSize;
        lastPosition = new int[]{size[X] - 1, size[Y] - 1};
        isNextFound(new int[]{-1,0});
    }

    private boolean isNextFound(int[] currentPos) {
        Set<String> localIndex = new HashSet<>();
        if (Arrays.equals(currentPos, lastPosition)) {
            reportSolution();
            return true;
        }
        currentPos = moveForward(currentPos);
        for (Piece piece : pieces) {
            if (localIndex.contains(Arrays.toString(piece.getEdges())) || isUsed(piece)) {
                continue;
            }
            if (isMatchWithNeighbors(piece, currentPos)) {
                board[currentPos[Y]][currentPos[X]] = piece;
                setUsed(piece, true);
                if (isNextFound(currentPos)) {
                    return true;
                } else {
                    setUsed(piece, false);
                    board[currentPos[Y]][currentPos[X]] = null;
                }
            }
            localIndex.add(Arrays.toString(piece.getEdges()));
        }
        return false;
    }

    private void setUsed(Piece piece, boolean isUsed) {
        usedPieces[piece.getId()] = isUsed;
    }


    private boolean isUsed(Piece piece) {
        return usedPieces[piece.getId()];
    }

    private void reportSolution() {
        // First int is number of columns, other ints are IDs in the solution order
        int[] solution = new int[size[X]*size[Y] + 1];
        solution[0] = size[X];
        int k = 1;
        for (Piece[] aBoard : board) {
            for (int j = 0; j < board[0].length; j++) {
                solution[k++] = aBoard[j].getId();
            }
        }
        report.setSolution(solution);
    }

    private int[] moveForward(int[] currentPos) {
        if (currentPos[X] < size[X] - 1) {
            return new int[]{currentPos[X] + 1, currentPos[Y]};
        } else {
            return new int[]{0, currentPos[Y] + 1};
        }
    }

    private boolean isMatchWithNeighbors(Piece piece, int[] currentPos) {
        Piece left;
        Piece top;
        Piece right;
        Piece bottom;
        Piece zero = new Piece(1, new int[]{0,0,0,0});

        if(currentPos[X] != 0) {
            left = board[currentPos[Y]][currentPos[X] - 1];
        } else {
            left = zero;
        }

        if(currentPos[X] != size[X] - 1) {
            right = board[currentPos[Y]][currentPos[X] + 1];
        } else {
            right = zero;
        }
        if(currentPos[Y] != 0) {
            top = board[currentPos[Y] - 1][currentPos[X]];
        } else {
            top = zero;
        }
        if(currentPos[Y] != size[Y] - 1) {
            bottom = board[currentPos[Y] + 1][currentPos[X]];
        } else {
            bottom = zero;
        }
        return (left.getRight() + piece.getLeft() == 0) &&
                (top.getBottom() + piece.getTop() == 0) &&
                (right == null || right.getLeft() + piece.getRight() == 0) &&
                (bottom == null || bottom.getTop() + piece.getBottom() == 0);
    }

}
