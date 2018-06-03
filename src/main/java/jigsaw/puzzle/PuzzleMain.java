package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PuzzleMain {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();
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
            System.out.println("All side options:");
            options.forEach(option-> System.out.println(Arrays.toString(option)));
            if (!report.hasErrors() && !options.isEmpty()) {
                Solver solver = new Solver(report, pieces);
                ForkJoinPool myPool = new ForkJoinPool(options.size());
                AtomicInteger finishedCounter = new AtomicInteger(0);
                options.forEach(option->myPool.execute(()-> {
                    System.out.format("Current being handled option: %s by thread [%s]%n",Arrays.toString(option), Thread.currentThread().getName());
                    solver.findMultiThreadedSolution(option);
                    finishedCounter.incrementAndGet();
                    synchronizedNotify();
                }));
                while (!report.hasSolution() && finishedCounter.get() != options.size()) {
                    synchronized (this) {
                        wait();
                    }
                }
                if (report.hasSolution()) {
                    myPool.shutdownNow();
                } else {
                    report.addErrorLine("Cannot solve puzzle: it seems that there is no proper solution");
                }
            }
        }
        System.out.println("Done");
        outputHandler.reportToFile(outputPath);

    }

    private synchronized void synchronizedNotify() {
        notifyAll();
    }

}
