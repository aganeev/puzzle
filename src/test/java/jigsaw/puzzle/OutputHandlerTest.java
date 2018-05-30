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
    void validInputNoSolving() throws IOException {
        String fileInput = "validInputNoSolving(1).txt";
        String INPUT_PATH = "src/test/resources/";
        String OUTPUT_PATH = "target/";
        String path = INPUT_PATH + fileInput;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        String fileOutput = "output.txt";
        String pathOutput = OUTPUT_PATH + fileOutput;
        OutputHandler outputHandler = new OutputHandler(report);
        outputHandler.reportToFile(pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("invalid Input - missing pieces")
    void invalidInputMissingPieces() throws IOException {
        String fileInput = "invalidInputMissingPieces(1).txt";
        String PATH = "src/test/resources/";
        String OUTPUT_PATH = "target/";
        String path = PATH + fileInput;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        String fileOutput = "output.txt";
        String pathOutput = OUTPUT_PATH + fileOutput;
        OutputHandler outputHandler = new OutputHandler(report);
        outputHandler.reportToFile(pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("invalid Input - wrong ID")
    void invalidInputWrongID() throws IOException {
        String fileInput = "invalidInputWrongID(1).txt";
        String PATH = "src/test/resources/";
        String OUTPUT_PATH = "target/";
        String path = PATH + fileInput;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        String fileOutput = "output.txt";
        String pathOutput = OUTPUT_PATH + fileOutput;
        OutputHandler outputHandler = new OutputHandler(report);
        outputHandler.reportToFile(pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("invalid Input - wrong piece edges")
    void invalidInputWrongPieceEdges() throws IOException {
        String fileInput = "invalidInputWrongPieceEdges(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + fileInput;
        String OUTPUT_PATH = "target/";
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        String fileOutput = "output.txt";
        String pathOutput = OUTPUT_PATH + fileOutput;
        OutputHandler outputHandler = new OutputHandler(report);
        outputHandler.reportToFile(pathOutput);
        File file = new File(pathOutput);
        assertTrue(file.exists());
    }
}