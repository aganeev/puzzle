package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

import java.util.ArrayList;
import java.util.List;

class PuzzleValidator {
    private Report report;
    private int[][] pieces;

    PuzzleValidator(Report report, int[][] pieces) {
        this.pieces = pieces;
        this.report = report;
    }


    List<int[]> getOptions() {
        List<int[]> options = new ArrayList<>();
        /* TODO:
        All types of "Cannot solve puzzle" checks like:
        1) At least on of each type of corner element should present (<TL><TR><BL><BR>)
        2) Sum of all Top and Bottom edge values should be 0
        3) Sum of all Left and Right edge values should be 0

        each error should be added to the report like report.addErrorLine(errorString);

        According to number of pieces (pieces.size) we should understand how which puzzle size options do we have.
        Example: 5 pieces - 2 options 5x1 and 1x5
        Example: 6 pieces - 4 options 6x1, 3x2, 2x3, 1x6
        etc.

        For each option we need to check that number of straight edges is enough
        Example: For 3x2 option should be 3 pieces with Top 0 edges and else 3 pieces with Bottom 0 edges and
        at among that pieces should be 2 pieces with Left 0 edges and else 2 pieces with Right 0 edges.


        If no one option do not have enough straight edges we should add the error:
        "Cannot solve puzzle: wrong number of straight edges" and only then add all other errors with specified (in requirements) order.

        method should return List of int arrays with 2 elements, each array is option of puzzle size (for example 3x2 - new int[]{3,2})
         */




        options.add(new int[]{3,3});
        options.add(new int[]{1,9});
        return options;
    }
}
