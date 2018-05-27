package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;

import java.util.Arrays;
import java.util.Set;

class Solver {
    private int[] size;
    private Piece[][] board;
    private Set<Piece> pieces;

    private static final int X = 0;
    private static final int Y = 1;


    Solver(Set<Piece> pieces, int[] boardSize) {
        this.pieces = pieces;
        board = new Piece[boardSize[Y]][boardSize[X]];
        size = boardSize;
    }

    String findSolution() {
        if (isNextFound(new int[]{-1,0})) {
            return solutionString();
        } else {
            return "";
        }
    }

    private boolean isNextFound(int[] currentPos) {
        if (Arrays.equals(currentPos, new int[]{size[X] - 1, size[Y] - 1})) {
            return true;
        }
        currentPos = moveForward(currentPos);
        for (Piece piece : pieces) {
            if ((!piece.isUsed()) && isMatchPiece(piece, currentPos)) {
//                board[currentPos[X]][currentPos[Y]] = piece;
                board[currentPos[Y]][currentPos[X]] = piece;
                piece.setUsed(true);
                if (isNextFound(currentPos)) {
                    return true;
                } else {
                    piece.setUsed(false);
                }
            }
        }
        return false;
    }

    private int[] moveForward(int[] currentPos) {
        if (currentPos[X] < size[X] - 1) {
            return new int[]{currentPos[X] + 1, currentPos[Y]};
        } else {
            return new int[]{0, currentPos[Y] + 1};
        }
    }

    private boolean isMatchPiece(Piece piece, int[] currentPos) {
        Piece left;
        Piece top;
        Piece right;
        Piece bottom;
        Piece zero = new Piece(1, new int[]{0,0,0,0});

        if(currentPos[X] != 0) {
//            left = board[currentPos[X] - 1][Y];
            left = board[currentPos[Y]][currentPos[X] - 1];
        } else {
            left = zero;
        }

        if(currentPos[X] != size[X] - 1) {
//            right = board[currentPos[X] + 1][Y];
            right = board[currentPos[Y]][currentPos[X] + 1];
        } else {
            right = zero;
        }
        if(currentPos[Y] != 0) {
//            top = board[currentPos[X]][Y - 1];
            top = board[currentPos[Y] - 1][currentPos[X]];
        } else {
            top = zero;
        }
        if(currentPos[Y] != size[Y] - 1) {
//            bottom = board[currentPos[X]][Y + 1];
            bottom = board[currentPos[Y] + 1][currentPos[X]];
        } else {
            bottom = zero;
        }

        return ((left.getRight() + piece.getLeft() == 0) &&
                (top.getBottom() + piece.getTop() == 0) &&
                (right == null || right.getLeft() + piece.getRight() == 0) &&
                (bottom == null || bottom.getTop() + piece.getBottom() == 0));
    }

    private String solutionString(){
        StringBuilder returnValue = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                returnValue.append(board[i][j]).append(" ");
            }
            returnValue.append("\n");
        }
        return returnValue.toString();
    }
}
