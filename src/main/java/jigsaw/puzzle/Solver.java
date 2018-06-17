package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Solver {
    private int[] size;
    private Piece[][] board;
    private Set<Piece> pieces;
    private Report report;
    private int[] lastPosition;
    private boolean[] puzzleUsingStatistic;
    private int requiredStraightRight;
    private int requiredStraightBottom;
    private int leftStraightRight = 0;
    private int leftStraightBottom = 0;
    private int leftLeftBottomCorners = 0;
    private int leftRightBottomCorners = 0;

    private static final int X = 0;
    private static final int Y = 1;


    Solver(Report report, Set<Piece> pieces) {
        this.pieces = pieces;
        this.report = report;
        puzzleUsingStatistic = new boolean[pieces.size()];
    }

    boolean findSolution(int[] boardSize) {
        countLeftStraightsAndCorners();
        board = new Piece[boardSize[Y]][boardSize[X]];
        size = boardSize;
        requiredStraightRight = size[Y];
        requiredStraightBottom = size[X];
        lastPosition = new int[]{size[X] - 1, size[Y] - 1};
        return isNextFound(new int[]{-1,0});
    }

    private boolean isNextFound(int[] currentPos) {
        if (Arrays.equals(currentPos, lastPosition)) {
            reportSolution();
            return true;
        }
        Set<String> localIndex = new HashSet<>();
        currentPos = moveForward(currentPos);
        if (!isLeftPiecesCheckPassed(currentPos)) {
            return false;
        }
        for (Piece piece : pieces) {
            int pieceId = piece.getId();
            int[] pieceEdges = piece.getEdges();
            if (localIndex.contains(Arrays.toString(pieceEdges)) || isUsed(pieceId)) {
                continue;
            }
            if (isMatchPiece(pieceEdges, currentPos)) {
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

    private boolean isLeftPiecesCheckPassed(int[] currentPos) {
        boolean isLastRowNotFirstColumn = currentPos[Y] == lastPosition[Y] && currentPos[X] > 0;
        return leftRightBottomCorners > 0 &&
                (((!isLastRowNotFirstColumn) && leftLeftBottomCorners > 0) || isLastRowNotFirstColumn) &&
                leftStraightBottom >= requiredStraightBottom &&
                leftStraightRight >= requiredStraightRight;

    }

    private boolean isUsed(int id) {
        return puzzleUsingStatistic[id - 1];
    }

    private void setUsed(Piece piece, boolean isUsed) {
        puzzleUsingStatistic[piece.getId() - 1] = isUsed;
        int right = piece.getRight();
        int bottom = piece.getBottom();
        int left = piece.getLeft();
        if (right == 0) {
            leftStraightRight = incrementOrDecrement(isUsed, leftStraightRight);
        }
        if (bottom == 0) {
            leftStraightBottom = incrementOrDecrement(isUsed, leftStraightBottom);
            if (right == 0) {
                leftRightBottomCorners = incrementOrDecrement(isUsed, leftRightBottomCorners);
            }
            if (left == 0) {
                leftLeftBottomCorners = incrementOrDecrement(isUsed, leftLeftBottomCorners);
            }
        }
    }

    private int incrementOrDecrement(boolean isUsed, int value) {
        if (isUsed) {
            value--;
        } else {
            value++;
        }
        return value;
    }

    private void reportSolution() {
        // First int is number of rows, other ints are IDs in the solution order
        int[] solution = new int[size[X]*size[Y] + 1];
        solution[0] = size[Y];
        int k = 1;
        for (Piece[] aBoard : board) {
            for (int j = 0; j < board[0].length; j++) {
                solution[k++] = aBoard[j].getId();
            }
        }
        report.setSolution(solution);
    }

    private int[] moveForward(int[] currentPos) {
        int[] newPos;
        if (currentPos[X] < size[X] - 1) {
            newPos = new int[]{currentPos[X] + 1, currentPos[Y]};
        } else {
            newPos = new int[]{0, currentPos[Y] + 1};

        }
        recalculateRequiredStraightLines(newPos);
        return newPos;
    }

    private void recalculateRequiredStraightLines(int[] currentPos) {
        requiredStraightRight = lastPosition[Y] + 1 - currentPos[Y];
        requiredStraightBottom = lastPosition[X] + 1;
        if (requiredStraightRight == 1) {
            requiredStraightBottom -= currentPos[X];
        }
    }

    private boolean isMatchPiece(int[] edges, int[] currentPos) {
        int left;
        int top;
        boolean isRightZero = false;
        boolean isBottomZero = false;
        int zero = 0;

        if(currentPos[X] != 0) {
            left = board[currentPos[Y]][currentPos[X] - 1].getRight();
        } else {
            left = zero;
        }

        if(currentPos[X] == size[X] - 1) {
            isRightZero = true;
        }

        if(currentPos[Y] != 0) {
            top = board[currentPos[Y] - 1][currentPos[X]].getBottom();
        } else {
            top = zero;
        }
        if(currentPos[Y] == size[Y] - 1) {
            isBottomZero = true;
        }
        return (left + edges[0] == 0) &&
                (top + edges[1] == 0) &&
                (!isRightZero || edges[2] == 0) &&
                (!isBottomZero || edges[3] == 0);
    }

    private void countLeftStraightsAndCorners() {
        pieces.forEach(piece -> {
            int right = piece.getRight();
            int bottom = piece.getBottom();
            int left = piece.getLeft();
            if (right == 0) {
                leftStraightRight++;
            }
            if (bottom == 0) {
                leftStraightBottom++;
                if (left == 0) {
                    leftLeftBottomCorners++;
                }
                if (right == 0) {
                    leftRightBottomCorners++;
                }
            }
        });
    }
}
