package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

class Solver {
    private Report report;

    Solver(Report report, int[] boardSize) {
        this.report = report;

    }

    boolean hasSolution() {
        //TODO: find solution for specified board size

        /* if solution is found, set solution to the report how it described in requirements:
        IDs of the elements in the structure that solves the puzzle:
        A single space between IDs on the same line, a new line for each row in the puzzle.

        report.setSucceedResult(stringVariableWithPuzzleStructure);

        and return true

        If solution not found, add appropriate error to the report: report.addErrorLine(errorString);

        and return false

         */
        report.setSucceedResult("1 2 3\n4 5 6\n7 8 9");
        return true;
    }
}
