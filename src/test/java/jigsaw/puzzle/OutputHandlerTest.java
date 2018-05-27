package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class OutputHandlerTest {

    @Test
    @DisplayName("Valid Input - not solving the puzzle")
    public void validInputNoSolving() throws IOException {
        String fileInput = "validInputNoSolving(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + fileInput;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        String fileOutput = "output.txt";
        String pathOutput = PATH + fileOutput;
        OutputHandler.reportToFile(report, pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("invalid Input - missing pieces")
    public void invalidInputMissingPieces() throws IOException {
        String fileInput = "invalidInputMissingPieces(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + fileInput;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        String fileOutput = "output.txt";
        String pathOutput = PATH + fileOutput;
        OutputHandler.reportToFile(report, pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("invalid Input - wrong ID")
    public void invalidInputWrongID() throws IOException {
        String fileInput = "invalidInputWrongID(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + fileInput;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        String fileOutput = "output.txt";
        String pathOutput = PATH + fileOutput;
        OutputHandler.reportToFile(report, pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("invalid Input - wrong piece edges")
    public void invalidInputWrongPieceEdges() throws IOException {
        String fileInput = "invalidInputWrongPieceEdges(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + fileInput;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        String fileOutput = "output.txt";
        String pathOutput = PATH + fileOutput;
        OutputHandler.reportToFile(report, pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }
}