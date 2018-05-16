package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class PuzzleMain {
    public static void main(String[] args) {
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        Report report = new Report();
        int[][] pieces = InputHandler.readFromFile(report, new File(inputFilePath));
        if (pieces.length != 0) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            List<int[]> options = puzzleValidator.getOptions();
            if (report.isEmpty()) {
                Iterator<int[]> optionsIterator = options.iterator();
                boolean isSolved = false;
                while (optionsIterator.hasNext() && !isSolved) {
                    Solver solver = new Solver(report, optionsIterator.next());
                    isSolved = solver.hasSolution();
                }
            }
        }
        OutputHandler.reportToFile(report, outputFilePath);
    }

}
