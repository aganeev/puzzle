package utils;

import jigsaw.puzzle.entities.Piece;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolverValidator {
    public static boolean isSolutionValid(Set<Piece> pieceSet, int[] solution) {
        Map<Integer,Piece> pieceMap = pieceSet.stream().collect(Collectors.toMap(Piece::getId, piece->piece,(a, b)->b));
        final Iterator<Integer> solutionIterator = IntStream.of(solution).boxed().iterator();
        int numberOfLines = solutionIterator.next();
        int numberOfColumns = (solution.length - 1) / numberOfLines;
        if (pieceMap.size() != solution.length - 1) {
            System.err.println("Wrong solution length");
            return false;
        }
        Piece[][] board = new Piece[numberOfLines][numberOfColumns];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = pieceMap.get(solutionIterator.next());
            }
        }

        return oneDirectionEdgesMatch(board, true) && oneDirectionEdgesMatch(board, false);
    }

    private static boolean oneDirectionEdgesMatch(Piece[][] board, boolean isLeft2Right) {
        int previousValue = 0;
        int firstLoopDestination = isLeft2Right ? board.length : board[0].length;
        int secondLoopDestination = isLeft2Right ? board[0].length : board.length;
        for (int i = 0; i < firstLoopDestination; i++) {
            for (int j = 0; j < secondLoopDestination; j++) {
                Piece currentPiece = isLeft2Right ? board[i][j] : board[j][i];
                int currentValue = isLeft2Right ? currentPiece.getLeft() : currentPiece.getTop();
                int nextPrevious = isLeft2Right ? currentPiece.getRight() : currentPiece.getBottom();
                if (previousValue + currentValue == 0) {
                    previousValue = nextPrevious;
                } else {
                    String previous = isLeft2Right ? "left" : "top";
                    System.err.format("Piece with id=%s is not match with %s piece.%n",currentPiece.getId(),previous);
                    return false;
                }
            }
            if (previousValue != 0) {
                String type = isLeft2Right ? "row" : "column";
                System.err.format("Last piece in the %s number %s is not match with border.%n", type, i+1);
                return false;
            }
        }
        return true;
    }
}
