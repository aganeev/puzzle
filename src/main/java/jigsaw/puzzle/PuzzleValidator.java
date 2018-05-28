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
    
    private static final int X = 0;
    private static final int Y = 1;

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
            .filter(option->(option[X] != 1 && option[Y] != 1))
            .filter(option1 -> !hasEnoughEdges(option1)).collect(Collectors.toSet());
    }

    private boolean hasEnoughEdges(int[] option) {
        int xEdges = option[X];
        int yEdges = option[Y];

        if (straightTopSet.size() < xEdges || // if number of pieces with straight top side is less than required by X dimension of this option
                straightBottomSet.size() < xEdges || // or number of pieces with straight bottom side is less than required by X dimension of this option
                straightRightSet.size() < yEdges || // or number of pieces with straight right side is less than required by Y dimension of this option
                straightLeftSet.size() < yEdges) // or number of pieces with straight left side is less than required by Y dimension of this option
        {
            return false;
        }
        // difference between expected and real straight horizontal edges (one piece may be counted twice if it have both top and bottom straight edges)
        int xDelta = (straightTopSet.size() + straightBottomSet.size()) - xEdges * 2;
        // difference between expected and real straight vertical edges (one piece may be counted twice if it have both left and right straight edges)
        int yDelta = (straightRightSet.size() + straightLeftSet.size()) - yEdges * 2;

        // number of pieces that have both top and bottom straight edges
        int xIntersect = (int) straightTopSet.keySet().stream()
                .filter(straightBottomSet::containsKey)
                .count();

        // number of pieces that have both left and right straight edges
        int yIntersect = (int) straightLeftSet.keySet().stream()
                .filter(straightRightSet::containsKey)
                .count();

        return (xIntersect <= xDelta) && (yIntersect <= yDelta);
    }


    private Set<int[]> findImpossibleOneLineCases(Set<int[]> options) {

        return options.stream()
                .filter(option->(option[X] == 1 || option[Y] == 1)) // leave only one line OR one column cases
                .filter(option->((option[X] == 1 && option[Y] != 1) && !hasEnoughEdgesForColumn()) || // leave one column case if it doesn't pass straight edges validation
                                ((option[X] != 1 && option[Y] == 1) && !hasEnoughEdgesForLine()) || // leave one line case if it doesn't pass straight edges validation
                                ((option[X] == 1 && option[Y] == 1) && !hasSquareOnePiece()) // leave one piece case if it doesn't pass straight edges validation
                ).collect(Collectors.toSet());
    }

    private boolean hasSquareOnePiece() {
        if (pieces.size() == 1) {
            Piece piece = pieces.iterator().next();
            return piece.getLeft() == 0 && piece.getBottom() == 0 && piece.getRight() == 0 && piece.getTop() == 0;
        }
        return false; // Actually unreachable case
    }

    private boolean hasEnoughEdgesForLine() {
        boolean isOnlyOnePieceWithStraightRight = straightRightSet.size() == 1;
        boolean isOnlyOnePieceWithStraightLeft = straightLeftSet.size() == 1;
        boolean isItSame = straightLeftSet.containsKey(straightRightSet.keySet().iterator().next());

        boolean areMoreThanOneWithStraightRightAndLeft = straightRightSet.size() >= 1 && straightLeftSet.size() >= 1;

        boolean areAllPiecesWithStraightTop = pieces.stream().allMatch(piece -> piece.getTop() == 0);
        boolean areAllPiecesWithStraightBottom = pieces.stream().allMatch(piece -> piece.getBottom() == 0);

        return ((isOnlyOnePieceWithStraightLeft && isOnlyOnePieceWithStraightRight && !isItSame) || areMoreThanOneWithStraightRightAndLeft) &&
                areAllPiecesWithStraightBottom && areAllPiecesWithStraightTop;
    }

    private boolean hasEnoughEdgesForColumn() {
        boolean isOnlyOnePieceWithStraightTop = straightTopSet.size() == 1;
        boolean isOnlyOnePieceWithStraightBottom = straightBottomSet.size() == 1;
        boolean isItSame = straightTopSet.containsKey(straightBottomSet.keySet().iterator().next());

        boolean areMoreThanOneWithStraightTopAndBottom = straightTopSet.size() >= 1 && straightBottomSet.size() >= 1;

        boolean areAllPiecesWithStraightLeft = pieces.stream().allMatch(piece -> piece.getLeft() == 0);
        boolean areAllPiecesWithStraightRight = pieces.stream().allMatch(piece -> piece.getRight() == 0);

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
