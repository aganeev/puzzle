package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.*;

public class PuzzleMain {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();
        if (args.length < 2) {
            System.err.println("Error: Not enough parameters are specified. Expected 2 (input file and output file)");
            System.exit(1);
        }
        String inputPath = args[0];
        String outputPath = args[1];

        PuzzleMain puzzleMain = new PuzzleMain();
        puzzleMain.doWork(inputPath, outputPath);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Elapsed time: " + TimeUnit.NANOSECONDS.toSeconds(estimatedTime) + " seconds");
    }

    private void doWork(String inputPath, String outputPath) throws InterruptedException {
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        OutputHandler outputHandler = new OutputHandler(report);
        Set<Piece> pieces = inputHandler.readFromFile(inputPath);

        if (!report.hasErrors() && !pieces.isEmpty()) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            Set<int[]> options = puzzleValidator.getOptions();
            if (!report.hasErrors() && !options.isEmpty()) {
                ForkJoinPool myPool = new ForkJoinPool(options.size());
                options.forEach(option->myPool.execute(()-> {
                    Solver solver = new Solver(report, pieces);
                    System.out.format("Current being handled option: %s by thread [%s]%n",Arrays.toString(option), Thread.currentThread().getName());
                    solver.findSolution(option);
                    synchronizedNotify();
                }));
                myPool.shutdown();
                synchronizedWait(report, myPool);
                myPool.shutdownNow();
                if (!report.hasSolution()) {
                    report.addErrorLine("Cannot solve puzzle: it seems that there is no proper solution");
                }
            }
        }
        System.out.println("Done");
        outputHandler.reportToFile(outputPath);

    }

    private synchronized void synchronizedWait(Report report, ForkJoinPool pool) throws InterruptedException {
        do  {
            wait(1000);
        } while (!report.hasSolution() && !pool.isTerminated());
    }

    private synchronized void synchronizedNotify() {
        notifyAll();
    }

}
