package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.util.*;
import java.util.stream.Collectors;

class PuzzleValidator {
    private Report report;
    private Set<Piece> pieces;
    private Map<Integer,Piece> straightTopSet;
    private Map<Integer,Piece> straightRightSet;
    private Map<Integer,Piece> straightBottomSet;
    private Map<Integer,Piece> straightLeftSet;

    PuzzleValidator(Report report, Set<Piece> pieces) {
        this.pieces = pieces;
        this.report = report;
    }

    private void defineStraightSets() {
        straightTopSet = new HashMap<>();
        straightRightSet = new HashMap<>();
        straightBottomSet = new HashMap<>();
        straightLeftSet = new HashMap<>();
        pieces.forEach(piece -> {
            if(piece.getTop() == 0) {
                straightTopSet.put(piece.getId(), piece);
            }
            if(piece.getRight() == 0) {
                straightRightSet.put(piece.getId(), piece);
            }
            if(piece.getBottom() == 0) {
                straightBottomSet.put(piece.getId(), piece);
            }
            if(piece.getLeft() == 0) {
                straightLeftSet.put(piece.getId(), piece);
            }
        });
    }


    Set<int[]> getOptions() {
        defineStraightSets();

        Set<int[]> options = findOptions();
        Set<int[]> impossibleOptions = findImpossibleOneLineCases(options);
        impossibleOptions.addAll(findImpossibleRegularCases(options));
        options.removeAll(impossibleOptions);
        if (options.isEmpty()) {
            report.addErrorLine("Cannot solve puzzle: wrong number of straight edges");
        }
        if (!isCornerCheckPassed()){
            options.clear();
        }
        if(!isEdgesSumCheckPassed()){
            report.addErrorLine("Cannot solve puzzle: sum of edges is not zero");
            options.clear();
        }
        return options;
    }

    private boolean isEdgesSumCheckPassed() {
        return pieces.stream().mapToInt(Piece::getSumOfEdges).sum() == 0;

    }

    private boolean isCornerCheckPassed() {
        boolean isCheckPassed = true;
        if (pieces.stream().noneMatch(piece -> piece.getLeft() == 0 && piece.getTop() == 0)) {
            report.addErrorLine("Cannot solve puzzle: missing corner element: TL");
            isCheckPassed = false;
        }
        if (pieces.stream().noneMatch(piece -> piece.getTop() == 0 && piece.getRight() == 0)) {
            report.addErrorLine("Cannot solve puzzle: missing corner element: TR");
            isCheckPassed = false;
        }
        if (pieces.stream().noneMatch(piece -> piece.getRight() == 0 && piece.getBottom() == 0)) {
            report.addErrorLine("Cannot solve puzzle: missing corner element: BR");
            isCheckPassed = false;
        }
        if (pieces.stream().noneMatch(piece -> piece.getBottom() == 0 && piece.getLeft() == 0)) {
            report.addErrorLine("Cannot solve puzzle: missing corner element: BL");
            isCheckPassed = false;
        }
        return isCheckPassed;
    }

    private Collection<? extends int[]> findImpossibleRegularCases(Set<int[]> options) {
        return options.stream()
            .filter(option->(option[0] != 1 && option[1] != 1))
            .filter(option1 -> !hasEnoughEdges(option1)).collect(Collectors.toSet());
    }

    private boolean hasEnoughEdges(int[] option) {
        int xEdges = option[0];
        int yEdges = option[1];
        if (straightTopSet.size() < xEdges || straightBottomSet.size() < xEdges ||
                straightRightSet.size() < yEdges || straightLeftSet.size() < yEdges) {
            return false;
        }
        int xDelta = (straightTopSet.size() + straightBottomSet.size()) - xEdges * 2;
        int yDelta = (straightRightSet.size() + straightLeftSet.size()) - yEdges * 2;

        int xIntersect = (int) straightTopSet.keySet().stream()
                .filter(straightBottomSet::containsKey)
                .count();

        int yIntersect = (int) straightLeftSet.keySet().stream()
                .filter(straightRightSet::containsKey)
                .count();

        return xIntersect <= xDelta && yIntersect <= yDelta;
    }


    private Set<int[]> findImpossibleOneLineCases(Set<int[]> options) {
        return options.stream()
                .filter(option->(option[0] == 1 || option[1] == 1) && // filter only one line OR one column cases
                    ((option[0] == 1 && option[1] != 1) && !hasEnoughEdgesForColumn()) || // one column cases
                                ((option[0] != 1 && option[1] == 1) && !hasEnoughEdgesForLine()) || // one line cases
                                ((option[0] == 1 && option[1] == 1) && !hasSquareOnePiece()) // one piece case
                ).collect(Collectors.toSet());
    }

    private boolean hasSquareOnePiece() {
        if (pieces.size() == 1) {
            Piece piece = pieces.iterator().next();
            return piece.getLeft() == 0 && piece.getBottom() == 0 && piece.getRight() == 0 && piece.getTop() == 0;
        }
        return false;
    }

    private boolean hasEnoughEdgesForLine() {
        boolean areMoreThanOneWithStraightRightAndLeft = straightRightSet.size() >= 1 && straightLeftSet.size() >= 1;
        boolean isOnlyOnePieceWithStraightRight = straightRightSet.size() == 1;
        boolean isOnlyOnePieceWithStraightLeft = straightLeftSet.size() == 1;
        boolean isItSame = straightLeftSet.containsKey(straightRightSet.keySet().iterator().next());

        boolean areAllPiecesWithStraightTop = pieces.stream().allMatch(piece -> straightTopSet.containsKey(piece.getId()));
        boolean areAllPiecesWithStraightBottom = pieces.stream().allMatch(piece -> straightBottomSet.containsKey(piece.getId()));

        return ((isOnlyOnePieceWithStraightLeft && isOnlyOnePieceWithStraightRight && !isItSame) || areMoreThanOneWithStraightRightAndLeft) &&
                areAllPiecesWithStraightBottom && areAllPiecesWithStraightTop;
    }

    private boolean hasEnoughEdgesForColumn() {
        boolean areMoreThanOneWithStraightTopAndBottom = straightTopSet.size() >= 1 && straightBottomSet.size() >= 1;
        boolean isOnlyOnePieceWithStraightTop = straightTopSet.size() == 1;
        boolean isOnlyOnePieceWithStraightBottom = straightBottomSet.size() == 1;
        boolean isItSame = straightTopSet.containsKey(straightBottomSet.keySet().iterator().next());

        boolean areAllPiecesWithStraightLeft = pieces.stream().allMatch(piece -> straightLeftSet.containsKey(piece.getId()));
        boolean areAllPiecesWithStraightRight = pieces.stream().allMatch(piece -> straightRightSet.containsKey(piece.getId()));

        return ((isOnlyOnePieceWithStraightBottom && isOnlyOnePieceWithStraightTop && !isItSame) || areMoreThanOneWithStraightTopAndBottom) &&
                areAllPiecesWithStraightRight && areAllPiecesWithStraightLeft;
    }


    private Set<int[]> findOptions() {
        Set<int[]> options = new HashSet<>();
        for (int i = 1; i <= pieces.size(); i++) {
            if (pieces.size() % i == 0) {
                options.add(new int[]{i, pieces.size()/i});
            }
        }
        return options;
    }
}
