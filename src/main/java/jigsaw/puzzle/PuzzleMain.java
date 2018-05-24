package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class PuzzleMain {

    final static String INPUT = "validInputNoSolving(1).txt";
    final static String OUTPUT = "output.txt";
    final static String PATH = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\main\\resources\\";


    public static void main(String[] args) {
        String inputPath = PATH + INPUT;
        Report report = new Report();
        Set<Piece> pieces = InputHandler.readFromFile(report, inputPath);

        for (Piece p : pieces) {
            System.out.println(p.getId() + ": " + p.getTop() + " " + p.getRight() + " " + p.getBottom() + " " + p.getLeft());
        }

        if (!pieces.isEmpty()) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            Set<int[]> options = puzzleValidator.getOptions();
            options.forEach(option -> System.out.println(Arrays.toString(option)));

            System.out.println(report.toString());
            if (!options.isEmpty()) {
                Iterator<int[]> optionsIterator = options.iterator();
                boolean isSolved = false;
                while (optionsIterator.hasNext() && !isSolved) {
                    Solver solver = new Solver(report, optionsIterator.next());
                    isSolved = solver.hasSolution();
                }
            }
        }

        String outputPath = PATH + OUTPUT;
        try {
            OutputHandler.reportToFile(report, outputPath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
