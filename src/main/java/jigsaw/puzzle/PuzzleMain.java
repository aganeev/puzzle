package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PuzzleMain {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        String inputPath = args[0];
        String outputPath = args[1];

        PuzzleMain puzzleMain = new PuzzleMain();
        puzzleMain.doWork(inputPath, outputPath);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Elapsed time: " + TimeUnit.NANOSECONDS.toSeconds(estimatedTime) + " seconds");

    }



    private void doWork(String inputPath, String outputPath) {
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        OutputHandler outputHandler = new OutputHandler(report);
        Set<Piece> pieces = inputHandler.readFromFile(inputPath);

        if (!report.hasErrors() && !pieces.isEmpty()) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            Set<int[]> options = puzzleValidator.getOptions();
            System.out.println("All side options:");
            options.forEach(option-> System.out.println(Arrays.toString(option)));
            if (!report.hasErrors() && !options.isEmpty()) {
                Solver solver = new Solver(report, pieces);
                if (options.stream()
                        .peek(option-> System.out.println("Current being handled option: " + Arrays.toString(option)))
                        .noneMatch(solver::hasSolution)) {
                    report.addErrorLine("Cannot solve puzzle: it seems that there is no proper solution");
                }
            }
        }
        System.out.println("Done");
        outputHandler.reportToFile(outputPath);
    }

}
