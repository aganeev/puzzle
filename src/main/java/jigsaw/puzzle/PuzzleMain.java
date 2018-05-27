package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PuzzleMain {

    // TODO: Remove before sending to Amir
    final static String INPUT = "solvable.txt";
    final static String OUTPUT = "output.txt";
    final static String PATH = "src/test/resources/";


    public static void main(String[] args) {
//        String inputPath = args[0];
//        String outputPath = args[1];

        // TODO: Remove before sending to Amir
        String inputPath = PATH + INPUT;
        String outputPath = PATH + OUTPUT;

        Report report = new Report();
        Set<Piece> pieces = InputHandler.readFromFile(report, inputPath);

        // TODO: Remove before sending to Amir
        for (Piece p : pieces) {
            System.out.println(p.getId() + ": " + p.getTop() + " " + p.getRight() + " " + p.getBottom() + " " + p.getLeft());
        }

        if (!pieces.isEmpty()) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            Set<int[]> options = puzzleValidator.getOptions();

            // TODO: Remove before sending to Amir
            options.forEach(option -> System.out.println(Arrays.toString(option)));
            System.out.println(report.toString());

            if (!options.isEmpty()) {
                Iterator<int[]> optionsIterator = options.iterator();
                String solution = "";
                while (optionsIterator.hasNext() && solution.isEmpty()) {
                    Solver solver = new Solver(pieces, optionsIterator.next());
                    solution = solver.findSolution();
                }
                if (!solution.isEmpty()) {
                    report.setText(solution);
                } else {
                    report.setText("Cannot solve puzzle: it seems that there is no proper solution");
                }
            }

        }

        OutputHandler.reportToFile(report, outputPath);
    }

}
